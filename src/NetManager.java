import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedList;

public class NetManager {

    // ---DATA FIELDS---
    private MyHashMap<String, Host> hosts; // Hosts in the network
    private MyHashMap<String, Backdoor> backdoors; // Backdoors in the network

    private int hostCount; // Size of hosts map
    private int unsealedCount; // Active backdoor count

    // ---CONSTRUCTORS---
    // Default Constructor
    public NetManager() {
        this.hosts = new MyHashMap<>();
        this.backdoors = new MyHashMap<>();

        this.hostCount = 0;
        this.unsealedCount = 0;
    }

    // ---METHODS---
    // CONSOLE INSTRUCTIONS:
    // 1) Creates a host in the network, if error returns error message
    public String spawnHost(String hostID, int clearanceLevel) {
        // Validations
        if (hostID == null) { // Is ID null ?
            return "Some error occurred in spawn_host.";
        }
        if (!hostID.matches("[A-Z0-9_]+")) { // Does it only contain (A–Z), (0–9), and underscores.
            return "Some error occurred in spawn_host.";
        }
        if (hosts.containsKey(hostID)) { // Does ID already exist ?
            return "Some error occurred in spawn_host.";
        }
        if (clearanceLevel < 1 || clearanceLevel > 5) { // is CL valid ?
            return "Some error occurred in spawn_host.";
        }

        // Creates, inserts host, and updates host counter
        Host newHost = new Host(hostID, clearanceLevel);
        hosts.put(hostID, newHost);
        hostCount++;

        // Success output
        return "Spawned host " + hostID + " with clearance level " + clearanceLevel + ".";
    }

    // 2) Creates a link between hosts, if error returns error message
    public String linkBackdoor(String hostID1, String hostID2, int latency, int bandwidth, int firewallLevel) {
        // Validations
        if (hostID1 == null || hostID2 == null) { // Are IDs null ?
            return "Some error occurred in link_backdoor.";
        }
        if (hostID1.equals(hostID2)) { // Is it a self-link?
            return "Some error occurred in link_backdoor.";
        }
        String backdoorKey = makeBackdoorKey(hostID1, hostID2);
        if (!hosts.containsKey(hostID1) || !hosts.containsKey(hostID2)) { // Do hosts exist ?
            return "Some error occurred in link_backdoor.";
        }
        if (backdoors.containsKey(backdoorKey)) { // Does backdoor already exist ?)
            return "Some error occurred in link_backdoor.";
        }
        if (firewallLevel < 1 || firewallLevel > 5) { // Is firewall-level valid ?
            return "Some error occurred in link_backdoor.";
        }

        // Creates and adds new backdoor to backdoors
        int[] costs = {latency, bandwidth, firewallLevel};
        Backdoor newBackdoor = new Backdoor(hostID1, hostID2, costs);
        backdoors.put(backdoorKey, newBackdoor);
        unsealedCount++;

        // Updates adjacency lists
        Host host1 = hosts.get(hostID1);
        Host host2 = hosts.get(hostID2);
        host1.addNeighbor(host2, newBackdoor);
        host2.addNeighbor(host1, newBackdoor);

        // Success output
        return "Linked " + hostID1 + " <-> " + hostID2
                + " with latency " + latency + "ms, bandwidth " + bandwidth
                + "Mbps, firewall " + firewallLevel + ".";
    }

    // 3) Seals or unseals a backdoor, if error returns error message
    public String sealBackdoor(String hostID1, String hostID2) {
        // Validations
        if (hostID1 == null || hostID2 == null) { // Is IDs null ?
            return "Some error occurred in seal_backdoor.";
        }
        if (!hosts.containsKey(hostID1) || !hosts.containsKey(hostID2)) { // Do hosts exist ?
                return "Some error occurred in seal_backdoor.";
        }
        String backdoorKey = makeBackdoorKey(hostID1, hostID2);
        if (!backdoors.containsKey(backdoorKey)) { // Does backdoor exist ?
            return "Some error occurred in seal_backdoor.";
        }

        // Toggles sealedFlag
        Backdoor backdoor = backdoors.get(backdoorKey);
        if (backdoor.getIsSealed()) { // T -> F
            backdoor.setIsSealed(false);
            unsealedCount++; // Updates unsealed count

            //Success output
            return "Backdoor " + hostID1 + " <-> " + hostID2 + " unsealed.";
        } else { // F -> T
            backdoor.setIsSealed(true);
            unsealedCount--; // Updates unsealed count

            // Success output
            return "Backdoor " + hostID1 + " <-> " + hostID2 + " sealed.";
        }
    }

