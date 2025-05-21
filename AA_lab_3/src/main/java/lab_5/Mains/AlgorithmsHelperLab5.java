package lab_5.Mains;

import lab_4.WeightedGraph.WeightedGraph;
import lab_5.Algorithms.MinimumSpanningTreeGraph;

public class AlgorithmsHelperLab5 {
    public static Integer runPrim(WeightedGraph<String> weightedGraph, String dummyNode) {
        MinimumSpanningTreeGraph<String> mstGraph = new MinimumSpanningTreeGraph<>(weightedGraph);
        String startVertex = mstGraph.getVertices().iterator().next();

        return (int) mstGraph.computePrimMST(startVertex);
    }

    public static Integer runKruskal(WeightedGraph<String> weightedGraph, String dummyNode) {
        MinimumSpanningTreeGraph<String> mstGraph = new MinimumSpanningTreeGraph<>(weightedGraph);
        String startVertex = mstGraph.getVertices().iterator().next();

        return (int) mstGraph.computeKruskalMST();
    }
}
