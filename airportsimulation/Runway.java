package airportsimulation;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Represents the physical runway as a mutually exclusive shared resource.
 */
public class Runway {
    // Shared static lock object to enforce mutual exclusion across all instances
    public static final Object RUNWAY_LOCK = new Object();
    private final Queue<String> takeoffQueue = new LinkedList<>();

    public void land(String planeName) {
        // Synchronized block to prevent concurrent runway access
        synchronized(RUNWAY_LOCK) {
            System.out.println(planeName + ": Landing.");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println(planeName + "'s runway operation was interrupted.");
            }
            System.out.println(planeName + ": Landed.");
            
            // Wakes up any threads holding for emergency landings
            RUNWAY_LOCK.notifyAll(); 
        }
    }

    public void takeOff(String planeName, ATC atc){
        synchronized(RUNWAY_LOCK){
            takeoffQueue.offer(planeName);
            
            // Yields runway if an emergency is incoming
            while((atc.isEmergencyIncoming() && !atc.isAirportFull()) || !takeoffQueue.peek().equals(planeName)){
                try {
                    RUNWAY_LOCK.wait();
                } catch (InterruptedException e) {
                    System.out.println(planeName + "'s runway operation was interrupted.");
                }
            }
            takeoffQueue.poll(); 

            System.out.println(planeName + ": Taking off.");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println(planeName + "'s runway operation was interrupted.");
            }
            
            atc.leaveAirport();
            RUNWAY_LOCK.notifyAll();
        }
    }
}