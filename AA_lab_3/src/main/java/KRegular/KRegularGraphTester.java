package KRegular;

import Graph.Graph;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Tester class that attempts to generate k-regular graphs and reports which (n,k) pairs
 * successfully generate graphs and which ones fail despite passing the initial validation criteria.
 * Now with capability to store generated graphs for later retrieval.
 */
public class KRegularGraphTester {
    // Static map to store successfully generated graphs
    private static Map<String, Graph<String>> storedGraphs = new HashMap<>();
    // Directory for storing serialized graphs
    private static final String STORAGE_DIR = "D:\\Universitate\\Semestrul 4\\AA\\AA labs\\AA labs Github\\AA_laboratories\\AA_lab_3\\src\\main\\java\\KRegular\\stored_graphs";

    // Load all previously stored graphs when the class is loaded
    static {
        loadStoredGraphs();
    }

    /**
     * Tests if a k-regular graph can be successfully generated with the given parameters.
     * First checks the basic requirements, then actually tries to generate the graph.
     *
     * @param n Number of vertices
     * @param k Degree of each vertex
     * @param storeGraph If true, store the successfully generated graph
     * @return true if graph was successfully generated, false if it failed validation or generation
     */
    public static boolean testKRegularGraphGeneration(int n, int k, boolean storeGraph) {
        // First, check if the parameters pass the basic validation
        if (!validateParameters(n, k)) {
            return false;
        }

        // If validation passes, try to actually generate the graph
        try {
            // Use String labels for simplicity
            String[] labels = new String[n];
            for (int i = 0; i < n; i++) {
                labels[i] = Character.toString((char)('A' + i % 26)) + (i >= 26 ? i/26 : "");
            }

            // Attempt to generate the graph
            Graph<String> graph = KRegularGraphGenerator.generateConnectedKRegularGraph(n, k, labels);

            // If requested, store the successfully generated graph
            if (storeGraph) {
                String key = getGraphKey(n, k);
                // Check if this graph is already stored
                if (!storedGraphs.containsKey(key)) {
                    storedGraphs.put(key, graph);
                    saveGraphToFile(key, graph);
                    System.out.println("Graph " + key + " stored successfully.");
                } else {
                    System.out.println("Graph " + key + " already exists in storage. Not overwriting.");
                }
            }

            return true; // Success if no exception is thrown
        } catch (IllegalArgumentException e) {
            // Failed to generate despite passing validation
            return false;
        }
    }

    private static void testSpecificPair(int n, int k, boolean storeGraph) {
        System.out.println("\nTesting specific pair (" + n + "," + k + "):");
        if (!validateParameters(n, k)) {
            System.out.println("Failed: Pair (" + n + "," + k + ") doesn't pass initial validation");
            return;
        }

        boolean success = testKRegularGraphGeneration(n, k, storeGraph);
        if (success) {
            System.out.println("Success: Pair (" + n + "," + k + ") generated a valid graph");
        } else {
            System.out.println("Failed: Pair (" + n + "," + k + ") passed validation but failed generation");
        }
    }

    /**
     * Validates the basic requirements for a k-regular graph without actually trying to generate it.
     */
    private static boolean validateParameters(int n, int k) {
        // Check if k is less than n
        if (k >= n) {
            return false;
        }

        // Check if k is at least 1 (for connectedness)
        if (k < 1) {
            return false;
        }

        // Special case: For k=1, only n=2 is valid (a single edge between two vertices)
        if (k == 1 && n != 2) {
            return false;
        }

        // Check if n*k is even (handshaking lemma - sum of degrees must be even)
        if ((n * k) % 2 != 0) {
            return false;
        }

        // Additional check for k=2: must have n ≥ 3 to form a cycle
        if (k == 2 && n < 3) {
            return false;
        }

        return true;
    }

