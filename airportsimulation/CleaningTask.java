package airportsimulation;

/**
 * Runnable task simulating aircraft interior cleaning and resupply.
 */
public class CleaningTask implements Runnable {
    private final String planeName;

    public CleaningTask(String planeName) {
        this.planeName = planeName;
    }

    @Override
    public void run() {
        System.out.println(planeName + ": Cleaning crew has entered the plane.");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.out.println(planeName + "'s cleaning task was interrupted.");
        }
        System.out.println(planeName + ": Cleaning and resupply complete.");
    }
}