    // 4) Traces a covert route, if error returns error message
    public String traceRoute(String sourceID, String destID, int minBandwidth, int lambda) {
        // Validations
        if (sourceID == null || destID == null) { // Are IDs null ?
            return "Some error occurred in trace_route.";
        }
        if (!hosts.containsKey(sourceID) || !hosts.containsKey(destID)) { // Do IDs exist ?
            return "Some error occurred in trace_route.";
        }

        // Are IDs same ?
        if (sourceID.equals(destID)) {
            // Success output
            return "Optimal route " + sourceID + " -> " + destID + ": " + sourceID + " (Latency = 0ms)";
        }

        // Dijkstra's algorithm with multi-objective optimization
        Host sourceHost = hosts.get(sourceID);

        // Local state for path reconstruction via parent pointers
        class State {
            Host host;
            int totalLatency;
            int numSegments;
            int parentIndex;

            State(Host host, int totalLatency, int numSegments, int parentIndex) {
                this.host = host;
                this.totalLatency = totalLatency;
                this.numSegments = numSegments;
                this.parentIndex = parentIndex;
            }
        }
        ArrayList<State> states = new ArrayList<>();

        // Best destination candidates (same best latency & segments)
        ArrayList<Integer> destCandidates = new ArrayList<>();
        int bestLatencyAtDest = Integer.MAX_VALUE;
        int bestSegmentsAtDest = Integer.MAX_VALUE;

        // MyMinHeap for Dijkstra (total latency, number of segments, lexicographic)
        MyMinHeap<Router> minHeap = new MyMinHeap<>(hostCount * 4);

        // Stores non-dominated states for each host
        MyHashMap<Host, ArrayList<int[]>> paretoFrontier = new MyHashMap<>();

        // First state for algorithm
        states.add(new State(sourceHost, 0, 0, -1));
        if (!paretoFrontier.containsKey(sourceHost)) {
            paretoFrontier.put(sourceHost, new ArrayList<>());
        }
        paretoFrontier.get(sourceHost).add(new int[]{0, 0, 0});
        minHeap.add(new Router(0, 0, sourceHost, 0));

        // Main loop of Dijkstra's algorithm: extracts the minimum router
        while (!minHeap.isEmpty()) {
            Router current = minHeap.poll();
            int currentIndex = current.getStateIndex();
            State currentState = states.get(currentIndex);

            int currentLatency = currentState.totalLatency;
            int currentSegment = currentState.numSegments;

            // Early termination
            if (currentLatency > bestLatencyAtDest) {
                break;
            }

            Host currentHost = current.getHost();

            // Lazy skip if dominated by another known state
            ArrayList<int[]> currentFront = paretoFrontier.get(currentHost);
            if (currentFront != null) {
                boolean dominatedByOther = false;
                for (int[] s : currentFront) {
                    if (s[2] == currentIndex) continue;
                    if (s[0] <= currentLatency && s[1] <= currentSegment) {
                        dominatedByOther = true;
                        break;
                    }
                }
                if (dominatedByOther) continue;
            }

            // Destination reached -> collects best candidates
            if (currentState.host.getHostID().equals(destID)) {
                if (currentLatency < bestLatencyAtDest ||
                        (currentLatency == bestLatencyAtDest && currentSegment < bestSegmentsAtDest)) {

                    bestLatencyAtDest = currentLatency;
                    bestSegmentsAtDest = currentSegment;
                    destCandidates.clear();
                    destCandidates.add(currentIndex);

                } else if (currentLatency == bestLatencyAtDest && currentSegment == bestSegmentsAtDest) {
                    destCandidates.add(currentIndex);
                }
                continue;
            }

            // Expands neighbors
            ArrayList<Host> neighhbors = currentHost.getNeighborList();
            MyHashMap<Host, Backdoor> neighborMap = currentHost.getNeighbors();
            int currentCL = currentHost.getClearanceLevel();
            for (int i = 0; i < neighhbors.size(); i++) {
                Host neighbor = neighhbors.get(i);
                String neighborID = neighbor.getHostID();

                Backdoor backdoor = neighborMap.get(neighbor);
                if (backdoor == null) continue;
                if (backdoor.getIsSealed()) continue;

                int[] costs = backdoor.getCosts();
                int latency = costs[0];
                int bandwidth = costs[1];
                int firewallLevel = costs[2];

                // Constraints
                if (bandwidth < minBandwidth) continue;
                if (currentCL < firewallLevel) continue;

                // Dynamic latency
                int dynamicLatency = latency + lambda * currentSegment;
                int newTotalLatency = currentLatency + dynamicLatency;
                int newSegmentCount = currentSegment + 1;

                // Prunes by known best dest latency
                if (bestLatencyAtDest != Integer.MAX_VALUE && newTotalLatency > bestLatencyAtDest) {
                    continue;
                }

                // Dominance prune on neighbor
                ArrayList<int[]> neighborFront = paretoFrontier.get(neighbor);
                boolean isDominated = false;
                if (neighborFront != null) {
                    for (int[] state : neighborFront) {
                        if (state[0] <= newTotalLatency && state[1] <= newSegmentCount) {
                            isDominated = true;
                            break;
                        }
                    }
                }
                if (isDominated) continue;

                // Creates new state
                int newIndex = states.size();
                states.add(new State(neighbor, newTotalLatency, newSegmentCount, currentIndex));

                // Adds to neighbor frontier and prunes states
                if (neighborFront == null) {
                    neighborFront = new ArrayList<>();
                    paretoFrontier.put(neighbor, neighborFront);
                }

                neighborFront.add(new int[]{newTotalLatency, newSegmentCount, newIndex});

                // Adds to heap
                minHeap.add(new Router(newTotalLatency, newSegmentCount, neighbor, newIndex));
            }
        }

        if (destCandidates.isEmpty()) {
            // Failure output
            return "No route found from " + sourceID + " to " + destID;
        }

        // Reconstructs and chooses lexicographically among candidates
        ArrayList<String> bestPath = null;
        for (int index : destCandidates) {
            ArrayList<String> path = new ArrayList<>();
            int currentIndex = index;
            while (currentIndex != -1) {
                path.add(states.get(currentIndex).host.getHostID());
                currentIndex = states.get(currentIndex).parentIndex;
            }
            // Reverses in-place
            for (int i = 0, j = path.size() - 1; i < j; i++, j--) {
                String temp = path.get(i);
                path.set(i, path.get(j));
                path.set(j, temp);
            }

            if (bestPath == null || isLexicoSmaller(path, bestPath)) {
                bestPath = path;
            }
        }

        StringBuilder route = new StringBuilder();
        for (int i = 0; i < bestPath.size(); i++) {
            if (i > 0) route.append(" -> ");
            route.append(bestPath.get(i));
        }

        // Success output
        return "Optimal route " + sourceID + " -> " + destID + ": "
                + route + " (Latency = " + bestLatencyAtDest + "ms)";
    }
    // Helper of 4, compares IDs lexicographically
    private boolean isLexicoSmaller(ArrayList<String> path1, ArrayList<String> path2) {
        int minSize = Math.min(path1.size(), path2.size());
        for (int i = 0; i < minSize; i++) {
            int cmp = path1.get(i).compareTo(path2.get(i));
            if (cmp < 0) return true;
            if (cmp > 0) return false;
        }
        return path1.size() < path2.size();
    }

