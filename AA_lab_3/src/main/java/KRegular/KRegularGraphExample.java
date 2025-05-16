package KRegular;

import Graph.Graph;

/**
 * Example class demonstrating how to use the enhanced KRegularGraphTester
 * and KRegularGraphStorage classes with persistent storage.
 */
public class KRegularGraphExample {

    public static void main(String[] args) {
        System.out.println("K-Regular Graph Example");
        System.out.println("======================");

        // Step 1: Show existing graphs that were loaded from disk
        System.out.println("\nExisting graphs loaded from disk:");
        listAllGraphs();

        // Step 2: Generate some new graphs if needed
        generateSomeGraphs();

        // Step 3: List all stored graphs after generation
        System.out.println("\nAfter generating more graphs:");
        listAllGraphs();

        // Step 4: Retrieve a specific graph
        retrieveSpecificGraph(10, 4);

        // Step 6: Run a test for a range of values and store the results
        testAndStoreRange();

        // Step 7: Display the final list of stored graphs
        System.out.println("\nFinal list of stored graphs:");
        listAllGraphs();

        // Step 8: Save all graphs to disk to ensure persistence
        System.out.println("\nSaving all graphs to disk for persistence...");
        KRegularGraphTester.saveAllGraphs();
        System.out.println("All graphs saved. They will be available in future runs.");

        // Optional: Uncomment to demonstrate clearing stored graphs
        // System.out.println("\nDemonstrating clear operation (commented out by default):");
        // KRegularGraphTester.clearStoredGraphs();
        // listAllGraphs();
    }

    private static void generateSomeGraphs() {
        System.out.println("\nGenerating some graphs (if not already existing)...");

        // Generate some graphs with different parameters
        KRegularGraphTester.testKRegularGraphGeneration(6, 3, true);
        KRegularGraphTester.testKRegularGraphGeneration(8, 4, true);
        KRegularGraphTester.testKRegularGraphGeneration(10, 4, true);
        KRegularGraphTester.testKRegularGraphGeneration(12, 6, true);

        System.out.println("Graph generation completed.");
    }

    private static void listAllGraphs() {
        String graphList = KRegularGraphStorage.listStoredGraphs();
        System.out.println(graphList);
        System.out.println("Total stored graphs: " + KRegularGraphStorage.getStoredGraphCount());
    }

    private static void retrieveSpecificGraph(int n, int k) {
        System.out.println("\nRetrieving graph with n=" + n + ", k=" + k + "...");
        Graph<String> graph = KRegularGraphStorage.getGraph(n, k);

        if (graph != null) {
            System.out.println("Graph exists.");
        } else {
            System.out.println("Graph retrieval failed.");
        }
    }


    private static void testAndStoreRange() {
        System.out.println("\nTesting a specific range of values and storing successful graphs...");

        // Test n=14 with k values from 3 to 6 (limited range for demonstration)
        System.out.println("Testing n=14 with various k values:");
        KRegularGraphTester.testSpecificN(14, 3, 6, true);

        // Test k=5 with n values from 10 to 30 (in steps of 10)
        System.out.println("\nTesting k=5 with various n values:");
        KRegularGraphTester.testSpecificK(5, 10, 30, true);
    }
}
