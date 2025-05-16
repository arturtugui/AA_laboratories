package DirectedAndUndirected;

import Graph.Graph;

import java.util.*;

public class DirectedUndirectedGraphGenerator {

    /**
     * Generates a random connected graph with n vertices and m edges.
     *
     * @param n Number of vertices
     * @param m Number of edges
     * @param isDirected Whether the graph is directed
     * @param vertexLabels Optional array of vertex labels (if null, integers 0 to n-1 will be used)
     * @return A connected graph with the specified properties
     * @throws IllegalArgumentException if parameters don't allow for a connected graph
     */
    public static <V extends Comparable<V>> Graph<V> generateConnectedGraph(int n, int m, boolean isDirected, V[] vertexLabels) {
        // Check if parameters allow for a connected graph
        int minEdges = n - 1; // Minimum edges needed for a connected graph
        int maxEdges = isDirected ? n * (n - 1) : n * (n - 1) / 2; // Maximum possible edges

        if (m < minEdges) {
            throw new IllegalArgumentException("At least " + minEdges + " edges needed for a connected graph with " + n + " vertices");
        }

        if (m > maxEdges) {
            throw new IllegalArgumentException("Too many edges requested. Maximum is " + maxEdges + " for a " +
                    (isDirected ? "directed" : "undirected") + " graph with " + n + " vertices");
        }

        // Create a new graph
        Graph<V> graph = new Graph<V>(isDirected);

        // Add all vertices
        @SuppressWarnings("unchecked")
        V[] vertices = vertexLabels != null ? vertexLabels : (V[]) createDefaultLabels(n);

        for (V vertex : vertices) {
            graph.addVertex(vertex);
        }

        // Generate a random spanning tree first to ensure connectivity
        // Using a randomized version of Prim's algorithm
        Random random = new Random();
        Set<V> includedVertices = new HashSet<>();
        Set<V> remainingVertices = new HashSet<>(Arrays.asList(vertices));

        V startVertex;
        if(isDirected){
            // To have a directed rooted tree
            // Call BFS/DFS on 1st node
            startVertex = vertices[0];
        } else{
            // Start with a random vertex
            startVertex = vertices[random.nextInt(n)];
        }
        includedVertices.add(startVertex);
        remainingVertices.remove(startVertex);

        // Generate spanning tree (n-1 edges)
        while (!remainingVertices.isEmpty()) {
            // Pick a random vertex from included vertices
            List<V> includedList = new ArrayList<>(includedVertices);
            V fromVertex = includedList.get(random.nextInt(includedList.size()));

            // Pick a random vertex from remaining vertices
            List<V> remainingList = new ArrayList<>(remainingVertices);
            V toVertex = remainingList.get(random.nextInt(remainingList.size()));

            // Add edge
            graph.addEdge(fromVertex, toVertex);

            // For undirected graphs, manually ensure the edge exists in both directions
            if (!isDirected) {
                ensureUndirectedEdge(graph, fromVertex, toVertex);
            }

            // Move the vertex from remaining to included
            includedVertices.add(toVertex);
            remainingVertices.remove(toVertex);
        }

        // We now have n-1 edges in our graph (a spanning tree)
        int addedEdges = n - 1;

        // Add remaining random edges
        List<EdgePair<V>> possibleEdges = new ArrayList<>();

        // Generate all possible remaining edges
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) continue; // Skip self-loops

                V from = vertices[i];
                V to = vertices[j];

                // Skip if edge already exists (need to check the adjacency of our graph)
                if (edgeExists(graph, from, to)) {
                    continue;
                }

                // For undirected graphs, only add one of (u,v) and (v,u)
                if (!isDirected && i > j) {
                    continue;
                }

                possibleEdges.add(new EdgePair<>(from, to));
            }
        }

        // Shuffle the possible edges
        Collections.shuffle(possibleEdges);

        // Add edges until we reach m
        for (EdgePair<V> edge : possibleEdges) {
            if (addedEdges >= m) {
                break;
            }

            graph.addEdge(edge.from, edge.to);

            // For undirected graphs, manually ensure the edge exists in both directions
            if (!isDirected) {
                ensureUndirectedEdge(graph, edge.from, edge.to);
            }

            addedEdges++;
        }

        return graph;
    }

    /**
     * Helper method to ensure that for undirected graphs, edges exist in both directions
     * in the adjacency list
     */
    private static <V> void ensureUndirectedEdge(Graph<V> graph, V from, V to) {
        Map<V, List<V>> adjacencyList = getAdjacencyList(graph);

        // Ensure from -> to exists
        if (!adjacencyList.get(from).contains(to)) {
            adjacencyList.get(from).add(to);
        }

        // Ensure to -> from exists
        if (!adjacencyList.get(to).contains(from)) {
            adjacencyList.get(to).add(from);
        }
    }

    /**
     * Helper method to check if an edge exists in the graph
     */
    private static <V> boolean edgeExists(Graph<V> graph, V from, V to) {
        try {
            // Use reflection to access the adjacency list
            @SuppressWarnings("unchecked")
            Map<V, List<V>> adjacencyList = getAdjacencyList(graph);
            return adjacencyList.get(from).contains(to);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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
     * Helper class to represent an edge
     */
    private static class EdgePair<V> {
        V from;
        V to;

        public EdgePair(V from, V to) {
            this.from = from;
            this.to = to;
        }
    }

    /**
     * Helper method to access the adjacency list from the Graph.Graph class
     */
    @SuppressWarnings("unchecked")
    private static <V> Map<V, List<V>> getAdjacencyList(Graph<V> graph) {
        try {
            java.lang.reflect.Field field = Graph.class.getDeclaredField("adjacencyList");
            field.setAccessible(true);
            return (Map<V, List<V>>) field.get(graph);
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    /**
     * Convenience method for generating a graph with String labels
     */
    public static Graph<String> generateStringLabelGraph(int n, int m, boolean isDirected) {
        String[] labels = new String[n];
        for (int i = 0; i < n; i++) {
            labels[i] = Character.toString((char)('A' + i % 26)) + (i >= 26 ? i/26 : "");
        }
        return generateConnectedGraph(n, m, isDirected, labels);
    }

    public static void main(String[] args) {
        // Example usage - create a random connected graph with 10 vertices and 15 edges
        try {
            Graph<String> randomGraph = generateStringLabelGraph(6, 7, false);
            //Graph<String> randomGraph = generateStringLabelGraph(6, 14, true);
            System.out.println("Generated random connected graph:");
            randomGraph.printGraph();

            // Visualize the graph
            javax.swing.SwingUtilities.invokeLater(() -> new DirectedUndirectedGraphVisualizer(randomGraph));

        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}