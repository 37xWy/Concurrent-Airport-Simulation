package airportsimulation;

import java.util.Random;

/**
 * The main Plane thread.
 * Follows the sequence from arriving to takeoff while following ATC coordination and parallel execution of ground tasks.
 */
public class Plane extends Thread{
    
    private final ATC atc;
    private final Runway runway;
    private final RefuelingTruck truck;
    private final String planeName;
    private final boolean isEmergency;
    private final int passengerCount;
    
    public Plane(String planeName, ATC atc, Runway runway, RefuelingTruck truck, boolean isEmergency){
        this.planeName = planeName;
        this.atc = atc;
        this.runway = runway;
        this.truck = truck;
        this.isEmergency = isEmergency;
        this.passengerCount = new Random().nextInt(50) + 1;
        this.setName(planeName);
    }
    
    @Override
    public void run(){
        long arrivalTime = System.currentTimeMillis();
        
        Gate assignedGate = atc.requestLanding(planeName, isEmergency);
        long waitTime = System.currentTimeMillis() - arrivalTime;
        
        runway.land(planeName);
        
        System.out.println(planeName + ": Coasting to " + assignedGate.getGateName() + ".");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            System.out.println(planeName + " was interrupted during coasting.");
        }
        System.out.println(planeName + ": Docked at " + assignedGate.getGateName() + ".");
        
        atc.reportDocked();
        
        // Start the ground jobs at the exact same time
        Thread passengerThread = new Thread(new PassengerTask(planeName, passengerCount));
        Thread cleaningThread = new Thread(new CleaningTask(planeName));
        Thread refuelingThread = new Thread(new RefuelingTask(planeName, truck));
        
        passengerThread.start();
        cleaningThread.start();
        refuelingThread.start();
        
        try {
            // Thread Synchronization: Main Plane thread waits until sub-threads (ground tasks) complete
            passengerThread.join();
            cleaningThread.join();
            refuelingThread.join();
        } catch(InterruptedException e){
            System.out.println(planeName + " was interrupted during ground tasks.");
        }
        
        atc.requestTakeoff(planeName);
        
        assignedGate.freeGate();
        runway.takeOff(planeName, atc);
        atc.recordStatistics(passengerCount, waitTime);
    }
}