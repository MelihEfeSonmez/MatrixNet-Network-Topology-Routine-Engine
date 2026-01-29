public class Router {

    // ---DATA FIELDS---
    private int totalLatency; // Total latency so far
    private int numSegments; // Number of hops
    private Host host; //  Host where this route ends
    private int stateIndex; // Index of the associated state

    // ---CONSTRUCTORS---
    public Router(int totalLatency, int numSegments, Host host, int stateIndex) {
        this.totalLatency = totalLatency;
        this.numSegments = numSegments;
        this.host = host;
        this.stateIndex = stateIndex;
    }

    // ---GETTERS---
    public int getTotalLatency() {return totalLatency;}
    public int getNumSegments() {return numSegments;}
    public Host getHost() {return host;}
    public int getStateIndex() {return stateIndex;}

    // ---SETTERS---
    public void setHost(Host host) {this.host = host;}

}
