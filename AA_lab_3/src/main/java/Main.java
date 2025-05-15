import DirectedAndUndirected.DirectedUndirectedGraphVisualizer;
import Graph.Graph;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        Graph<String> graph = new Graph<String>(true);

        graph.addEdge("A", "B");
        graph.addEdge("B", "A");
        graph.addEdge("B", "C");
        graph.addEdge("C", "A");

        SwingUtilities.invokeLater(() -> new DirectedUndirectedGraphVisualizer(graph));
    }
}
