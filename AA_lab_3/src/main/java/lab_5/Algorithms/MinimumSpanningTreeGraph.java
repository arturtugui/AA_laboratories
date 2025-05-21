package lab_5.Algorithms;

import lab_3.DirectedAndUndirected.DirectedUndirectedGraphGenerator;
import lab_3.Graph.Graph;
import lab_4.WeightedGraph.WeightedEdge;
import lab_4.WeightedGraph.WeightedGraph;

import java.util.*;

import static lab_4.WeightedGraph.GraphToWeightedGraphConverter.convertToWeightedGraph;

/**
 * Extension of WeightedGraph that includes minimum spanning tree functionality
 * using Prim's algorithm
 */
public class MinimumSpanningTreeGraph<V> extends WeightedGraph<V> {
    // Additional fields for the MST
    private final Map<V, List<WeightedEdge<V>>> mstAdjacencyList;
    private double mstTotalCost;
    private boolean mstComputed;

    /**
     * Class used for priority queue entries in Prim's algorithm
     */
    private static class HeapNode<V> implements Comparable<HeapNode<V>> {
        V vertex;
        V parent;
        double weight;

        public HeapNode(V vertex, V parent, double weight) {
            this.vertex = vertex;
            this.parent = parent;
            this.weight = weight;
        }

        @Override
        public int compareTo(HeapNode<V> other) {
            return Double.compare(this.weight, other.weight);
        }
    }

    /**
     * Constructor for MinimumSpanningTreeGraph that creates it from an existing WeightedGraph
     *
     * @param graph the WeightedGraph to convert to a MinimumSpanningTreeGraph
     */
    public MinimumSpanningTreeGraph(WeightedGraph<V> graph) {
        super(graph.isDirected());
        this.mstAdjacencyList = new HashMap<>();
        this.mstTotalCost = 0;
        this.mstComputed = false;

        // Copy all vertices and edges from the original graph
        for (V vertex : graph.getVertices()) {
            addVertex(vertex);
        }

        // Copy all edges
        for (V source : graph.getVertices()) {
            for (WeightedEdge<V> edge : graph.getNeighbors(source)) {
                // To avoid duplicate edges in undirected graphs
                if (!graph.isDirected() && source.toString().compareTo(edge.target.toString()) > 0) {
                    continue;
                }
                addEdge(source, edge.target, edge.weight);
            }
        }
    }

    /**
     * Computes the minimum spanning tree starting from the given vertex
     *
     * @param startVertex the vertex to start the MST computation from
     * @return the total cost of the MST
     * @throws IllegalArgumentException if the startVertex is not in the graph
     */
    public double computePrimMST(V startVertex) {
        if (!hasVertex(startVertex)) {
            throw new IllegalArgumentException("Start vertex not found in the graph");
        }
        mstAdjacencyList.clear();
        mstTotalCost = 0;

        for (V vertex : getVertices()) {
            mstAdjacencyList.put(vertex, new ArrayList<>());
        }

        Set<V> visited = new HashSet<>();
        Set<V> unvisited = new HashSet<>(getVertices());

        PriorityQueue<HeapNode<V>> minHeap = new PriorityQueue<>();

        visited.add(startVertex);
        unvisited.remove(startVertex);

        for (WeightedEdge<V> edge : getNeighbors(startVertex)) {
            minHeap.add(new HeapNode<>(edge.target, startVertex, edge.weight));
        }

        while (!unvisited.isEmpty() && !minHeap.isEmpty()) {
            HeapNode<V> node = minHeap.poll();
            V currentVertex = node.vertex;
            V parentVertex = node.parent;
            double weight = node.weight;

            if (visited.contains(currentVertex)) {
                continue;
            }

            mstAdjacencyList.get(parentVertex).add(new WeightedEdge<>(currentVertex, weight));
            if (!isDirected()) {
                mstAdjacencyList.get(currentVertex).add(new WeightedEdge<>(parentVertex, weight));
            }

            mstTotalCost += weight;

            visited.add(currentVertex);
            unvisited.remove(currentVertex);

            for (WeightedEdge<V> edge : getNeighbors(currentVertex)) {
                if (unvisited.contains(edge.target)) {
                    minHeap.add(new HeapNode<>(edge.target, currentVertex, edge.weight));
                }
            }
        }

        if (!unvisited.isEmpty()) {
            System.out.println("Warning: Graph is not connected. MST does not span all vertices.");
        }
        mstComputed = true;
        return mstTotalCost;
    }

    /**
     * Prints the minimum spanning tree
     *
     * @throws IllegalStateException if the MST has not been computed yet
     */
    public void printMST() {
        if (!mstComputed) {
            throw new IllegalStateException("MST has not been computed yet");
        }

        System.out.println("Minimum Spanning Tree:");
        for (V vertex : mstAdjacencyList.keySet()) {
            System.out.println(vertex + " -> " + mstAdjacencyList.get(vertex));
        }
        System.out.println("Total MST Cost: " + mstTotalCost);
    }