    /**
     * Tests a range of (n,k) pairs and returns those that successfully generate graphs.
     * For each n, tests k values from minK to n-2.
     *
     * @param minN Minimum number of vertices
     * @param maxN Maximum number of vertices
     * @param minK Minimum degree
     * @param storeGraph If true, store successfully generated graphs
     * @return List of successful (n,k) pairs
     */
    public static List<String> findSuccessfulPairs(int minN, int maxN, int minK, boolean storeGraph) {
        List<String> successfulPairs = new ArrayList<>();

        for (int n = minN; n <= maxN; n++) {
            List<String> pairsForCurrentN = new ArrayList<>();

            // For each n, test k from minK (or 3, whichever is larger) up to n-2
            int kStart = Math.max(minK, 3);
            int kEnd = n - 2;

            for (int k = kStart; k <= kEnd; k++) {
                // Skip invalid combinations that wouldn't pass initial validation
                if (!validateParameters(n, k)) {
                    continue;
                }

                System.out.println("Testing pair (" + n + "," + k + ")...");
                boolean success = testKRegularGraphGeneration(n, k, storeGraph);

                if (success) {
                    String pair = "(" + n + "," + k + ")";
                    pairsForCurrentN.add(pair);
                    System.out.println("Success: " + pair);
                } else {
                    System.out.println("Failed: (" + n + "," + k + ")");
                }
            }

            if (!pairsForCurrentN.isEmpty()) {
                // Add the current n value before its successful pairs
                successfulPairs.add("n=" + n + ": " + String.join(", ", pairsForCurrentN));
            }
        }

        return successfulPairs;
    }

    /**
     * Tests a range of n values, with k from 3 to n-2 for each n
     */
    private static void testRangeOfNAndK(int minN, int maxN, int minK, boolean storeGraph) {
        System.out.println("\nFinding all pairs (n,k) that successfully generate graphs...");
        System.out.println("For each n from " + minN + " to " + maxN + ", testing k from " + minK + " to n-2");
        List<String> successfulPairs = findSuccessfulPairs(minN, maxN, minK, storeGraph);

        System.out.println("\nSUMMARY OF SUCCESSFUL PAIRS:");
        for (String line : successfulPairs) {
            System.out.println(line);
        }
    }

    /**
     * Tests a specific k value across a range of n values
     */
    private static void testForSpecificK(int k, int minN, int maxN, boolean storeGraph) {
        List<String> successfulNValues = testSpecificK(k, minN, maxN, storeGraph);

        System.out.println("\nSUMMARY OF SUCCESSFUL N VALUES FOR k=" + k + ":");
        if (successfulNValues.isEmpty()) {
            System.out.println("No successful n values found");
        } else {
            System.out.println("k=" + k + ": " + String.join(", ", successfulNValues));
        }
    }

    /**
     * Tests a specific n value across a range of k values
     */
    private static void testForSpecificN(int n, int minK, int maxK, boolean storeGraph) {
        List<String> successfulKValues = testSpecificN(n, minK, maxK, storeGraph);

        System.out.println("\nSUMMARY OF SUCCESSFUL K VALUES FOR n=" + n + ":");
        if (successfulKValues.isEmpty()) {
            System.out.println("No successful k values found");
        } else {
            System.out.println("n=" + n + ": " + String.join(", ", successfulKValues));
        }
    }

    /**
     * Tests a specific k value against a range of n values.
     *
     * @param k The specific degree to test
     * @param minN Minimum number of vertices
     * @param maxN Maximum number of vertices
     * @param storeGraph If true, store successfully generated graphs
     * @return List of successful n values as strings
     */
    public static List<String> testSpecificK(int k, int minN, int maxN, boolean storeGraph) {
        List<String> successfulNValues = new ArrayList<>();

        System.out.println("\nTesting specific k=" + k + " across n values from " + minN + " to " + maxN);

        for (int n = minN; n <= maxN; n+=10) {
            // Skip invalid combinations based on requirements
            if (k >= n || (k == 1 && n != 2) || ((n * k) % 2 != 0) || (k == 2 && n < 3)) {
                System.out.println("Skipping n=" + n + " (fails validation)");
                continue;
            }

            System.out.println("Testing pair (" + n + "," + k + ")...");
            boolean success = testKRegularGraphGeneration(n, k, storeGraph);

            if (success) {
                String result = "n=" + n;
                successfulNValues.add(result);
                System.out.println("Success: " + result);
            } else {
                System.out.println("Failed: n=" + n);
            }
        }

        return successfulNValues;
    }

