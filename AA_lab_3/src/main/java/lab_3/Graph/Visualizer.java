package lab_3.Graph;

import lab_3.Bipartite.BipartiteGraphVisualizer;
import lab_3.DirectedAndUndirected.DirectedUndirectedGraphVisualizer;

import javax.swing.*;
import java.util.Set;

import static lab_3.Bipartite.BipartiteGraphGenerator.getBipartitePartitions;

public class Visualizer {
    public static void visualizeDirectedAndUndirected(Graph<String> graph){
        SwingUtilities.invokeLater(() -> new DirectedUndirectedGraphVisualizer(graph));
    }

    public static void visualizeBipartite(Graph<String> bipartiteGraph){
        Set<String>[] partitions = getBipartitePartitions(bipartiteGraph);
        System.out.println("Set U: " + partitions[0]);
        System.out.println("Set V: " + partitions[1]);
        javax.swing.SwingUtilities.invokeLater(() ->
                BipartiteGraphVisualizer.visualizeBipartiteGraph(bipartiteGraph, partitions[0], partitions[1]));
    }
}
