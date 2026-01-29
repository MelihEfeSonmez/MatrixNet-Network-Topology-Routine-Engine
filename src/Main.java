import java.io.*;
import java.util.Locale;

/**
 * Main entry point for "MatrixNet: The Operator's Console".
 */
public class Main {

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        if (args.length != 2) {
            System.err.println("Usage: java Main <input_file> <output_file>");
            System.exit(1);
        }

        String inputFile = args[0];
        String outputFile = args[1];

        NetManager network = new NetManager(); // Initialized for this class

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                processCommand(line, writer, network);
            }

        } catch (IOException e) {
            System.err.println("Error reading/writing files: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void processCommand(String command, BufferedWriter writer, NetManager network)
            throws IOException {

        String[] parts = command.split("\\s+");
        String operation = parts[0];

        try {
            String result = "";

            switch (operation) {
                case "spawn_host":
                    if (parts.length == 3) {
                        String hostID = parts[1];
                        int clearanceLevel = Integer.parseInt(parts[2]);

                        result = network.spawnHost(hostID, clearanceLevel);
                    } else {
                        result = "Some error occurred in spawn_host.";
                    }
                    break;

                case "link_backdoor":
                    if (parts.length == 6) {
                        String hostID1 = parts[1];
                        String hostID2 = parts[2];
                        int latency = Integer.parseInt(parts[3]);
                        int bandwidth = Integer.parseInt(parts[4]);
                        int firewallLevel = Integer.parseInt(parts[5]);

                        result = network.linkBackdoor(hostID1, hostID2, latency, bandwidth, firewallLevel);
                    } else {
                        result = "Some error occurred in link_backdoor.";
                    }
                    break;

                case "seal_backdoor":
                    if (parts.length == 3) {
                        String hostID1 = parts[1];
                        String hostID2 = parts[2];

                        result = network.sealBackdoor(hostID1, hostID2);
                    } else {
                        result = "Some error occurred in seal_backdoor.";
                    }
                    break;

                case "trace_route":
                    if (parts.length == 5) {
                        String hostID1 = parts[1];
                        String hostID2 = parts[2];
                        int minBandwidth = Integer.parseInt(parts[3]);
                        int lambda = Integer.parseInt(parts[4]);

                        result = network.traceRoute(hostID1, hostID2, minBandwidth, lambda);
                    } else {
                        result = "Some error occurred in trace_route.";
                    }
                    break;

                case "scan_connectivity":
                    if (parts.length == 1) {
                        result = network.scanConnectivity();
                    } else {
                        result = "Some error occurred in scan_connectivity.";
                    }
                    break;

                case "simulate_breach":
                    if (parts.length == 2) {
                        String hostID = parts[1];

                        result = network.simulateBreach(hostID);
                    } else if (parts.length == 3) {
                        String hostID1 = parts[1];
                        String hostID2 = parts[2];

                        result = network.simulateBreach(hostID1, hostID2);
                    } else {
                        result = "Some error occurred in simulate_breach.";
                    }
                    break;

                case "oracle_report":
                    if (parts.length == 1) {
                        result = network.oracleReport();
                    } else {
                        result = "Some error occurred in oracle_report.";
                    }
                    break;

                default:
                    result = "Unknown command: " + operation;
            }

            writer.write(result);
            writer.newLine();

        } catch (Exception e) {
            writer.write("Error processing command: " + command);
            writer.newLine();
        }
    }
}
