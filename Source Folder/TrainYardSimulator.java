/*
Name: Joshua Khooba
Course: CNT 4714 Spring 2026
Assignment title: Project 2 – Multi-threaded programming in Java
Date: February 15, 2026
Class: TrainYardSimulator
Description: Main driver class for the train yard simulation. Reads configuration files,
creates trains and switches, and manages the simulation using an ExecutorService with a
fixed thread pool.
*/

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class TrainYardSimulator {
    private static final int MAX_TRAINS = 30;
    private static final int MAX_SWITCHES = 10;
    private static final int MAX_CONFIGURATIONS = 60;
    
    private List<Train> trains;
    private List<RouteConfiguration> yardConfigurations;
    private Switch[] switches;
    private ExecutorService executor;
    private int dispatchCounter;
    
    public TrainYardSimulator() {
        trains = new ArrayList<>();
        yardConfigurations = new ArrayList<>();
        switches = new Switch[MAX_SWITCHES];
        dispatchCounter = 0;
        
        // Initialize all possible switches
        for (int i = 0; i < MAX_SWITCHES; i++) {
            switches[i] = new Switch(i + 1);
        }
    }
    
    public void loadYardConfiguration(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        
        System.out.println("Loading yard configuration from " + filename + "...");
        
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;
            
            // Remove parentheses if present
            line = line.replaceAll("[()]", "");
            String[] parts = line.split(",");
            
            if (parts.length == 5) {
                int inbound = Integer.parseInt(parts[0].trim());
                int sw1 = Integer.parseInt(parts[1].trim());
                int sw2 = Integer.parseInt(parts[2].trim());
                int sw3 = Integer.parseInt(parts[3].trim());
                int outbound = Integer.parseInt(parts[4].trim());
                
                RouteConfiguration config = new RouteConfiguration(inbound, sw1, sw2, sw3, outbound);
                yardConfigurations.add(config);
            }
        }
        reader.close();
        
        System.out.println("Loaded " + yardConfigurations.size() + " yard configurations.");
        System.out.println();
    }
    
    public void loadFleet(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        
        System.out.println("Loading fleet from " + filename + "...");
        
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;
            
            // Remove parentheses if present
            line = line.replaceAll("[()]", "");
            String[] parts = line.split(",");
            
            if (parts.length == 3) {
                int trainNum = Integer.parseInt(parts[0].trim());
                int inbound = Integer.parseInt(parts[1].trim());
                int outbound = Integer.parseInt(parts[2].trim());
                
                // Find matching route configuration
                RouteConfiguration route = findRoute(inbound, outbound);
                
                Train train = new Train(trainNum, inbound, outbound, route, switches);
                trains.add(train);
            }
        }
        reader.close();
        
        System.out.println("Loaded " + trains.size() + " trains.");
        System.out.println();
    }
    
    private RouteConfiguration findRoute(int inbound, int outbound) {
        for (RouteConfiguration config : yardConfigurations) {
            if (config.matches(inbound, outbound)) {
                return config;
            }
        }
        return null; // No valid route found - train will be on hold
    }
    
    public void runSimulation() {
        System.out.println("$ $ $ TRAIN MOVEMENT SIMULATION BEGINS............ $ $ $");
        System.out.println();
        
        executor = Executors.newFixedThreadPool(MAX_TRAINS);
        
        // Submit all trains to executor
        for (Train train : trains) {
            executor.submit(train);
        }
        
        // Shutdown executor and wait for all trains to complete
        executor.shutdown();
        
        try {
            // Wait for all trains to be dispatched or determined to be on hold
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            System.err.println("Simulation interrupted!");
        }
        
        System.out.println();
        System.out.println("$ $ $ SIMULATION ENDS $ $ $");
        System.out.println();
    }
    
    public void printFinalStatus() {
        System.out.println("============================================================");
        System.out.println("FINAL STATUS OF ALL TRAINS IN SIMULATION");
        System.out.println("============================================================");
        System.out.println();
        
        // Sort trains by train number for organized output
        Collections.sort(trains, (t1, t2) -> Integer.compare(t1.getTrainNumber(), t2.getTrainNumber()));
        
        int assignedCount = 0;
        for (Train train : trains) {
            if (!train.isOnHold() && train.isDispatched()) {
                assignedCount++;
            }
        }
        
        int sequence = 1;
        for (Train train : trains) {
            if (!train.isOnHold() && train.isDispatched()) {
                train.setDispatchSequence(sequence++);
            }
        }
        
        for (Train train : trains) {
            System.out.println("Train Number: " + train.getTrainNumber() + " assigned.");
            System.out.printf("%-15s %-15s %-10s %-10s %-10s %-10s %-12s %-18s%n",
                "Train Number", "Inbound Track", "Outbound Track", "Switch 1", "Switch 2", "Switch 3", 
                "Hold", "Dispatched", "Dispatch Sequence");
            System.out.println("---------------------------------------------------------------------------------------------------");
            
            if (train.isOnHold()) {
                System.out.printf("%-15d %-15d %-15d %-10d %-10d %-10d %-12s %-18s %-18s%n",
                    train.getTrainNumber(),
                    train.getInboundTrack(),
                    train.getOutboundTrack(),
                    0, 0, 0,
                    "true",
                    "false",
                    "0");
            } else {
                RouteConfiguration route = train.getRoute();
                System.out.printf("%-15d %-15d %-15d %-10d %-10d %-10d %-12s %-18s %-18s%n",
                    train.getTrainNumber(),
                    train.getInboundTrack(),
                    train.getOutboundTrack(),
                    route.getSwitch1(),
                    route.getSwitch2(),
                    route.getSwitch3(),
                    "false",
                    train.isDispatched() ? "true" : "false",
                    train.getDispatchSequence());
            }
            System.out.println();
        }
    }
    
    public static void main(String[] args) {
        TrainYardSimulator simulator = new TrainYardSimulator();
        
        try {
            // Load configuration files
            simulator.loadYardConfiguration("theYardFile.csv");
            simulator.loadFleet("theFleetFile.csv");
            
            // Run the simulation
            simulator.runSimulation();
            
            // Print final status
            simulator.printFinalStatus();
            
        } catch (FileNotFoundException e) {
            System.err.println("Error: Configuration file not found - " + e.getMessage());
            System.err.println("Please ensure theYardFile.csv and theFleetFile.csv are in the current directory.");
        } catch (IOException e) {
            System.err.println("Error reading configuration files: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
