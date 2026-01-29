public class Backdoor {

    // ---DATA FIELDS---
    private String fromHostID; // source host ID
    private String toHostID; // destination host ID
    private int[] costs; // {latency, bandwidth, firewall-Level}
    private boolean isSealed;

    // ---CONSTRUCTORS---
    public Backdoor(String fromHostID, String toHostID, int[] costs) {
        this.fromHostID = fromHostID;
        this.toHostID = toHostID;
        this.costs = costs;
        this.isSealed = false;
    }

    // ---GETTERS---
    public int[] getCosts() {return costs;}
    public boolean getIsSealed() {return isSealed;}

    // ---SETTERS---
    public void setIsSealed(boolean sealState) {this.isSealed = sealState;}

}
