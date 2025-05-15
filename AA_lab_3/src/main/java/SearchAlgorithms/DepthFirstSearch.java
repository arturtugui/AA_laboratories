package SearchAlgorithms;

import DirectedAndUndirected.DirectedUndirectedGraphVisualizer;
import Graph.Graph;

import javax.swing.*;
import java.util.*;

import static DirectedAndUndirected.DirectedUndirecrtedGraphGenerator.generateStringLabelGraph;

public class DepthFirstSearch {

    public static <V> int dfs(Graph<V> graph, V startNode) {
        Set<V> visited = new HashSet<>();
        Stack<V> stack = new Stack<>();
        int maxStackSize = 0;

        // First push the start node to the stack (but don't mark as visited yet)
        stack.push(startNode);
        maxStackSize = 1;

        while (!stack.isEmpty()) {
            V current = stack.pop();

            // Only process the node if we haven't visited it yet
            if (!visited.contains(current)) {
                System.out.print(current + " ");
                visited.add(current);

                // Get all neighbors
                List<V> neighbors = graph.getAdjacencyList().getOrDefault(current, new ArrayList<>());

                // Process neighbors in reverse order to maintain the expected DFS traversal order
                for (int i = neighbors.size() - 1; i >= 0; i--) {
                    V neighbor = neighbors.get(i);
                    if (!visited.contains(neighbor)) {
                        stack.push(neighbor);
                        maxStackSize = Math.max(maxStackSize, stack.size());
                    }
                }
            }
        }

        // For debugging: print the number of visited nodes vs total nodes
        System.out.println("\nVisited " + visited.size() + " nodes out of " + graph.getAdjacencyList().size() + " total nodes");

        return maxStackSize;
    }

    public static void main(String[] args) {
        Graph<String> graph = generateStringLabelGraph(8, 7, false);
        graph.printGraph();
        SwingUtilities.invokeLater(() -> new DirectedUndirectedGraphVisualizer(graph));

        System.out.println("DFS traversal starting from node A:");
        int maxStackSize = dfs(graph, "A");
        System.out.println("Maximum stack size during DFS: " + maxStackSize);

        // If not all nodes were visited, try to identify disconnected components
        if (graph.getAdjacencyList().size() > 0) {
            Set<String> allNodes = new HashSet<>(graph.getAdjacencyList().keySet());
            Set<String> visited = new HashSet<>();

            // Use the corrected DFS to visit all nodes in each component
            for (String node : allNodes) {
                if (!visited.contains(node)) {
                    System.out.println("\nStarting new component from node " + node + ":");
                    // This is a simplified DFS just to identify components
                    Set<String> componentNodes = new HashSet<>();
                    findConnectedComponent(graph, node, componentNodes);
                    visited.addAll(componentNodes);
                    System.out.println("Component size: " + componentNodes.size() + " nodes");
                }
            }
        }
    }

    // Helper method to find all nodes in a connected component
    private static void findConnectedComponent(Graph<String> graph, String startNode, Set<String> visited) {
        Stack<String> stack = new Stack<>();
        stack.push(startNode);

        while (!stack.isEmpty()) {
            String current = stack.pop();
            if (!visited.contains(current)) {
                System.out.print(current + " ");
                visited.add(current);

                List<String> neighbors = graph.getAdjacencyList().getOrDefault(current, new ArrayList<>());
                for (String neighbor : neighbors) {
                    if (!visited.contains(neighbor)) {
                        stack.push(neighbor);
                    }
                }
            }
        }
    }
}
