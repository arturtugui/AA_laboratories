package lab_4.Mains;

import lab_4.Dijkstra.DijkstraAlgorithm;
import lab_4.WeightedGraph.WeightedGraph;

import java.util.Map;

import static lab_4.FloydWarshall.FloydWarshall.findAllPairsShortestPaths;

public class AlgorithmsHelper {
    public static Integer runDijkstra(WeightedGraph<String> graph, String startNode) {
        DijkstraAlgorithm<String> dijkstra = new DijkstraAlgorithm<>(graph);
        dijkstra.findShortestPaths(startNode);
        return 1;
    }

    public static Integer runDijkstraOnAll(WeightedGraph<String> graph, String dummyNode) {
        DijkstraAlgorithm<String> dijkstra = new DijkstraAlgorithm<>(graph);
        for(String v : graph.getVertices()){
            dijkstra.findShortestPaths(v);
        }
        return 1;
    }

    public static Integer runFloydWarshall(WeightedGraph<String> graph, String dummyNode) {
        Map<String, double[][]> result = findAllPairsShortestPaths(graph);
        return 1;
    }
}