    // 5) Checks whether network is fully connected
    public String scanConnectivity() {
        if (hostCount <= 1) { // Base case: 0 or 1 host
            // Success output
            return "Network is fully connected.";
        }

        // Checks whether it is single unit
        int componentCount = countComponents(); // Finds components count
        if (componentCount == 1) {
            // Success output
            return "Network is fully connected.";
        } else {
            // Failure output
            return "Network has " + componentCount + " disconnected components.";
        }
    }

    // 6) Analyzes without permanently modifications (2 method -> according to the input length), if error returns error message
    // 6.A) Host Breach
    public String simulateBreach(String hostID) {
        // Validations
        if (hostID == null) { // Is ID null ?
            return "Some error occurred in simulate_breach.";
        }
        if (!hosts.containsKey(hostID)) { // Does ID exist ?
            return "Some error occurred in simulate_breach.";
        }

        // If only 0 or 1 hosts, there is NO articulation point
        if (hostCount <= 1) {
            return "Host " + hostID + " is NOT an articulation point. Network remains the same.";
        }

        // Counts components before removal
        int componentCountBefore = countComponents();

        // Temporarily remove the host
        Host removedHost = hosts.remove(hostID);
        hostCount--;

        // Counts components after removal
        int componentCountAfter = countComponents();

        // Restores the host and backdoors
        hosts.put(hostID, removedHost);
        hostCount++;

        if (componentCountAfter > componentCountBefore) {
            // Articulation point output
            return "Host " + hostID + " IS an articulation point.\n" +
                    "Failure results in " + componentCountAfter + " disconnected components.";
        } else {
            // NOT articulation point output
            return "Host " + hostID + " is NOT an articulation point. Network remains the same.";
        }
    }
    // 6.B) Backdoor Breach
    public String simulateBreach(String hostID1, String hostID2) {
        // Validations
        if (hostID1 == null || hostID2 == null) { // Are IDs null ?
            return "Some error occurred in simulate_breach.";
        }
        if (!hosts.containsKey(hostID1) || !hosts.containsKey(hostID2)) { // Do IDs exist ?
            return "Some error occurred in simulate_breach.";
        }
        String backdoorKey = makeBackdoorKey(hostID1, hostID2);
        if (!backdoors.containsKey(backdoorKey)) { // Does backdoor exist ?
            return "Some error occurred in simulate_breach.";
        }
        Backdoor backdoor = backdoors.get(backdoorKey);
        if (backdoor.getIsSealed()) { // Is backdoor unsealed ?
            return "Some error occurred in simulate_breach.";
        }

        // Counts components before sealing
        int componentsBefore = countComponents();

        // Temporarily seals the backdoor
        backdoor.setIsSealed(true);
        unsealedCount--;

        // Counts components after sealing
        int componentsAfter = countComponents();

        // Restores the backdoor state
        backdoor.setIsSealed(false);
        unsealedCount++;

        if (componentsAfter > componentsBefore) {
            // Bridge output
            return "Backdoor " + hostID1 + " <-> " + hostID2 + " IS a bridge.\n" +
                    "Failure results in " + componentsAfter + " disconnected components.";
        } else {
            // NOT bridge output
            return "Backdoor " + hostID1 + " <-> " + hostID2 + " is NOT a bridge. Network remains the same.";
        }
    }

