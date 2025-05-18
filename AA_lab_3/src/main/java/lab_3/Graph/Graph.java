package lab_3.Graph;

import java.util.*;

public class Graph<V> {
    private final Map<V, List<V>> adjacencyList;
    private final boolean isDirected;

    public Graph(boolean isDirected) {
        this.adjacencyList = new HashMap<>();
        this.isDirected = isDirected;
    }

    public Graph(Graph<V> other) {
        this.isDirected = other.isDirected();
        this.adjacencyList = new HashMap<>();
        for (V vertex : other.getVertices()) {
            this.adjacencyList.put(vertex, new ArrayList<>(other.getAdjacencyList().get(vertex)));
        }
    }


    public void addVertex(V vertex) {
        adjacencyList.putIfAbsent(vertex, new ArrayList<>());
    }

    public void addEdge(V from, V to) {
        adjacencyList.putIfAbsent(from, new ArrayList<>());
        adjacencyList.putIfAbsent(to, new ArrayList<>());

        adjacencyList.get(from).add(to);
    }

    public void removeEdge(V from, V to) {
        List<V> fromList = adjacencyList.get(from);
        if (fromList != null) {
            fromList.remove(to);
        }

        if (!isDirected) {
            List<V> toList = adjacencyList.get(to);
            if (toList != null) {
                toList.remove(from);
            }
        }
    }

    public Set<V> getVertices() {
        return adjacencyList.keySet();
    }

    public Map<V, List<V>> getAdjacencyList() {
        return adjacencyList;
    }

    public boolean isDirected() {
        return isDirected;
    }

    public void printGraph() {
        for(V vertex : adjacencyList.keySet()) {
            System.out.println(vertex + " -> " + adjacencyList.get(vertex));
        }
    }

    public static <V> List<Set<V>> findDisconnectedComponents(Graph<V> graph) {
        List<Set<V>> components = new ArrayList<>();
        Set<V> allNodes = new HashSet<>(graph.getAdjacencyList().keySet());
        Set<V> visited = new HashSet<>();

        // Iterate through all nodes in the graph
        for (V node : allNodes) {
            // If we haven't visited this node yet, it belongs to a new component
            if (!visited.contains(node)) {
                Set<V> componentNodes = new HashSet<>();
                // Find all nodes in this connected component
                findConnectedComponent(graph, node, componentNodes);
                // Add all nodes from this component to our visited set
                visited.addAll(componentNodes);
                // Add this component to our list of components
                components.add(componentNodes);
            }
        }

        return components;
    }

    private static <V> void findConnectedComponent(Graph<V> graph, V startNode, Set<V> visited) {
        Stack<V> stack = new Stack<>();
        stack.push(startNode);

        while (!stack.isEmpty()) {
            V current = stack.pop();
            if (!visited.contains(current)) {
                visited.add(current);

                List<V> neighbors = graph.getAdjacencyList().getOrDefault(current, new ArrayList<>());
                for (V neighbor : neighbors) {
                    if (!visited.contains(neighbor)) {
                        stack.push(neighbor);
                    }
                }
            }
        }
    }

    // Example usage:
    public static void analyzeGraphComponents(Graph<String> graph) {
        List<Set<String>> components = findDisconnectedComponents(graph);

        System.out.println("\nGraph Component Analysis:");
        System.out.println("Total number of components: " + components.size());

        for (int i = 0; i < components.size(); i++) {
            Set<String> component = components.get(i);
            System.out.println("Component " + (i+1) + " contains " + component.size() +
                    " nodes: " + component);
        }
    }
}