    /**
     * Creates a new WeightedGraph that represents just the MST
     *
     * @return a new WeightedGraph containing only the MST edges
     * @throws IllegalStateException if the MST has not been computed yet
     */
    public WeightedGraph<V> getMSTAsGraph() {
        if (!mstComputed) {
            throw new IllegalStateException("MST has not been computed yet");
        }

        WeightedGraph<V> mstGraph = new WeightedGraph<>(isDirected());

        // Add all vertices
        for (V vertex : mstAdjacencyList.keySet()) {
            mstGraph.addVertex(vertex);
        }

        // Add all edges, avoiding duplicates in undirected graphs
        Set<String> addedEdges = new HashSet<>();
        for (V source : mstAdjacencyList.keySet()) {
            for (WeightedEdge<V> edge : mstAdjacencyList.get(source)) {
                V destination = edge.target;
                double weight = edge.weight;

                // For undirected graphs, ensure we don't add the same edge twice
                String edgeStr = source.toString().compareTo(destination.toString()) < 0 ?
                        source + "-" + destination : destination + "-" + source;

                if (isDirected() || !addedEdges.contains(edgeStr)) {
                    mstGraph.addEdge(source, destination, weight);
                    addedEdges.add(edgeStr);
                }
            }
        }

        return mstGraph;
    }

    /**
     * Checks if the MST has been computed
     *
     * @return true if the MST has been computed, false otherwise
     */
    public boolean isMSTComputed() {
        return mstComputed;
    }








    /**
     * Computes the minimum spanning tree using Kruskal's algorithm
     *
     * @return the total cost of the MST
     */
    public double computeKruskalMST() {
        mstAdjacencyList.clear();
        mstTotalCost = 0;

        for (V vertex : getVertices()) {
            mstAdjacencyList.put(vertex, new ArrayList<>());
        }

        List<EdgeTriple<V>> edges = new ArrayList<>();
        for (V source : getVertices()) {
            for (WeightedEdge<V> edge : getNeighbors(source)) {
                if (!isDirected() && source.toString().compareTo(edge.target.toString()) > 0) {
                    continue;
                }
                edges.add(new EdgeTriple<>(source, edge.target, edge.weight));
            }
        }

        Collections.sort(edges);

        DisjointSets<V> sets = new DisjointSets<>(getVertices());

        for (EdgeTriple<V> edge : edges) {
            V source = edge.source;
            V target = edge.target;
            double weight = edge.weight;

            if (!sets.isSameSet(source, target)) {
                mstAdjacencyList.get(source).add(new WeightedEdge<>(target, weight));
                if (!isDirected()) {
                    mstAdjacencyList.get(target).add(new WeightedEdge<>(source, weight));
                }

                mstTotalCost += weight;

                sets.union(source, target);
            }
        }

        mstComputed = true;
        return mstTotalCost;
    }

    /**
     * Helper class to store an edge with its source, target and weight
     */
    private static class EdgeTriple<V> implements Comparable<EdgeTriple<V>> {
        V source;
        V target;
        double weight;

        public EdgeTriple(V source, V target, double weight) {
            this.source = source;
            this.target = target;
            this.weight = weight;
        }

        @Override
        public int compareTo(EdgeTriple<V> other) {
            return Double.compare(this.weight, other.weight);
        }
    }

    /**
     * Disjoint Sets (Union-Find) implementation for Kruskal's algorithm
     */
    private static class DisjointSets<V> {
        private final Map<V, V> parent;
        private final Map<V, Integer> rank;

        public DisjointSets(Set<V> vertices) {
            parent = new HashMap<>();
            rank = new HashMap<>();

            // Initialize each vertex as a separate set
            for (V vertex : vertices) {
                parent.put(vertex, vertex);
                rank.put(vertex, 0);
            }
        }

        /**
         * Find the representative of the set containing the given element
         * with path compression
         */
        public V find(V vertex) {
            if (!parent.get(vertex).equals(vertex)) {
                parent.put(vertex, find(parent.get(vertex)));
            }
            return parent.get(vertex);
        }

        /**
         * Union two sets by rank
         */
        public void union(V x, V y) {
            V rootX = find(x);
            V rootY = find(y);

            if (rootX.equals(rootY)) return;

            // Attach smaller rank tree under root of high rank tree
            if (rank.get(rootX) < rank.get(rootY)) {
                parent.put(rootX, rootY);
            } else if (rank.get(rootX) > rank.get(rootY)) {
                parent.put(rootY, rootX);
            } else {
                // If ranks are same, make one as root and increment its rank
                parent.put(rootY, rootX);
                rank.put(rootX, rank.get(rootX) + 1);
            }
        }

        /**
         * Check if two elements are in the same set
         */
        public boolean isSameSet(V x, V y) {
            return find(x).equals(find(y));
        }
    }

    // Example code for your main method
    public static void main(String[] args) {
        Graph<String> graph = DirectedUndirectedGraphGenerator.generateStringLabelGraph(6, 9, false);
        WeightedGraph<String> weightedGraph = convertToWeightedGraph(graph);
        System.out.println("\nInitial graph:");
        weightedGraph.printGraph();
        lab_4.WeightedGraph.Visualizer.visualizeDirectedAndUndirected(weightedGraph);

        //Prim
        MinimumSpanningTreeGraph<String> mstGraph = new MinimumSpanningTreeGraph<>(weightedGraph);
        String startVertex = mstGraph.getVertices().iterator().next();
        //Kruskal
        MinimumSpanningTreeGraph<String> mstGraph2 = new MinimumSpanningTreeGraph<>(weightedGraph);


        double totalCost = mstGraph.computePrimMST(startVertex);
        System.out.println("\nPrim algorithm:");
        mstGraph.printMST();
        lab_4.WeightedGraph.Visualizer.visualizeDirectedAndUndirected(mstGraph.getMSTAsGraph());

        double kruskalCost = mstGraph.computeKruskalMST();
        System.out.println("\nKruskal algorithm:");
        mstGraph.printMST();
        lab_4.WeightedGraph.Visualizer.visualizeDirectedAndUndirected(mstGraph.getMSTAsGraph());
    }
}