    // 7) Reports all features of the network to Oracle
    public String oracleReport() {
        // Network connectivity (isConnected, componentCount)
        String isConnected;
        int componentCount = countComponents();

        if (hostCount == 0 || hostCount == 1) {
            isConnected = "Connected";
        } else {
            isConnected = (componentCount == 1) ? "Connected" : "Disconnected";
        }

        // Checks for cycles (isCyclic)
        String isCyclic = containsCycles() ? "Yes" : "No";

        // Calculates average bandwidth (avgBW)
        double avgBW = 0.0;

        if (unsealedCount > 0) {
            double totalBandwidth = 0.0;
            for (Backdoor backdoor : backdoors.valueSet()) {
                if (!backdoor.getIsSealed()) {
                    int[] costs = backdoor.getCosts();
                    totalBandwidth += costs[1]; // bandwidth is at index 1
                }
            }
            avgBW = round(totalBandwidth / unsealedCount);
        }

        // Calculates average clearance level (avgCL)
        double avgCL = 0.0;

        if (hostCount > 0) {
            double totalClearance = 0;
            for (Host host : hosts.valueSet()) {
                totalClearance += host.getClearanceLevel();
            }
            avgCL = round(totalClearance / hostCount);
        }

        return "--- Resistance Network Report ---\n" +
                "Total Hosts: " + hostCount + "\n" +
                "Total Unsealed Backdoors: " + unsealedCount + "\n" +
                "Network Connectivity: " + isConnected + "\n" +
                "Connected Components: " + componentCount + "\n" +
                "Contains Cycles: " + isCyclic + "\n" +
                "Average Bandwidth: " + avgBW + "Mbps\n" +
                "Average Clearance Level: " + avgCL;
    }
    // Rounds to one decimal place, used in 7
    private double round(double value) {
        BigDecimal roundedNum = BigDecimal.valueOf(value);
        roundedNum = roundedNum.setScale(1, RoundingMode.HALF_UP);
        return roundedNum.doubleValue();
    }
    // Helper of 7, returns whether network contains cycle
    private boolean containsCycles() {
        if (hostCount < 3) {
            return false;
        }

        // Iterates all hosts
        MyHashMap<String, Boolean> visited = new MyHashMap<>();
        ArrayList<Host> allHosts = hosts.valueSet();
        for (int i = 0; i < allHosts.size(); i++) {
            Host currentHost = allHosts.get(i);
            String hostID = currentHost.getHostID();

            if (!visited.containsKey(hostID)) {
                if (hasCycleDFS(currentHost, null, visited)) {
                    return true;
                }
            }
        }

        return false;
    }
    // Helper of containsCycle() - DFS traversal
    private boolean hasCycleDFS(Host currentHost, Host parentHost, MyHashMap<String, Boolean> visiteds) {
        String currentID = currentHost.getHostID();
        visiteds.put(currentID, true);

        // Iterates all neighbors
        ArrayList<Host> neighbors = currentHost.getNeighbors().keySet();
        for (int i = 0; i < neighbors.size(); i++) {
            Host neighbor = neighbors.get(i);
            String neighborID = neighbor.getHostID();

            // Checks if it goes back to parent
            if (parentHost != null && neighborID.equals(parentHost.getHostID())) {
                continue;
            }
            // Checks if backdoor is null or sealed
            Backdoor backdoor = currentHost.getNeighbors().get(neighbor);
            if (backdoor == null || backdoor.getIsSealed()) {
                continue;
            }
            // Checks if neighbor is visited
            if (!visiteds.containsKey(neighborID)) {
                // Recursive calls
                if (hasCycleDFS(neighbor, currentHost, visiteds)) {
                    return true;
                }
            } else {
                // Found cycle
                return true;
            }
        }

        return false;
    }

