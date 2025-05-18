package lab_4.Dijkstra;

import lab_3.Bipartite.BipartiteGraphGenerator;
import lab_3.DirectedAndUndirected.DirectedUndirectedGraphGenerator;
import lab_3.Graph.Graph;
import lab_3.KRegular.KRegularGraphGenerator;
import lab_4.WeightedGraph.Visualizer;
import lab_4.WeightedGraph.WeightedGraph;

import static lab_4.WeightedGraph.GraphToWeightedGraphConverter.convertToWeightedGraph;

public class TestingDijkstra {
    public static void main(String[] args) {
        // undirected
        Graph<String> graph = DirectedUndirectedGraphGenerator.generateStringLabelGraph(6, 9, false);
        WeightedGraph<String> weightedGraph = convertToWeightedGraph(graph);
        lab_4.WeightedGraph.Visualizer.visualizeDirectedAndUndirected(weightedGraph);

        DijkstraAlgorithm<String> dijkstra1 = new DijkstraAlgorithm<>(weightedGraph);
        dijkstra1.printShortestPaths("A");

        // directed
        graph = DirectedUndirectedGraphGenerator.generateStringLabelGraph(5, 15, true);
        weightedGraph = convertToWeightedGraph(graph);
        lab_4.WeightedGraph.Visualizer.visualizeDirectedAndUndirected(weightedGraph);
        dijkstra1 = new DijkstraAlgorithm<>(weightedGraph);
        dijkstra1.printShortestPaths("A");

        // undirected bipartite
        Graph<String> bipartiteGraph = BipartiteGraphGenerator.generateStringLabelBipartiteGraph(7, 9, 3);
        WeightedGraph<String> weightedBipartiteGraph = convertToWeightedGraph(bipartiteGraph);
        Visualizer.visualizeBipartite(weightedBipartiteGraph);
        dijkstra1 = new DijkstraAlgorithm<>(weightedBipartiteGraph);
        dijkstra1.printShortestPaths("U1");

        // undirected weighted
        Graph<String> kgraph = KRegularGraphGenerator.generateStringLabelKRegularGraph(6, 3);
        WeightedGraph<String> weightedkGraph = convertToWeightedGraph(kgraph);
        lab_4.WeightedGraph.Visualizer.visualizeDirectedAndUndirected(weightedkGraph);
        dijkstra1 = new DijkstraAlgorithm<>(weightedkGraph);
        dijkstra1.printShortestPaths("A");
    }
}
