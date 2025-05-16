package KRegular;

import Graph.Graph;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A class for retrieving and managing previously stored k-regular graphs.
 * Works in conjunction with KRegularGraphTester to provide access to
 * pre-generated k-regular graphs.
 */
public class KRegularGraphStorage {

    /**
     * Retrieves a specific k-regular graph if it exists in storage.
     *
     * @param n Number of vertices
     * @param k Degree of each vertex
     * @return The stored graph if it exists, null otherwise
     */
    public static Graph<String> getGraph(int n, int k) {
        String key = getGraphKey(n, k);
        Map<String, Graph<String>> storedGraphs = KRegularGraphTester.getStoredGraphs();

        if (storedGraphs.containsKey(key)) {
            System.out.println("Retrieved graph with n=" + n + ", k=" + k + " from storage.");
            return storedGraphs.get(key);
        } else {
            System.out.println("Graph with n=" + n + ", k=" + k + " not found in storage.");
            return null;
        }
    }

    /**
     * Lists all stored graph parameters (n,k) in a formatted output.
     * Each n value starts on a new line, and all k values for that n are listed.
     *
     * @return Formatted string containing all stored graph parameters
     */
    public static String listStoredGraphs() {
        Map<String, Graph<String>> storedGraphs = KRegularGraphTester.getStoredGraphs();

        if (storedGraphs.isEmpty()) {
            return "No graphs currently in storage.";
        }

        // Use TreeMap to sort the output by n values
        Map<Integer, List<Integer>> groupedByN = new TreeMap<>();

        // Group by n values
        for (String key : storedGraphs.keySet()) {
            String[] parts = key.split("-");
            int n = Integer.parseInt(parts[0]);
            int k = Integer.parseInt(parts[1]);

            if (!groupedByN.containsKey(n)) {
                groupedByN.put(n, new ArrayList<>());
            }
            groupedByN.get(n).add(k);
        }

        // Build the output string
        StringBuilder output = new StringBuilder();
        output.append("Stored k-regular graphs:\n");

        for (Map.Entry<Integer, List<Integer>> entry : groupedByN.entrySet()) {
            int n = entry.getKey();
            List<Integer> kValues = entry.getValue();

            // Sort the k values for consistent output
            kValues.sort(Integer::compareTo);

            output.append("n=").append(n).append(": ");
            for (int i = 0; i < kValues.size(); i++) {
                output.append("(").append(n).append(",").append(kValues.get(i)).append(")");
                if (i < kValues.size() - 1) {
                    output.append(", ");
                }
            }
            output.append("\n");
        }

        return output.toString();
    }

    /**
     * Counts the total number of stored graphs.
     *
     * @return The count of stored graphs
     */
    public static int getStoredGraphCount() {
        return KRegularGraphTester.getStoredGraphs().size();
    }

    /**
     * Checks if a specific graph exists in storage.
     *
     * @param n Number of vertices
     * @param k Degree of each vertex
     * @return true if the graph exists in storage, false otherwise
     */
    public static boolean hasGraph(int n, int k) {
        String key = getGraphKey(n, k);
        return KRegularGraphTester.getStoredGraphs().containsKey(key);
    }

    /**
     * Gets a consistent key format for accessing graphs
     *
     * @param n Number of vertices
     * @param k Degree of each vertex
     * @return String key in format "n-k"
     */
    private static String getGraphKey(int n, int k) {
        return n + "-" + k;
    }

    /**
     * If a graph doesn't exist in storage, attempts to generate it and add it to storage.
     *
     * @param n Number of vertices
     * @param k Degree of each vertex
     * @return true if the graph was successfully retrieved or generated, false otherwise
     */
    public static boolean retrieveOrGenerateGraph(int n, int k) {
        // Check if graph already exists in storage
        if (hasGraph(n, k)) {
            return true;
        }

        // Attempt to generate the graph
        System.out.println("Graph (" + n + "," + k + ") not found in storage. Attempting to generate...");
        return KRegularGraphTester.testKRegularGraphGeneration(n, k, true);
    }

    /**
     * Main method for demonstration purposes
     */
    public static void main(String[] args) {

    }
}
