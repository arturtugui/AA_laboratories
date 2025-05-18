package lab_4.WeightedGraph;

import lab_4.DirectedAndUndirectedWeighted.DirectedAndUndirectedWeightedVisualizer;

import javax.swing.*;
import java.util.Set;

import static lab_4.BipartiteWeighted.BipartiteWeightedVisualizer.getBipartiteWeightedPartitions;
import static lab_4.BipartiteWeighted.BipartiteWeightedVisualizer.visualizeBipartiteWeightedGraph;

public class Visualizer {
    public static void visualizeDirectedAndUndirected(WeightedGraph<String> graph){
        SwingUtilities.invokeLater(() -> new DirectedAndUndirectedWeightedVisualizer(graph));
    }

    public static void visualizeBipartite(WeightedGraph<String> bipartiteGraph){
        Set<String>[] partitions = getBipartiteWeightedPartitions(bipartiteGraph);
        SwingUtilities.invokeLater(() -> visualizeBipartiteWeightedGraph(bipartiteGraph, partitions[0], partitions[1]));
    }
}
