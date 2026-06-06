package airportsimulation;

/**
 * Runnable task simulating passenger disembarkation and boarding.
 */
public class PassengerTask implements Runnable {
    private final String planeName;
    private final int passengerCount;

    public PassengerTask(String planeName, int passengerCount) {
        this.planeName = planeName;
        this.passengerCount = passengerCount;
    }

    @Override
    public void run() {
        System.out.println(planeName + ": " + passengerCount + " passengers are disembarking out of " + planeName + ".");
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            System.out.println(planeName + "'s passenger task was interrupted.");
        }
        System.out.println(planeName + ": " + passengerCount + " passengers have completed boarding " + planeName + ".");
    }
}