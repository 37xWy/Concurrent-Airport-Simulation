package airportsimulation;

/**
 * Represents a parking gate at the airport.
 * State is managed by the ATC monitor to prevent race conditions.
 */
public class Gate {
    private String gateName;
    private boolean isOccupied;
    
    public Gate(String gateName){
        this.gateName = gateName;
        this.isOccupied = false;
    }
    
    public String getGateName(){
        return gateName;
    }
    
    public synchronized boolean allocateGate(){
        if(!isOccupied){
            isOccupied = true;
            return true;
        }
        return false;
    }
    
    public synchronized void freeGate(){
        isOccupied = false;
    }
    
    public boolean verifyEmpty(){
        return !isOccupied;
    }
}