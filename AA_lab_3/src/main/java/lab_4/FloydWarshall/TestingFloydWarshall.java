package lab_4.FloydWarshall;

import lab_3.Bipartite.BipartiteGraphGenerator;
import lab_3.DirectedAndUndirected.DirectedUndirectedGraphGenerator;
import lab_3.Graph.Graph;
import lab_3.KRegular.KRegularGraphGenerator;
import lab_4.WeightedGraph.Visualizer;
import lab_4.WeightedGraph.WeightedGraph;

import java.util.Map;

import static lab_4.FloydWarshall.FloydWarshall.findAllPairsShortestPaths;
import static lab_4.FloydWarshall.FloydWarshall.printAllShortestPaths;
import static lab_4.WeightedGraph.GraphToWeightedGraphConverter.convertToWeightedGraph;

public class TestingFloydWarshall {
    public static void main(String[] args) {
        // undirected
        Graph<String> graph = DirectedUndirectedGraphGenerator.generateStringLabelGraph(6, 9, false);
        WeightedGraph<String> weightedGraph = convertToWeightedGraph(graph);
        lab_4.WeightedGraph.Visualizer.visualizeDirectedAndUndirected(weightedGraph);

        Map<String, double[][]> result = findAllPairsShortestPaths(weightedGraph);
        printAllShortestPaths(weightedGraph, result);

        // directed
        graph = DirectedUndirectedGraphGenerator.generateStringLabelGraph(5, 15, true);
        weightedGraph = convertToWeightedGraph(graph);
        lab_4.WeightedGraph.Visualizer.visualizeDirectedAndUndirected(weightedGraph);
        Map<String, double[][]> result2 = findAllPairsShortestPaths(weightedGraph);
        printAllShortestPaths(weightedGraph, result2);

        // undirected bipartite
        Graph<String> bipartiteGraph = BipartiteGraphGenerator.generateStringLabelBipartiteGraph(7, 9, 3);
        WeightedGraph<String> weightedBipartiteGraph = convertToWeightedGraph(bipartiteGraph);
        Visualizer.visualizeBipartite(weightedBipartiteGraph);
        Map<String, double[][]> result3 = findAllPairsShortestPaths(weightedBipartiteGraph);
        printAllShortestPaths(weightedBipartiteGraph, result3);

        // undirected weighted
        Graph<String> kgraph = KRegularGraphGenerator.generateStringLabelKRegularGraph(6, 3);
        WeightedGraph<String> weightedkGraph = convertToWeightedGraph(kgraph);
        lab_4.WeightedGraph.Visualizer.visualizeDirectedAndUndirected(weightedkGraph);
        Map<String, double[][]> result4 = findAllPairsShortestPaths(weightedkGraph);
        printAllShortestPaths(weightedkGraph, result4);
    }
}
