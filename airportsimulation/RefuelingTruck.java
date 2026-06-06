package airportsimulation;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Shared resource simulating a single refueling truck.
 * Utilizes a wait/notify mechanism and an explicit FIFO queue to enforce fair thread scheduling.
 */
public class RefuelingTruck {
    
    private boolean inUse = false;
    private final Queue<String> truckQueue = new LinkedList<>();

    public void refuel(String planeName) {
        
        // Checking and acquiring the resource lock
        synchronized(this) {
            truckQueue.offer(planeName);
            
            if (inUse || !truckQueue.peek().equals(planeName)) {
                System.out.println(planeName + ": Waiting for the refueling truck. Truck is busy or another plane is ahead.");
            }
            
            // Thread waits until the truck is released & it is their turn in the queue
            while (inUse || !truckQueue.peek().equals(planeName)) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    System.out.println("Refueling truck was interrupted while queueing " + planeName + ".");
                }
            }
            truckQueue.poll();
            inUse = true; 
        }

        // Execution outside the synchronized block to maintain concurrent airport operations
        System.out.println(planeName + ": Started using the refueling truck to refuel.");
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            System.out.println("Refueling truck was interrupted while servicing " + planeName + ".");
        }
        System.out.println(planeName + ": Finished refueling. Truck is now available.");

        // Releasing the resource and notifying the wait pool
        synchronized(this) {
            inUse = false; 
            notifyAll();
        }
    }
}