package airportsimulation;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Air Traffic Control acts as the primary Monitor object.
 * It keeps track of how many planes are here and decides who gets to land or take off.
 */
public class ATC {
    private int planesOnGround = 0;
    private boolean emergencyIncoming = false;
    private boolean landingSequenceActive = false; 
    
    // First-Come-First-Serve queue to ensure fair thread scheduling
    private final Queue<String> waitingQueue = new LinkedList<>();
    private final Gate[] gates; 
    
    private int totalPlanesServed = 0;
    private int totalPassengersBoarded = 0;
    private long maxWaitTime = 0;
    private long minWaitTime = Long.MAX_VALUE;
    private long totalWaitTime = 0;
    
    public ATC(Gate[] gates) {
        this.gates = gates;
    }
    
    /**
     * Synchronized method: Only one plane can ask to land at a time.
     */
    public synchronized Gate requestLanding(String planeName, boolean isEmergency){
        System.out.println(planeName + ": Requesting Landing.");
        String lastDenyReason = "";
        
        if (isEmergency) {
            emergencyIncoming = true;
            
            // Forces thread into WAITING state if the airport is full or the runway is busy
            while (planesOnGround >= 3 || landingSequenceActive) {
                String currentReason;
                if (planesOnGround >= 3) {
                    currentReason = "Airport Full";
                } else {
                    currentReason = "Runway currently in use";
                }
                
                if (!lastDenyReason.equals(currentReason)) {
                    System.out.println("ATC: Emergency Landing delayed for " + planeName + ". Reason: " + currentReason + ".");
                    lastDenyReason = currentReason;
                }
                try {
                    wait();
                } catch (InterruptedException e) {
                    System.out.println("Main simulation thread was interrupted.");
                }
            }
            planesOnGround++;
            landingSequenceActive = true; 
            emergencyIncoming = false; 
            System.out.println("ATC: ***EMERGENCY Landing granted for " + planeName + ".***");
        }
        else{
            waitingQueue.offer(planeName);
            
            // Normal planes wait for sufficient capacity, no emergency, free runway and their turn in the queue
            while (emergencyIncoming || planesOnGround >= 3 || !waitingQueue.peek().equals(planeName) || landingSequenceActive) {
                String currentReason;
                if (planesOnGround >= 3) {
                    currentReason = "Airport Full";
                } else if (emergencyIncoming) {
                    currentReason = "Airspace reserved for emergency";
                } else if (landingSequenceActive) {
                    currentReason = "Runway currently in use";
                } else {
                    currentReason = "Another plane is ahead in the queue";
                }
                
                if (!lastDenyReason.equals(currentReason)) {
                    System.out.println("ATC: Landing Permission Denied for " + planeName + ". Reason: " + currentReason + ".");
                    lastDenyReason = currentReason;
                }
                try {
                    wait();
                } catch (InterruptedException e) {
                    System.out.println("Main simulation thread was interrupted.");
                }
            }
            waitingQueue.poll(); 
            planesOnGround++;
            landingSequenceActive = true; 
            System.out.println("ATC: Landing permission granted for " + planeName + ".");
        }
        
        // Gate allocation
        Gate assignedGate = null;
        for (Gate g : gates) {
            if(g.allocateGate()) {
                assignedGate = g;
                break;
            }
        }
        System.out.println("ATC: " + assignedGate.getGateName() + " assigned for " + planeName + ".");
        return assignedGate;
    }
    
    /**
     * Signals that the landing sequence is complete, notifying waiting threads.
     */
    public synchronized void reportDocked(){
        landingSequenceActive = false; 
        notifyAll(); 
    }
    
    /**
     * Prevents takeoff if another thread is currently holding the runway lock for landing.
     */
    public synchronized void requestTakeoff(String planeName){
        System.out.println(planeName + ": Requesting Taking off.");
        boolean printedWait = false;
        
        while(landingSequenceActive){
            if(!printedWait){
                System.out.println("ATC: Takeoff delayed for " + planeName + ". Reason: Runway currently in use.");
                printedWait = true;
            }
            try {
                wait();
            }
            catch (InterruptedException e) {
                System.out.println("Main simulation thread was interrupted.");
            }
        }
        System.out.println("ATC: Taking-off is granted for " + planeName + ". Runway is free.");
    }
    
    /**
     * Updates capacity and notifies all waiting threads in the sky queue.
     */
    public synchronized void leaveAirport(){
        planesOnGround--;
        totalPlanesServed++;
        notifyAll(); 
    }
    
    public synchronized void recordStatistics(int passengers, long waitTime){
        totalPassengersBoarded += passengers;
        totalWaitTime += waitTime;
        if(waitTime > maxWaitTime){
            maxWaitTime = waitTime;
        }
        if(waitTime < minWaitTime){
            minWaitTime = waitTime;
        }
    }
    
    public void printSanityCheck(Gate[] gates){
        System.out.println("\n ========= ATC Statistics Report ========= \n");
        System.out.println("Sanity Check: Verifying gates are empty...");
        for(Gate gate:gates){
            System.out.println(gate.getGateName() + " Empty: " + gate.verifyEmpty());
        }
        long avgWaitTime;
        if (totalPlanesServed > 0) {
            avgWaitTime = totalWaitTime / totalPlanesServed;
        } else {
            avgWaitTime = 0;
        }
        System.out.println("Average Wait Time: " + avgWaitTime + " ms");
        System.out.println("Total Planes Served: " + totalPlanesServed);
        System.out.println("Total Passengers Boarded: " + totalPassengersBoarded);
        System.out.println("===============================================\n");
    }
    
    public boolean isEmergencyIncoming(){
        return emergencyIncoming;
    }
    
    public boolean isAirportFull(){
        return planesOnGround >= 3;
    }
}