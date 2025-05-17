package lab_4.Mains;

import lab_4.Dijkstra.DijkstraAlgorithm;
import lab_4.WeightedGraph.WeightedGraph;

import java.util.Map;

import static lab_4.FloydWarshall.FloydWarshall.findAllPairsShortestPaths;

public class AlgorithmsHelper {
    private static Integer runDijkstra(WeightedGraph<String> graph, String startNode) {
        // Create new instance each time
        DijkstraAlgorithm<String> dijkstra = new DijkstraAlgorithm<>(graph);
        dijkstra.printShortestPaths(startNode);
        return 1;
    }

    public static Integer runFloydWarshall(WeightedGraph<String> graph, String dummyNode) {
        Map<String, double[][]> result = findAllPairsShortestPaths(graph);
        return 1;
    }
}
