package DFS;

import Graph.Graph;
import javax.swing.*;

import static KRegular.KRegularGraphGenerator.generateStringLabelKRegularGraph;
import static DFS.DepthFirstSearch.dfs;

/**
 * A utility class that provides methods to run DFS with visualization.
 * This class serves as an entry point to visualize the DFS algorithm in action.
 */
public class DFSVisualizationRunner {

    /**
     * Runs DFS visualization on a graph starting from a specified node.
     *
     * @param graph The graph to run DFS on
     * @param startNode The starting node for DFS traversal
     */

    public static <V> void visualizeDFS(Graph<V> graph, V startNode) {
        if (!(startNode instanceof String)) {
            throw new IllegalArgumentException("Start node must be a String for visualization");
        }

        // Cast to String for visualization
        String start = (String) startNode;

        // Launch visualization in the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            DFSVisualizer visualizer = new DFSVisualizer((Graph<String>) graph);

            // Start DFS after a short delay to ensure the GUI is ready
            Timer timer = new Timer(500, e -> visualizer.startDFSVisualization(start));
            timer.setRepeats(false);
            timer.start();
        });
    }

    /**
     * Example main method demonstrating how to use the DFS visualization
     */
    public static void main(String[] args) {
//        //undirected
//        Graph<String> graph = generateStringLabelGraph(6, 10, false);
//        graph.printGraph();
//        SwingUtilities.invokeLater(() -> new DirectedUndirectedGraphVisualizer(graph));

//        //directed
//        Graph<String> graph = generateStringLabelGraph(6, 9, true);
//        graph.printGraph();
//        SwingUtilities.invokeLater(() -> new DirectedUndirectedGraphVisualizer(graph));

        Graph<String> graph = generateStringLabelKRegularGraph(10, 3);
        graph.printGraph();

        System.out.println("DFS traversal starting from node A:");
        int maxStackSize = dfs(graph, "A");
        System.out.println("Maximum stack size during DFS: " + maxStackSize);



        // Start the visualization from node A
        visualizeDFS(graph, "A");
    }
}