    /**
     * Tests a specific n value against a range of k values.
     *
     * @param n The specific number of vertices to test
     * @param minK Minimum degree
     * @param maxK Maximum degree (will be capped at n-1)
     * @param storeGraph If true, store successfully generated graphs
     * @return List of successful k values as strings
     */
    public static List<String> testSpecificN(int n, int minK, int maxK, boolean storeGraph) {
        List<String> successfulKValues = new ArrayList<>();

        // Make sure maxK doesn't exceed n-1
        maxK = Math.min(maxK, n-1);

        System.out.println("\nTesting specific n=" + n + " across k values from " + minK + " to " + maxK);

        for (int k = minK; k <= maxK; k++) {
            // Skip invalid combinations based on requirements
            if (k >= n || (k == 1 && n != 2) || ((n * k) % 2 != 0) || (k == 2 && n < 3)) {
                System.out.println("Skipping k=" + k + " (fails validation)");
                continue;
            }

            System.out.println("Testing pair (" + n + "," + k + ")...");
            boolean success = testKRegularGraphGeneration(n, k, storeGraph);

            if (success) {
                String result = "k=" + k;
                successfulKValues.add(result);
                System.out.println("Success: " + result);
            } else {
                System.out.println("Failed: k=" + k);
            }
        }

        return successfulKValues;
    }

    /**
     * Gets a consistent key format for storing graphs
     *
     * @param n Number of vertices
     * @param k Degree of each vertex
     * @return String key in format "n-k"
     */
    private static String getGraphKey(int n, int k) {
        return n + "-" + k;
    }

    /**
     * Returns the map of stored graphs
     *
     * @return Map of stored graphs with keys in format "n-k"
     */
    public static Map<String, Graph<String>> getStoredGraphs() {
        // Make sure graphs are loaded
        if (storedGraphs.isEmpty()) {
            loadStoredGraphs();
        }
        return storedGraphs;
    }

    /**
     * Saves a graph to a file.
     *
     * @param key The key in format "n-k"
     * @param graph The graph to save
     */

    private static void saveGraphToFile(String key, Graph<String> graph) {
        try {
            Map<String, List<String>> adjacencyList = new HashMap<>();

            for (String node : graph.getNodes()) {
                List<String> neighbors = graph.getNeighbors(node);
                adjacencyList.put(node, neighbors);
            }

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(adjacencyList);

            File file = new File(STORAGE_DIR, key + ".json");
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(json);
            }
        } catch (IOException e) {
            System.err.println("Error saving graph to JSON: " + e.getMessage());
        }
    }


    /**
     * Loads all stored graphs from files.
     */
    @SuppressWarnings("unchecked")
    private static void loadStoredGraphs() {
        File directory = new File(STORAGE_DIR);
        if (!directory.exists() || !directory.isDirectory()) {
            return;
        }

        File[] files = directory.listFiles((dir, name) -> name.endsWith(".ser"));
        if (files == null) return;

        int count = 0;
        for (File file : files) {
            String filename = file.getName();
            String key = filename.substring(0, filename.lastIndexOf('.'));

            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Graph<String> graph = (Graph<String>) ois.readObject();
                storedGraphs.put(key, graph);
                count++;
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading graph from file " + filename + ": " + e.getMessage());
            }
        }

        if (count > 0) {
            System.out.println("Loaded " + count + " graphs from storage.");
        }
    }

    /**
     * Saves all graphs in memory to files.
     */
    public static void saveAllGraphs() {
        for (Map.Entry<String, Graph<String>> entry : storedGraphs.entrySet()) {
            saveGraphToFile(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Clears all stored graphs both from memory and from disk.
     */
    public static void clearStoredGraphs() {
        // Clear in-memory storage
        storedGraphs.clear();

        // Delete files from disk
        File directory = new File(STORAGE_DIR);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles((dir, name) -> name.endsWith(".ser"));
            if (files != null) {
                for (File file : files) {
                    if (!file.delete()) {
                        System.err.println("Failed to delete file: " + file.getAbsolutePath());
                    }
                }
            }
        }

        System.out.println("All stored graphs have been cleared from memory and disk.");
    }

    public static void main(String[] args) {
        System.out.println("Testing K-Regular Graph Generation");
        System.out.println("=================================");

        // Print how many graphs are already loaded from disk
        System.out.println("Initial graphs loaded from storage: " + storedGraphs.size());

        // Choose one of the testing modes by uncommenting the desired section
        // Add storage parameter (true) to store graphs

        // MODE 1: Test a range of n values with k values from 3 to n-2 for each n
        // testRangeOfNAndK(4, 20, 3, true);

        // MODE 2: Test a specific k value across a range of n values
        // testForSpecificK(3, 10, 100, true);

        // MODE 3: Test a specific n value across a range of k values
        testForSpecificN(1000, 3, 10, true);

        // Ensure all graphs are saved to disk before exiting
        saveAllGraphs();

        System.out.println("\nTotal graphs in storage after testing: " + storedGraphs.size());
    }
}