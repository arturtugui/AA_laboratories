package KRegular;

import Graph.Graph;
import java.util.ArrayList;
import java.util.List;

/**
 * Tester class that attempts to generate k-regular graphs and reports which (n,k) pairs
 * successfully generate graphs and which ones fail despite passing the initial validation criteria.
 */
public class KRegularGraphTester {

    /**
     * Tests if a k-regular graph can be successfully generated with the given parameters.
     * First checks the basic requirements, then actually tries to generate the graph.
     *
     * @param n Number of vertices
     * @param k Degree of each vertex
     * @return true if graph was successfully generated, false if it failed validation or generation
     */
    public static boolean testKRegularGraphGeneration(int n, int k) {
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
            return true; // Success if no exception is thrown
        } catch (IllegalArgumentException e) {
            // Failed to generate despite passing validation
            return false;
        }
    }

    private static void testSpecificPair(int n, int k) {
        System.out.println("\nTesting specific pair (" + n + "," + k + "):");
        if (!validateParameters(n, k)) {
            System.out.println("Failed: Pair (" + n + "," + k + ") doesn't pass initial validation");
            return;
        }

        boolean success = testKRegularGraphGeneration(n, k);
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
     */
    /**
     * Tests a range of (n,k) pairs and returns those that successfully generate graphs.
     * For each n, tests k values from minK to n-2.
     */
    public static List<String> findSuccessfulPairs(int minN, int maxN, int minK) {
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
                boolean success = testKRegularGraphGeneration(n, k);

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
    private static void testRangeOfNAndK(int minN, int maxN, int minK) {

        System.out.println("\nFinding all pairs (n,k) that successfully generate graphs...");
        System.out.println("For each n from " + minN + " to " + maxN + ", testing k from " + minK + " to n-2");
        List<String> successfulPairs = findSuccessfulPairs(minN, maxN, minK);

        System.out.println("\nSUMMARY OF SUCCESSFUL PAIRS:");
        for (String line : successfulPairs) {
            System.out.println(line);
        }
    }

    /**
     * Tests a specific k value across a range of n values
     */
    private static void testForSpecificK(int k, int minN, int maxN) {
        List<String> successfulNValues = testSpecificK(k, minN, maxN);

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
    private static void testForSpecificN(int n, int minK, int maxK) {
        List<String> successfulKValues = testSpecificN(n, minK, maxK);

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
     * @return List of successful n values as strings
     */
    public static List<String> testSpecificK(int k, int minN, int maxN) {
        List<String> successfulNValues = new ArrayList<>();

        System.out.println("\nTesting specific k=" + k + " across n values from " + minN + " to " + maxN);

        for (int n = minN; n <= maxN; n+=10) {
            // Skip invalid combinations based on requirements
            if (k >= n || (k == 1 && n != 2) || ((n * k) % 2 != 0) || (k == 2 && n < 3)) {
                System.out.println("Skipping n=" + n + " (fails validation)");
                continue;
            }

            System.out.println("Testing pair (" + n + "," + k + ")...");
            boolean success = testKRegularGraphGeneration(n, k);

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
     * @return List of successful k values as strings
     */
    public static List<String> testSpecificN(int n, int minK, int maxK) {
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
            boolean success = testKRegularGraphGeneration(n, k);

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

    public static void main(String[] args) {
        System.out.println("Testing K-Regular Graph Generation");
        System.out.println("=================================");

        //int[] nValues = {10, 50, 100, 250, 500, 750, 1000, 1250, 1500};
        //int[] nValues = {10, 50, 100, 250, 500, 750, 1000, 1250, 1750, 2500, 3500, 5000};

        // Choose one of the testing modes by uncommenting the desired section

        // MODE 1: Test a range of n values with k values from 3 to n-2 for each n
        //testRangeOfNAndK(4, 20, 3);

        // MODE 2: Test a specific k value across a range of n values
        //testForSpecificK(3, 10, 1500);
        //testForSpecificK(7, 10, 1500);

        // MODE 3: Test a specific n value across a range of k values
        //testForSpecificN(1000, 3, 100);


        //ANALYSIS

        //with 100k
        //For k = 3, increasing n
        //int[] nValues = {10, 50, 100, 250, 500, 750, 1000, 1250, 1500};
        //For k = 7, increasing n
        //int[] nValues = {40, 70, 100, 250,    500, 750, 1000, 1250, 1500};

        //with 10k
        // for k = 7
        // n=220, n=230, n=520, n=620, n=880, n=920, n=930, n=950, n=1350, n=1480, n=1500

        //with 1k
        //For n = 1000, increasing k = 3,4

        //with 10k
        //For n = 1000, increasing k = 3, 4, 5, 6, ...

        //with 100k
        //For n = 1000, increasing k = 3,4,5,6...
    }
}