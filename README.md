# Train Yard Simulator — Multi-Threaded Java Simulation

**Course:** CNT 4714 – Enterprise Computing | Spring 2026  
**Author:** Joshua Khooba  
**Language:** Java (Concurrency / Multi-threading)

---

## Overview

The Train Yard Simulator is a multi-threaded Java application that models concurrent train dispatching through a shared yard with a limited number of track switches. Each train runs as an independent thread, competing for access to switches using synchronization to prevent conflicts. The simulation reads train and yard configuration from CSV files and manages execution via a fixed-size thread pool.

Key features:
- Each train runs as its own thread
- Track switches are shared, synchronized resources — only one train can hold a switch at a time
- Thread pool management via `ExecutorService`
- Configurable fleet size (up to 30 trains) and yard layout (up to 10 switches, 60 route configurations)
- Input-driven simulation from CSV config files

---

## Project Structure

```
Project 2/
├── Source Folder/
│   ├── TrainYardSimulator.java   # Main driver — loads config, manages thread pool
│   ├── Train.java                # Runnable thread representing each train
│   ├── Switch.java               # Synchronized shared resource (track switch)
│   ├── RouteConfiguration.java   # Models a yard routing configuration
│   ├── theFleetFile.csv          # Train definitions
│   └── theYardFile.csv           # Yard/switch configurations
```

---

## Requirements

- Java JDK 8 or higher
- No external libraries required — uses standard Java SE (`java.util.concurrent`)

---

## How to Download

```bash
git clone https://github.com/joshuakhooba/train-yard-multithreaded-simulator.git
cd train-yard-multithreaded-simulator
```

---

## How to Compile & Run

1. Navigate to the source folder:
   ```bash
   cd "Source Folder"
   ```

2. Compile all Java files:
   ```bash
   javac *.java
   ```

3. Run the simulator (config files must be in the same directory):
   ```bash
   java TrainYardSimulator
   ```

---

## How It Works

1. `TrainYardSimulator` reads `theFleetFile.csv` to build a list of trains and `theYardFile.csv` to load route configurations.
2. A fixed thread pool is created and each `Train` is submitted as a `Runnable` task.
3. As trains execute, they attempt to acquire `Switch` locks along their route. A switch can only be held by one train at a time — others wait.
4. Once a train completes its route, it releases all switch locks and the thread returns to the pool.
5. The simulation ends when all trains have completed their routes.

---

## Configuration Files

### `theFleetFile.csv`
Defines the trains in the simulation. Each row specifies a train ID and its route as a sequence of switch numbers.

### `theYardFile.csv`
Defines the yard layout — maps switch numbers to physical track positions and available routes.

> Modify these files to change the number of trains, their routes, or the yard topology.
