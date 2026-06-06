# ✈️ Asia Pacific Airport: Concurrent Java Simulation

A multi-threaded Java application simulating the complex, real-time coordination of a busy airport to ensure safe aircraft arrivals, ground services, and departures. The system implements robust concurrency patterns and thread-safety mechanisms to manage shared resources—such as runways, terminal gates, and refueling trucks—preventing race conditions and deadlocks under heavy traffic.

## 🚀 Features

* **Air Traffic Control (ATC) Monitor:** Acts as the central coordinator, limiting airport capacity to a strict maximum of 3 planes on the ground. It utilizes Guarded Blocks (`wait()` and `notifyAll()`) to safely suspend incoming flights and manage emergency landing overrides.
* **Runway Mutual Exclusion:** Enforces strict one-at-a-time access for landing and takeoff operations. It protects the physical runway using explicit `synchronized` lock objects and state checks.
* **Parallel Ground Operations:** Once docked, each aircraft spawns independent `Runnable` sub-threads to handle passenger boarding, interior cleaning, and refueling simultaneously. A `.join()` synchronization barrier ensures the plane does not request takeoff until all ground tasks are complete.
* **Fair Thread Scheduling:** Implements FIFO `Queue` structures within the ATC and Refueling Truck resources to ensure threads (planes) are serviced sequentially and fairly when waiting for airspace or fuel.

## 🏗️ System Architecture

The simulation is built on a custom, heavily multi-threaded architecture:
* **The Main Thread:** Initializes the environment, instantiates the shared monitors (ATC, Runway, Gates, RefuelingTruck), and spawns independent `Plane` threads with randomized arrival intervals.
* **Plane Threads (`Thread`):** Autonomous entities that navigate their complete lifecycle: requesting landing, coasting, docking, dispatching ground crews, and requesting takeoff. 
* **Ground Crew Threads (`Runnable`):** `PassengerTask`, `CleaningTask`, and `RefuelingTask` run concurrently per plane, competing for shared service resources.
* **Shared Monitors:** `ATC`, `Runway`, `Gate`, and `RefuelingTruck` manage their own encapsulated states, utilizing Intrinsic Locks and Method Synchronization to guarantee thread-safe state mutations.

## ⚙️ Prerequisites

* Java Development Kit (JDK) 8 or higher installed on your system.
* A terminal or command prompt.

## 💻 How to Run

**Option 1: Using an IDE (Recommended)**
1. Clone or download this repository to your local machine.
2. Open the project folder in your preferred Java IDE (e.g., IntelliJ IDEA, Eclipse, VS Code, NetBeans).
3. Locate `Main.java` inside the `airportsimulation` package and run it directly.

**Option 2: Using the Terminal / Command Line**
1. Clone or download this repository.
2. Open your terminal and navigate to the root directory (the folder containing the `airportsimulation` folder).
3. Compile all the Java files together using the wildcard command:
   ```bash
   javac airportsimulation/*.java
