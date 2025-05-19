package lab_4.WeightedGraph;

import lab_3.Bipartite.BipartiteGraphGenerator;
import lab_3.Graph.Graph;
import lab_3.DirectedAndUndirected.DirectedUndirectedGraphGenerator;
import lab_3.KRegular.KRegularGraphGenerator;

import java.util.*;

/**
 * Utility class for converting Graph objects to WeightedGraph objects
 * with random integer weights assigned to each edge.
 */
public class GraphToWeightedGraphConverter {

    /**
     * Converts a Graph object to a WeightedGraph object with random weights.
     *
     * @param graph The Graph object to convert
     * @param minWeight The minimum weight to assign (inclusive)
     * @param maxWeight The maximum weight to assign (inclusive)
     * @param <V> Type of vertices
     * @return A WeightedGraph with the same structure and random weights
     */
    public static <V> WeightedGraph<V> convertToWeightedGraph(Graph<V> graph, int minWeight, int maxWeight) {
        if (graph == null) {
            throw new IllegalArgumentException("Graph cannot be null");
        }

        if (minWeight > maxWeight) {
            throw new IllegalArgumentException("Minimum weight cannot be greater than maximum weight");
        }

        // Create a new weighted graph with the same directedness
        WeightedGraph<V> weightedGraph = new WeightedGraph<>(graph.isDirected());
        Random random = new Random();

        // Add all vertices from the original graph
        for (V vertex : graph.getVertices()) {
            weightedGraph.addVertex(vertex);
        }

        // For undirected graphs, we need to track weights to ensure consistency
        Map<EdgePair<V>, Integer> edgeWeights = new HashMap<>();

        // Add all edges with random weights
        Map<V, List<V>> adjacencyList = graph.getAdjacencyList();
        Set<V> vertices = graph.getVertices();

        for (V from : vertices) {
            List<V> neighbors = adjacencyList.get(from);
            if (neighbors != null) {
                for (V to : neighbors) {
                    int weight;

                    if (!graph.isDirected()) {
                        // For undirected graphs, ensure the same weight for both directions
                        EdgePair<V> edge = new EdgePair<>(from, to);
                        EdgePair<V> reverseEdge = new EdgePair<>(to, from);

                        // Check if we already have a weight for this edge (in either direction)
                        if (edgeWeights.containsKey(edge)) {
                            weight = edgeWeights.get(edge);
                        } else if (edgeWeights.containsKey(reverseEdge)) {
                            weight = edgeWeights.get(reverseEdge);
                        } else {
                            // Generate a new weight
                            weight = random.nextInt(maxWeight - minWeight + 1) + minWeight;
                            // Store it for future reference
                            edgeWeights.put(edge, weight);
                        }
                    } else {
                        // For directed graphs, generate a new random weight for each edge
                        weight = random.nextInt(maxWeight - minWeight + 1) + minWeight;
                    }

                    weightedGraph.addEdge(from, to, weight);
                }
            }
        }

        return weightedGraph;
    }

    /**
     * Helper class to represent an edge pair for tracking weights in undirected graphs
     */
    private static class EdgePair<V> {
        V from;
        V to;

        public EdgePair(V from, V to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            EdgePair<?> edgePair = (EdgePair<?>) o;
            return Objects.equals(from, edgePair.from) && Objects.equals(to, edgePair.to);
        }

        @Override
        public int hashCode() {
            return Objects.hash(from, to);
        }
    }

    /**
     * Convenience method that converts a Graph to a WeightedGraph with weights between 1 and 100.
     *
     * @param graph The Graph object to convert
     * @param <V> Type of vertices
     * @return A WeightedGraph with the same structure and random weights between 1 and 100
     */
    public static <V> WeightedGraph<V> convertToWeightedGraph(Graph<V> graph) {
        return convertToWeightedGraph(graph, 1, 100);
    }

    public static void main(String[] args) {
        // undirected
        Graph<String> graph = DirectedUndirectedGraphGenerator.generateStringLabelGraph(4, 5, false);
        System.out.println("Original Undirected Graph:");
        graph.printGraph();
        lab_3.Graph.Visualizer.visualizeDirectedAndUndirected(graph);

        WeightedGraph<String> weightedGraph = convertToWeightedGraph(graph);
        System.out.println("\nWeighted Undirected Graph with random weights:");
        weightedGraph.printGraph();
        lab_4.WeightedGraph.Visualizer.visualizeDirectedAndUndirected(weightedGraph);

        // directed
        graph = DirectedUndirectedGraphGenerator.generateStringLabelGraph(5, 15, true);
        System.out.println("Original Directed  Graph:");
        graph.printGraph();
        lab_3.Graph.Visualizer.visualizeDirectedAndUndirected(graph);

        weightedGraph = convertToWeightedGraph(graph);
        System.out.println("\nWeighted Directed Graph with random weights:");
        weightedGraph.printGraph();
        lab_4.WeightedGraph.Visualizer.visualizeDirectedAndUndirected(weightedGraph);

        // undirected bipartite
        Graph<String> bipartiteGraph = BipartiteGraphGenerator.generateStringLabelBipartiteGraph(5, 5, 2);
        System.out.println("Original BipartiteGraph:");
        bipartiteGraph.printGraph();
        lab_3.Graph.Visualizer.visualizeBipartite(bipartiteGraph);

        WeightedGraph<String> weightedBipartiteGraph = convertToWeightedGraph(bipartiteGraph);
        System.out.println("\nWeighted Bipartite Graph with random weights:");
        weightedBipartiteGraph.printGraph();
        Visualizer.visualizeBipartite(weightedBipartiteGraph);

        // undirected weighted
        Graph<String> kgraph = KRegularGraphGenerator.generateStringLabelKRegularGraph(8, 3);
        System.out.println("Original Undirected Graph:");
        kgraph.printGraph();
        lab_3.Graph.Visualizer.visualizeDirectedAndUndirected(kgraph);

        WeightedGraph<String> weightedkGraph = convertToWeightedGraph(kgraph);
        System.out.println("\nWeighted Undirected Graph with random weights:");
        weightedkGraph.printGraph();
        lab_4.WeightedGraph.Visualizer.visualizeDirectedAndUndirected(weightedkGraph);
    }
}