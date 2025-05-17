package DFS;

import DirectedAndUndirected.DirectedUndirectedGraphVisualizer;
import Graph.Graph;

import javax.swing.*;
import java.util.*;

import static Bipartite.BipartiteGraphGenerator.generateStringLabelBipartiteGraph;
import static DirectedAndUndirected.DirectedUndirectedGraphGenerator.generateStringLabelGraph;
import static KRegular.KRegularGraphGenerator.generateStringLabelKRegularGraph;

public class DepthFirstSearch {

    public static <V> int dfs(Graph<V> graph, V startNode) {
        Set<V> visited = new HashSet<>();
        Stack<V> stack = new Stack<>();
        int maxStackSize;

        visited.add(startNode);
        stack.push(startNode);
        maxStackSize = 1;

        while (!stack.isEmpty()) {
            V current = stack.pop();

            List<V> neighbors = graph.getAdjacencyList().getOrDefault(current, new ArrayList<>());

            for (int i = neighbors.size() - 1; i >= 0; i--) {
                V neighbor = neighbors.get(i);
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    stack.push(neighbor);
                    maxStackSize = Math.max(maxStackSize, stack.size());
                }
            }
        }

        return maxStackSize;
    }

    public static <V> int dfsWithOutput(Graph<V> graph, V startNode) {
        Set<V> visited = new HashSet<>();
        Stack<V> stack = new Stack<>();
        int maxStackSize = 0;

        visited.add(startNode);
        stack.push(startNode);
        maxStackSize = 1;

        while (!stack.isEmpty()) {
            V current = stack.pop();
            System.out.print(current + " ");

            // Get all neighbors
            List<V> neighbors = graph.getAdjacencyList().getOrDefault(current, new ArrayList<>());

            // Process neighbors in reverse order
            for (int i = neighbors.size() - 1; i >= 0; i--) {
                V neighbor = neighbors.get(i);
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    stack.push(neighbor);
                    maxStackSize = Math.max(maxStackSize, stack.size());
                }
            }
        }

        // For debugging: print the number of visited nodes vs total nodes
        System.out.println("\nVisited " + visited.size() + " nodes out of " + graph.getAdjacencyList().size() + " total nodes");

        return maxStackSize;
    }

    public static void main(String[] args) {
        //undirected
        int n = 5000;
        //Graph<String> graph = generateStringLabelGraph(n, (n*(n-1))/2, false);
        //Graph<String> graph = generateStringLabelGraph(n, (n-1), false);

        System.out.println("\n\nUndirected:");
        Graph<String> graph = generateStringLabelGraph(6, 10, false);
        graph.printGraph();
        System.out.println("DFS traversal starting from node A:");
        int maxStackSize = dfsWithOutput(graph, "A");
        System.out.println("Maximum stack size during DFS: " + maxStackSize);
        SwingUtilities.invokeLater(() -> new DirectedUndirectedGraphVisualizer(graph));
        SwingUtilities.invokeLater(() -> DFSVisualizer.visualizeDFS(graph, "A"));

//        System.out.println("\n\nDirected:");
//        //directed
//        graph = generateStringLabelGraph(6, 30, true);
//        graph.printGraph();
//        System.out.println("DFS traversal starting from node A:");
//        maxStackSize = dfsWithOutput(graph, "A");
//        System.out.println("Maximum stack size during DFS: " + maxStackSize);
//        SwingUtilities.invokeLater(() -> new DirectedUndirectedGraphVisualizer(graph));

//        System.out.println("\n\nBipartite:");
//        //bipartite
//        graph = generateStringLabelBipartiteGraph(9, 18, 3);
//        graph.printGraph();
////
////        Set<String>[] partitions = getBipartitePartitions(graph);
////        System.out.println("Set U: " + partitions[0]);
////        System.out.println("Set V: " + partitions[1]);
////        javax.swing.SwingUtilities.invokeLater(() ->
////                BipartiteGraphVisualizer.visualizeBipartiteGraph(graph, partitions[0], partitions[1]));
////
////        //for bipartite
//        System.out.println("DFS traversal starting from node U1:");
//        maxStackSize = dfsWithOutput(graph, "U1");
//        System.out.println("Maximum stack size during DFS: " + maxStackSize);
//
//        System.out.println("\n\nK-regular:");
//        graph = generateStringLabelKRegularGraph(10, 3);
//        graph.printGraph();
//
//        System.out.println("DFS traversal starting from node A:");
//        maxStackSize = dfsWithOutput(graph, "A");
//        System.out.println("Maximum stack size during DFS: " + maxStackSize);





        // If not all nodes were visited, try to identify disconnected components
//        if (graph.getAdjacencyList().size() > 0) {
//            Set<String> allNodes = new HashSet<>(graph.getAdjacencyList().keySet());
//            Set<String> visited = new HashSet<>();
//
//            // Use the corrected DFS to visit all nodes in each component
//            for (String node : allNodes) {
//                if (!visited.contains(node)) {
//                    System.out.println("\nStarting new component from node " + node + ":");
//                    // This is a simplified DFS just to identify components
//                    Set<String> componentNodes = new HashSet<>();
//                    findConnectedComponent(graph, node, componentNodes);
//                    visited.addAll(componentNodes);
//                    System.out.println("Component size: " + componentNodes.size() + " nodes");
//                }
//            }
//        }
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
