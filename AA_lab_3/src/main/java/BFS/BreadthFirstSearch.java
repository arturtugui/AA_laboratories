package BFS;

import Graph.Graph;

import java.util.*;

import static BFS.BipartiteBFSVisualizer.visualizeBipartiteBFS;
import static Bipartite.BipartiteGraphGenerator.generateStringLabelBipartiteGraph;
import static DirectedAndUndirected.DirectedUndirectedGraphGenerator.generateStringLabelGraph;
import static KRegular.KRegularGraphGenerator.generateStringLabelKRegularGraph;

public class BreadthFirstSearch {

    public static <V> int bfs(Graph<V> graph, V startNode) {
        Set<V> visited = new HashSet<>();
        Queue<V> queue = new LinkedList<>();
        int maxQueueSize = 0;

        visited.add(startNode);
        queue.add(startNode);
        maxQueueSize = 1;

        while (!queue.isEmpty()) {
            V current = queue.poll();

            List<V> neighbors = graph.getAdjacencyList().getOrDefault(current, new ArrayList<>());
            for (V neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                    maxQueueSize = Math.max(maxQueueSize, queue.size());
                }
            }
        }

        return maxQueueSize;
    }

    public static <V> int bfsWithOutput(Graph<V> graph, V startNode) {
        Set<V> visited = new HashSet<>();
        Queue<V> queue = new LinkedList<>();
        int maxQueueSize = 0;

        visited.add(startNode);
        queue.add(startNode);
        maxQueueSize = 1;

        while (!queue.isEmpty()) {
            V current = queue.poll();
            System.out.print(current + " ");

            List<V> neighbors = graph.getAdjacencyList().getOrDefault(current, new ArrayList<>());
            for (V neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                    maxQueueSize = Math.max(maxQueueSize, queue.size());
                }
            }
        }

        // For debugging: print the number of visited nodes vs total nodes
        System.out.println("\nVisited " + visited.size() + " nodes out of " + graph.getAdjacencyList().size() + " total nodes");

        return maxQueueSize;
    }

    public static void main(String[] args) {
        Graph<String> graph;
        int maxStackSize;

//        System.out.println("\n\nUndirected:");
//        Graph<String> graph = generateStringLabelGraph(6, 15, false);
//        graph.printGraph();
//        System.out.println("DFS traversal starting from node A:");
//        int maxStackSize = bfsWithOutput(graph, "A");
//        System.out.println("Maximum stack size during DFS: " + maxStackSize);
//        //SwingUtilities.invokeLater(() -> new DirectedUndirectedGraphVisualizer(graph));
//
//        System.out.println("\n\nDirected:");
//        //directed
//        graph = generateStringLabelGraph(6, 30, true);
//        graph.printGraph();
//        System.out.println("DFS traversal starting from node A:");
//        maxStackSize = bfsWithOutput(graph, "A");
//        System.out.println("Maximum stack size during DFS: " + maxStackSize);
//        //SwingUtilities.invokeLater(() -> new DirectedUndirectedGraphVisualizer(graph));

        System.out.println("\n\nBipartite:");
        //bipartite
        graph = generateStringLabelBipartiteGraph(9, 13, 3);
        graph.printGraph();
        visualizeBipartiteBFS(graph, "U1");
//
//        Set<String>[] partitions = getBipartitePartitions(graph);
//        System.out.println("Set U: " + partitions[0]);
//        System.out.println("Set V: " + partitions[1]);
//        javax.swing.SwingUtilities.invokeLater(() ->
//                BipartiteGraphVisualizer.visualizeBipartiteGraph(graph, partitions[0], partitions[1]));
//
//        //for bipartite
        System.out.println("BFS traversal starting from node U1:");
        maxStackSize = bfsWithOutput(graph, "U1");
        System.out.println("Maximum queue size during BFS: " + maxStackSize);

//        System.out.println("\n\nK-regular:");
//        graph = generateStringLabelKRegularGraph(10, 3);
//        graph.printGraph();
//
//        System.out.println("BFS traversal starting from node A:");
//        maxStackSize = bfsWithOutput(graph, "A");
//        System.out.println("Maximum queue size during BFS: " + maxStackSize);
    }
}
