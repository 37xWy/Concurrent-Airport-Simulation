package airportsimulation;

/**
 * Runnable task that acts as a client requesting access to the shared RefuelingTruck resource.
 */
public class RefuelingTask implements Runnable {
    private final String planeName;
    private final RefuelingTruck truck;

    public RefuelingTask(String planeName, RefuelingTruck truck) {
        this.planeName = planeName;
        this.truck = truck;
    }

    @Override
    public void run() {
        truck.refuel(planeName);
    }
}