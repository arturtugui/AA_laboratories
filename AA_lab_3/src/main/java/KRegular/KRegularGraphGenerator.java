package KRegular;

import DirectedAndUndirected.DirectedUndirectedGraphVisualizer;
import Graph.Graph;

import java.util.*;

/**
 * Generator for connected k-regular undirected graphs.
 * A k-regular graph is one where each vertex has exactly k neighbors.
 */
public class KRegularGraphGenerator {

    /**
     * Generates a connected k-regular undirected graph with n vertices.
     * A k-regular graph has exactly k edges connected to each vertex.
     *
     * @param n Number of vertices
     * @param k Degree of each vertex (number of connections)
     * @param vertexLabels Optional array of vertex labels (if null, integers 0 to n-1 will be used)
     * @return A connected k-regular graph
     * @throws IllegalArgumentException if parameters don't allow for a k-regular connected graph
     */
    public static <V extends Comparable<V>> Graph<V> generateConnectedKRegularGraph(int n, int k, V[] vertexLabels) {
        // Check if parameters allow for a k-regular graph
        if (k >= n) {
            throw new IllegalArgumentException("Degree k must be less than the number of vertices n");
        }


        if (k < 1) {
            throw new IllegalArgumentException("Degree k must be at least 1 for a connected graph");
        }
        if (k == 1 && n != 2) {
            throw new IllegalArgumentException("For degree k = 1, nr. of vertices must be n = 2");
        }


        if ((n * k) % 2 != 0) {
            throw new IllegalArgumentException("For a k-regular graph, n*k must be even");
        }

        // Create a new undirected graph
        Graph<V> graph = new Graph<V>(false);

        // Add all vertices
        @SuppressWarnings("unchecked")
        V[] vertices = vertexLabels != null ? vertexLabels : (V[]) createDefaultLabels(n);

        for (V vertex : vertices) {
            graph.addVertex(vertex);
        }

        Random random = new Random();
        boolean isConnected = false;
        int maxAttempts = 100000; // Limit the number of attempts

        while (!isConnected && maxAttempts > 0) {
            // Clear any existing edges
            for (V v : graph.getVertices()) {
                List<V> neighbors = new ArrayList<>(graph.getAdjacencyList().get(v));
                for (V neighbor : neighbors) {
                    graph.removeEdge(v, neighbor);
                }
            }

            // Generate a k-regular graph using the configuration model
            isConnected = generateConfigurationModel(graph, vertices, k, random);

            // If not connected, try again
            maxAttempts--;
        }

        if (!isConnected) {
            throw new IllegalArgumentException("Failed to generate a connected k-regular graph after multiple attempts. Try different parameters.");
        }

        return graph;
    }

    /**
     * Implements the configuration model to generate a random k-regular graph.
     * Returns true if the generated graph is connected.
     */
    private static <V extends Comparable<V>> boolean generateConfigurationModel(Graph<V> graph, V[] vertices, int k, Random random) {
        int n = vertices.length;

        // Create k stubs for each vertex
        List<V> stubs = new ArrayList<>(n * k);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < k; j++) {
                stubs.add(vertices[i]);
            }
        }

        // Shuffle the stubs
        Collections.shuffle(stubs, random);

        // Match stubs to form edges
        for (int i = 0; i < stubs.size(); i += 2) {
            if (i + 1 >= stubs.size()) break; // Safety check

            V v1 = stubs.get(i);
            V v2 = stubs.get(i + 1);

            // Skip self-loops and multiple edges
            if (v1.equals(v2) || edgeExists(graph, v1, v2)) {
                // If we encounter a self-loop or multiple edge, start over
                return false;
            }

            graph.addEdge(v1, v2);
        }

        // Check if the graph is connected
        return isConnected(graph, vertices[0], vertices);
    }

    /**
     * Checks if the graph is connected using BFS
     */
    private static <V> boolean isConnected(Graph<V> graph, V start, V[] allVertices) {
        Set<V> visited = new HashSet<>();
        Queue<V> queue = new LinkedList<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            V current = queue.poll();

            for (V neighbor : graph.getAdjacencyList().get(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        // Check if all vertices were visited
        return visited.size() == allVertices.length;
    }

    /**
     * Helper method to check if an edge exists in the graph
     */
    private static <V> boolean edgeExists(Graph<V> graph, V from, V to) {
        Map<V, List<V>> adjacencyList = graph.getAdjacencyList();
        return adjacencyList.get(from).contains(to) || adjacencyList.get(to).contains(from);
    }

    /**
     * Helper method to create default integer labels
     */
    private static Integer[] createDefaultLabels(int n) {
        Integer[] labels = new Integer[n];
        for (int i = 0; i < n; i++) {
            labels[i] = i;
        }
        return labels;
    }

    /**
     * Convenience method for generating a k-regular graph with String labels
     */
    public static Graph<String> generateStringLabelKRegularGraph(int n, int k) {
        String[] labels = new String[n];
        for (int i = 0; i < n; i++) {
            labels[i] = Character.toString((char)('A' + i % 26)) + (i >= 26 ? i/26 : "");
        }
        return generateConnectedKRegularGraph(n, k, labels);
    }

    public static void main(String[] args) {
        try {
            // Example: Generate a 3-regular graph with 10 vertices
            Graph<String> regularGraph = generateStringLabelKRegularGraph(30, 5);
            System.out.println("Generated connected k-regular graph:");
            regularGraph.printGraph();

            // Visualize the graph if the visualizer class is available
            try {
                javax.swing.SwingUtilities.invokeLater(() -> new DirectedUndirectedGraphVisualizer(regularGraph));
            } catch (NoClassDefFoundError e) {
                System.out.println("Visualizer class not available. Skipping visualization.");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
