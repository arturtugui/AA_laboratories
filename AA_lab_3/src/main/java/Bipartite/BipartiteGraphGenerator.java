package Bipartite;

import Graph.Graph;

import java.util.*;

/**
 * Generator for random connected undirected bipartite graphs.
 * A bipartite graph has its vertices divided into two disjoint sets U and V,
 * such that every edge connects a vertex in U to a vertex in V.
 */
public class BipartiteGraphGenerator {

    /**
     * Generates a random connected undirected bipartite graph.
     *
     * @param n Total number of vertices
     * @param m Number of edges
     * @param sizeOfU Size of the first partition (set U)
     * @param <V> Type of vertex labels
     * @return A connected undirected bipartite graph
     * @throws IllegalArgumentException if parameters don't allow for a connected bipartite graph
     */
    public static <V> Graph<V> generateConnectedBipartiteGraph(int n, int m, int sizeOfU, V[] vertexLabels) {
        // Validate input parameters
        validateParameters(n, m, sizeOfU);

        // Calculate size of set V
        int sizeOfV = n - sizeOfU;

        // Create graph
        Graph<V> graph = new Graph<V>(false); // Undirected graph

        // Add all vertices to the graph
        if (vertexLabels == null || vertexLabels.length < n) {
            throw new IllegalArgumentException("Vertex labels array must contain at least " + n + " elements");
        }

        // Create sets U and V with their corresponding vertices
        List<V> setU = new ArrayList<>(sizeOfU);
        List<V> setV = new ArrayList<>(sizeOfV);

        // Add first sizeOfU vertices to set U
        for (int i = 0; i < sizeOfU; i++) {
            V vertex = vertexLabels[i];
            graph.addVertex(vertex);
            setU.add(vertex);
        }

        // Add remaining vertices to set V
        for (int i = sizeOfU; i < n; i++) {
            V vertex = vertexLabels[i];
            graph.addVertex(vertex);
            setV.add(vertex);
        }

        // First, create a spanning tree to ensure connectivity
        // For a bipartite graph, our spanning tree must alternate between U and V
        createBipartiteSpanningTree(graph, setU, setV);

        // Calculate how many edges we've already added (n-1 for spanning tree)
        int addedEdges = n - 1;

        // If we need more edges than the spanning tree, add random bipartite edges
        if (m > addedEdges) {
            addRandomBipartiteEdges(graph, setU, setV, m - addedEdges);
        }

        return graph;
    }

    /**
     * Creates a spanning tree that connects all vertices in a bipartite fashion
     */
    private static <V> void createBipartiteSpanningTree(Graph<V> graph, List<V> setU, List<V> setV) {
        Random random = new Random();
        Set<V> includedVertices = new HashSet<>();

        // Start with a random vertex from set U
        V startVertex = setU.get(random.nextInt(setU.size()));
        includedVertices.add(startVertex);

        // Alternate between sets until all vertices are included
        while (includedVertices.size() < setU.size() + setV.size()) {
            // Find which vertices we still need to include from each set
            List<V> remainingU = new ArrayList<>();
            for (V v : setU) {
                if (!includedVertices.contains(v)) {
                    remainingU.add(v);
                }
            }

            List<V> remainingV = new ArrayList<>();
            for (V v : setV) {
                if (!includedVertices.contains(v)) {
                    remainingV.add(v);
                }
            }

            // Choose a source vertex (already in the tree) and a target vertex (not in the tree)
            V source, target;

            if (!remainingU.isEmpty() && !remainingV.isEmpty()) {
                // We have vertices left in both sets - choose randomly which set to add from
                if (random.nextBoolean()) {
                    // Add from set U
                    target = remainingU.get(random.nextInt(remainingU.size()));
                    // Choose a random vertex from set V that's already included
                    List<V> includedFromV = getIncludedVertices(setV, includedVertices);
                    if (includedFromV.isEmpty()) {
                        // If no vertices from V are included yet, we need to add one first
                        target = remainingV.get(random.nextInt(remainingV.size()));
                        List<V> includedFromU = getIncludedVertices(setU, includedVertices);
                        source = includedFromU.get(random.nextInt(includedFromU.size()));
                    } else {
                        source = includedFromV.get(random.nextInt(includedFromV.size()));
                    }
                } else {
                    // Add from set V
                    target = remainingV.get(random.nextInt(remainingV.size()));
                    // Choose a random vertex from set U that's already included
                    List<V> includedFromU = getIncludedVertices(setU, includedVertices);
                    if (includedFromU.isEmpty()) {
                        // If no vertices from U are included yet, we need to add one first
                        target = remainingU.get(random.nextInt(remainingU.size()));
                        List<V> includedFromV = getIncludedVertices(setV, includedVertices);
                        source = includedFromV.get(random.nextInt(includedFromV.size()));
                    } else {
                        source = includedFromU.get(random.nextInt(includedFromU.size()));
                    }
                }
            } else if (!remainingU.isEmpty()) {
                // Only vertices from U remain
                target = remainingU.get(random.nextInt(remainingU.size()));
                List<V> includedFromV = getIncludedVertices(setV, includedVertices);
                source = includedFromV.get(random.nextInt(includedFromV.size()));
            } else {
                // Only vertices from V remain
                target = remainingV.get(random.nextInt(remainingV.size()));
                List<V> includedFromU = getIncludedVertices(setU, includedVertices);
                source = includedFromU.get(random.nextInt(includedFromU.size()));
            }

            // Add the edge to the graph
            graph.addEdge(source, target);
            includedVertices.add(target);
        }
    }