    // ---GENERAL HELPERS---
    // Returns hosts of a backdoor as a string
    private String makeBackdoorKey(String hostID1, String hostID2) {
        if (hostID1.compareTo(hostID2) < 0) {
            return hostID1 + " " + hostID2;
        } else {
            return hostID2 + " " + hostID1;
        }
    }

    // Returns component count
    private int countComponents() {
        if (hostCount == 0 || hostCount == 1) { // 0 or 1 host
            return hostCount;
        }

        MyHashMap<String, Boolean> visiteds = new MyHashMap<>();
        int componentCount = 0;

        // Iterates all hosts and increase component count
        for (String hostID : hosts.keySet()) {
            if (!visiteds.containsKey(hostID)) {
                Host startHost = hosts.get(hostID);
                BFS(startHost, visiteds);
                componentCount++;
            }
        }

        return componentCount;
    }
    // Helper of countComponents() - BFS traversal
    private void BFS(Host startHost, MyHashMap<String, Boolean> visiteds) {
        LinkedList<Host> queue = new LinkedList<>();
        queue.offer(startHost);
        visiteds.put(startHost.getHostID(), true);

        // Main loop for BFS
        while (!queue.isEmpty()) {
            Host current = queue.poll();

            // Iterates all neighbors
            for (Host neighbor : current.getNeighbors().keySet()) {
                String neighborID = neighbor.getHostID();

                // Skips if neighbor is not in hosts (this is for 6.A)
                if (!hosts.containsKey(neighborID)) continue;

                // Checks if backdoor is unsealed
                Backdoor backdoor = current.getNeighbors().get(neighbor);
                if (backdoor == null || backdoor.getIsSealed()) {
                    continue;
                }
                // Checks if neighbor is not in visiteds
                if (!visiteds.containsKey(neighborID)) {
                    visiteds.put(neighborID, true);
                    queue.offer(neighbor);
                }
            }
        }
    }

}
