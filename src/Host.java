import java.util.ArrayList;

public class Host {

    // ---DATA FIELDS---
    private String hostID; // Unique host ID
    private int clearanceLevel; // Security level {1, 2, 3, 4, 5}
    private MyHashMap<Host, Backdoor> neighbors; // Neighbors with backdoor
    private ArrayList<Host> neighborList; // Cached neighbor keys

    // ---CONSTRUCTORS---
    public Host(String hostID, int clearanceLevel) {
        this.hostID = hostID;
        this.clearanceLevel = clearanceLevel;
        this.neighbors = new MyHashMap<>();
        this.neighborList = new ArrayList<>();
    }

    // ---GETTERS---
    public String getHostID() {return hostID;}
    public int getClearanceLevel() {return clearanceLevel;}
    public MyHashMap<Host, Backdoor> getNeighbors() {return neighbors;}
    public ArrayList<Host> getNeighborList() {return neighborList;}

    // ---METHODS---
    public void addNeighbor(Host neighbor, Backdoor backdoor) {
        if (!neighbors.containsKey(neighbor)) {
            neighbors.put(neighbor, backdoor);
            neighborList.add(neighbor);
        }
    }

}
