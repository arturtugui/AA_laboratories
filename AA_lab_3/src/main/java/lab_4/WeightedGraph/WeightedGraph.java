package lab_4.WeightedGraph;

import java.util.*;

import lab_4.WeightedGraph.WeightedEdge;

public class WeightedGraph<V> {
    private final Map<V, List<WeightedEdge<V>>> adjacencyList;
    private final boolean isDirected;

    public WeightedGraph(boolean isDirected) {
        this.adjacencyList = new HashMap<>();
        this.isDirected = isDirected;
    }

    public void addVertex(V vertex) {
        adjacencyList.putIfAbsent(vertex, new ArrayList<>());
    }

    public void addEdge(V from, V to, double weight) {
        adjacencyList.putIfAbsent(from, new ArrayList<>());
        adjacencyList.putIfAbsent(to, new ArrayList<>());

        adjacencyList.get(from).add(new WeightedEdge<>(to, weight));
        if (!isDirected) {
            adjacencyList.get(to).add(new WeightedEdge<>(from, weight));
        }
    }

    public List<WeightedEdge<V>> getNeighbors(V vertex) {
        return adjacencyList.getOrDefault(vertex, new ArrayList<>());
    }

    public Set<V> getVertices() {
        return adjacencyList.keySet();
    }

    public void printGraph() {
        for (V vertex : adjacencyList.keySet()) {
            System.out.println(vertex + " -> " + adjacencyList.get(vertex));
        }
    }

    public static void main(String[] args) {
        WeightedGraph<String> graph = new WeightedGraph<String>(false);

        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");

        graph.addEdge("A", "B", 1.5);
        graph.addEdge("A", "C", 2.0);
        graph.addEdge("B", "C", 2.5);

        graph.printGraph();
    }

    public boolean isDirected() {
        return this.isDirected;
    }

    public boolean hasVertex(V vertex) {
        return adjacencyList.containsKey(vertex);
    }
}