    /**
     * Gets a list of vertices from a set that are already included in the tree
     */
    private static <V> List<V> getIncludedVertices(List<V> set, Set<V> includedVertices) {
        List<V> included = new ArrayList<>();
        for (V v : set) {
            if (includedVertices.contains(v)) {
                included.add(v);
            }
        }
        return included;
    }

    /**
     * Adds random bipartite edges to the graph
     */
    private static <V> void addRandomBipartiteEdges(Graph<V> graph, List<V> setU, List<V> setV, int edgesToAdd) {
        Random random = new Random();

        // Generate all possible edges between sets U and V
        List<EdgePair<V>> possibleEdges = new ArrayList<>();

        for (V u : setU) {
            for (V v : setV) {
                // Skip if edge already exists
                if (!edgeExists(graph, u, v)) {
                    possibleEdges.add(new EdgePair<>(u, v));
                }
            }
        }

        // Shuffle possible edges
        Collections.shuffle(possibleEdges);

        // Add edges until we reach the desired number or run out of possible edges
        int added = 0;
        for (EdgePair<V> edge : possibleEdges) {
            if (added >= edgesToAdd) {
                break;
            }

            graph.addEdge(edge.from, edge.to);
            added++;
        }

        // If we couldn't add enough edges, throw an exception
        if (added < edgesToAdd) {
            throw new IllegalArgumentException("Could not add " + edgesToAdd +
                    " additional edges. Only " + added + " edges were available.");
        }
    }

    /**
     * Validates the input parameters for the bipartite graph generation
     */
    private static void validateParameters(int n, int m, int sizeOfU) {
        if (n < 2) {
            throw new IllegalArgumentException("Graph.Graph must have at least 2 vertices");
        }

        if (sizeOfU < 1 || sizeOfU >= n) {
            throw new IllegalArgumentException("Size of set U must be between 1 and n-1");
        }

        int sizeOfV = n - sizeOfU;

        // Minimum edges needed for a connected bipartite graph is n-1 (spanning tree)
        int minEdges = n - 1;

        // Maximum possible edges in a bipartite graph is |U| * |V|
        int maxEdges = sizeOfU * sizeOfV;

        if (m < minEdges) {
            throw new IllegalArgumentException("At least " + minEdges +
                    " edges needed for a connected graph with " + n + " vertices");
        }

        if (m > maxEdges) {
            throw new IllegalArgumentException("Too many edges requested. Maximum is " +
                    maxEdges + " for a bipartite graph with sets of size " + sizeOfU +
                    " and " + sizeOfV);
        }
    }

    /**
     * Helper method to check if an edge exists in the graph
     */
    private static <V> boolean edgeExists(Graph<V> graph, V from, V to) {
        Map<V, List<V>> adjacencyList = graph.getAdjacencyList();
        return adjacencyList.get(from).contains(to);
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
     * Convenience method for generating a bipartite graph with String labels
     */
    public static Graph<String> generateStringLabelBipartiteGraph(int n, int m, int sizeOfU) {
        String[] labels = new String[n];

        // Create labels for set U: U1, U2, U3, ...
        for (int i = 0; i < sizeOfU; i++) {
            labels[i] = "U" + (i + 1);
        }

        // Create labels for set V: V1, V2, V3, ...
        for (int i = sizeOfU; i < n; i++) {
            labels[i] = "V" + (i - sizeOfU + 1);
        }

        return generateConnectedBipartiteGraph(n, m, sizeOfU, labels);
    }

    @SuppressWarnings("unchecked")
    public static <V> Set<V>[] getBipartitePartitions(Graph<V> graph) {
        Set<V> setU = new HashSet<>();
        Set<V> setV = new HashSet<>();

        for (V vertex : graph.getVertices()) {
            String name = vertex.toString();
            if (name.startsWith("U")) {
                setU.add(vertex);
            } else if (name.startsWith("V")) {
                setV.add(vertex);
            }
        }

        return new Set[]{setU, setV};
    }

    public static void main(String[] args) {
        try {
            // Generate a bipartite graph with 10 vertices (4 in set U, 6 in set V) and 15 edges
            Graph<String> bipartiteGraph = generateStringLabelBipartiteGraph(9, 12, 3);

            System.out.println("Generated random connected bipartite graph:");
            bipartiteGraph.printGraph();

            Set<String>[] partitions = getBipartitePartitions(bipartiteGraph);
            System.out.println("Set U: " + partitions[0]);
            System.out.println("Set V: " + partitions[1]);
            javax.swing.SwingUtilities.invokeLater(() ->
                    BipartiteGraphVisualizer.visualizeBipartiteGraph(bipartiteGraph, partitions[0], partitions[1]));

            Graph.analyzeGraphComponents(bipartiteGraph);

        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}