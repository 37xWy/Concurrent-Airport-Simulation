package airportsimulation;

import java.util.Random;

/**
 * Main setup file for the Asia Pacific Airport Simulation.
 * Initializes shared resources and manages the instantiation of Plane threads.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=== ASIA PACIFIC AIRPORT SIMULATION STARTING ===\n");

        // Initialize shared resources
        Gate[] gates = new Gate[3];
        for (int i = 0; i < 3; i++) {
            gates[i] = new Gate("Gate-" + (i + 1));
        }
        
        ATC atc = new ATC(gates);
        Runway runway = new Runway();
        RefuelingTruck truck = new RefuelingTruck();

        Plane[] planes = new Plane[6]; 
        Random rand = new Random();

        // Create and start the plane threads
        for (int i = 0; i < 6; i++) {

            String planeName = "Plane-" + (i + 1);
            boolean isEmergency = (i == 4); 
            
            planes[i] = new Plane(planeName, atc, runway, truck, isEmergency);
            planes[i].start(); // Transitions thread to RUNNABLE state

            try {
                // Random Number Generator to emulate 0, 1, or 2 seconds arrival time requirement
                Thread.sleep(rand.nextInt(3) * 1000); 
            } catch (InterruptedException e) {
                System.out.println("Main thread interrupted.");
            }
        }

        // Thread Synchronization: Main thread waits for all Plane threads to finish their jobs 
        for (int i = 0; i < 6; i++) {
            try {
                if (planes[i] != null){
                    planes[i].join(); 
                }
            } catch (InterruptedException e) {
                System.out.println("Main thread interrupted while waiting for planes.");
            }
        }

        System.out.println("\n=== ALL PLANES HAVE DEPARTED ===");
        atc.printSanityCheck(gates);
    }
}