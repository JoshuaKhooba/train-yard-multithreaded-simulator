/*
Name: Joshua Khooba
Course: CNT 4714 Spring 2026
Assignment title: Project 2 – Multi-threaded programming in Java
Date: February 15, 2026
Class: Train
Description: Represents a train attempting to move through the switch yard. Each train
must acquire all required switches in order before it can traverse the yard. Implements
Runnable to allow concurrent execution via ExecutorService.
*/

import java.util.Random;

public class Train implements Runnable {
    private int trainNumber;
    private int inboundTrack;
    private int outboundTrack;
    private RouteConfiguration route;
    private Switch[] switches;
    private boolean dispatched;
    private boolean onHold;
    private int dispatchSequence;
    private Random random;
    
    public Train(int trainNumber, int inboundTrack, int outboundTrack, 
                 RouteConfiguration route, Switch[] switches) {
        this.trainNumber = trainNumber;
        this.inboundTrack = inboundTrack;
        this.outboundTrack = outboundTrack;
        this.route = route;
        this.switches = switches;
        this.dispatched = false;
        this.onHold = (route == null);
        this.dispatchSequence = 0;
        this.random = new Random();
    }
    
    @Override
    public void run() {
        if (onHold) {
            System.out.println("*************");
            System.out.println("Train " + trainNumber + " is on permanent hold and cannot be dispatched.");
            System.out.println("*************");
            return;
        }
        
        boolean success = false;
        while (!success && !dispatched) {
            success = attemptToAcquireSwitches();
            if (!success) {
                try {
                    // Wait random time between 100-500ms before trying again
                    Thread.sleep(100 + random.nextInt(401));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
        
        if (success) {
            movethroughYard();
            releaseSwitches();
        }
    }
    
    private boolean attemptToAcquireSwitches() {
        Switch sw1 = switches[route.getSwitch1() - 1];
        Switch sw2 = switches[route.getSwitch2() - 1];
        Switch sw3 = switches[route.getSwitch3() - 1];
        
        // Try to acquire first switch
        if (!sw1.tryLock()) {
            System.out.println("Train " + trainNumber + ": UNABLE TO LOCK first required switch: Switch " + 
                             route.getSwitch1() + ". Train will wait...");
            return false;
        }
        System.out.println("Train " + trainNumber + ": LOCKS first required Switch " + route.getSwitch1() + ".");
        
        // Try to acquire second switch
        if (!sw2.tryLock()) {
            System.out.println("Train " + trainNumber + ": UNABLE TO LOCK second required switch: Switch " + 
                             route.getSwitch2() + ".");
            System.out.println("Train " + trainNumber + ": Releasing lock on first required switch: Switch " + 
                             route.getSwitch1() + ". Train will wait...");
            sw1.unlock();
            return false;
        }
        System.out.println("Train " + trainNumber + ": LOCKS second required Switch " + route.getSwitch2() + ".");
        
        // Try to acquire third switch
        if (!sw3.tryLock()) {
            System.out.println("Train " + trainNumber + ": UNABLE TO LOCK third required switch: Switch " + 
                             route.getSwitch3() + ".");
            System.out.println("Train " + trainNumber + ": Releasing locks on first and second required switches: Switch " + 
                             route.getSwitch1() + " and Switch " + route.getSwitch2() + ". Train will wait...");
            sw2.unlock();
            sw1.unlock();
            return false;
        }
        System.out.println("Train " + trainNumber + ": LOCKS third required Switch " + route.getSwitch3() + ".");
        
        System.out.println("Train " + trainNumber + ": HOLDS ALL NEEDED SWITCH LOCKS - Train movement begins.");
        return true;
    }
    
    private void movethroughYard() {
        try {
            // Simulate time to move through yard (500-1500ms)
            Thread.sleep(500 + random.nextInt(1001));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private void releaseSwitches() {
        System.out.println("Train " + trainNumber + ": Clear of yard control.");
        System.out.println("Train " + trainNumber + ": Releasing all switch locks.");
        
        Switch sw1 = switches[route.getSwitch1() - 1];
        Switch sw2 = switches[route.getSwitch2() - 1];
        Switch sw3 = switches[route.getSwitch3() - 1];
        
        System.out.println("Train " + trainNumber + ": Unlocks/releases lock on Switch " + route.getSwitch1() + ".");
        sw1.unlock();
        
        System.out.println("Train " + trainNumber + ": Unlocks/releases lock on Switch " + route.getSwitch2() + ".");
        sw2.unlock();
        
        System.out.println("Train " + trainNumber + ": Unlocks/releases lock on Switch " + route.getSwitch3() + ".");
        sw3.unlock();
        
        System.out.println("Train " + trainNumber + ": Has been dispatched and moves on down the line out of yard control into CTC.");
        System.out.println("@ @ @ TRAIN " + trainNumber + ": DISPATCHED @ @ @");
        System.out.println();
        
        dispatched = true;
    }
    
    public int getTrainNumber() {
        return trainNumber;
    }
    
    public int getInboundTrack() {
        return inboundTrack;
    }
    
    public int getOutboundTrack() {
        return outboundTrack;
    }
    
    public boolean isDispatched() {
        return dispatched;
    }
    
    public boolean isOnHold() {
        return onHold;
    }
    
    public void setDispatchSequence(int sequence) {
        this.dispatchSequence = sequence;
    }
    
    public int getDispatchSequence() {
        return dispatchSequence;
    }
    
    public RouteConfiguration getRoute() {
        return route;
    }
}
