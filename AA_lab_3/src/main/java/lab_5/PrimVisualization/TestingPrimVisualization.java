package lab_5.PrimVisualization;

import lab_3.Bipartite.BipartiteGraphGenerator;
import lab_3.DirectedAndUndirected.DirectedUndirectedGraphGenerator;
import lab_3.Graph.Graph;
import lab_3.KRegular.KRegularGraphGenerator;
import lab_4.WeightedGraph.Visualizer;
import lab_4.WeightedGraph.WeightedGraph;
import lab_5.Algorithms.MinimumSpanningTreeGraph;

import javax.swing.*;

import static lab_4.WeightedGraph.GraphToWeightedGraphConverter.convertToWeightedGraph;

public class TestingPrimVisualization {
    public static void main(String[] args) {
        // undirected
        //callUndirected();


        // directed
        ///Prim and Kruskal are not intended for directed graphs

        // undirected bipartite
        callBipartite();


        // undirected k-regular
        //callKGraph();

    }


    public static void callUndirected(){
        Graph<String> graph = DirectedUndirectedGraphGenerator.generateStringLabelGraph(6, 9, false);
        WeightedGraph<String> weightedGraph = convertToWeightedGraph(graph);
        lab_4.WeightedGraph.Visualizer.visualizeDirectedAndUndirected(weightedGraph);
        //Prim
        MinimumSpanningTreeGraph<String> mstGraph = new MinimumSpanningTreeGraph<>(weightedGraph);
        String startVertex = mstGraph.getVertices().iterator().next();
//        //Kruskal
//        MinimumSpanningTreeGraph<String> mstGraph2 = new MinimumSpanningTreeGraph<>(weightedGraph);
        //Prim
        double totalCost = mstGraph.computePrimMST(startVertex);
        System.out.println("\nPrim algorithm:");
        mstGraph.printMST();
        lab_4.WeightedGraph.Visualizer.visualizeDirectedAndUndirected(mstGraph.getMSTAsGraph());
//        //Kruskal
//        double kruskalCost = mstGraph.computeKruskalMST();
//        System.out.println("\nKruskal algorithm:");
//        mstGraph.printMST();
//        lab_4.WeightedGraph.Visualizer.visualizeDirectedAndUndirected(mstGraph.getMSTAsGraph());

        PrimAlgorithmVisualizer.visualizeUndirected(weightedGraph);
    }

    public static void callBipartite() {
        Graph<String> bipartiteGraph = BipartiteGraphGenerator.generateStringLabelBipartiteGraph(7, 9, 3);
        WeightedGraph<String> weightedBipartiteGraph = convertToWeightedGraph(bipartiteGraph);
        Visualizer.visualizeBipartite(weightedBipartiteGraph);
        //Prim
        MinimumSpanningTreeGraph<String> mstGraph = new MinimumSpanningTreeGraph<>(weightedBipartiteGraph);
        String startVertex = mstGraph.getVertices().iterator().next();
//        //Kruskal
//        MinimumSpanningTreeGraph<String> mstGraph2 = new MinimumSpanningTreeGraph<>(weightedBipartiteGraph);
        //Prim
        double totalCost = mstGraph.computePrimMST(startVertex);
        System.out.println("\nPrim algorithm:");
        mstGraph.printMST();
        lab_4.WeightedGraph.Visualizer.visualizeBipartite(mstGraph.getMSTAsGraph());
//        //Kruskal
//        double kruskalCost = mstGraph.computeKruskalMST();
//        System.out.println("\nKruskal algorithm:");
//        mstGraph.printMST();
//        lab_4.WeightedGraph.Visualizer.visualizeBipartite(mstGraph.getMSTAsGraph());

        BipartitePrimVisualizer.visualizeBipartite(weightedBipartiteGraph);
    }

    public static void callKGraph() {
        Graph<String> kgraph = KRegularGraphGenerator.generateStringLabelKRegularGraph(6, 3);
        WeightedGraph<String> weightedGraph = convertToWeightedGraph(kgraph);
        lab_4.WeightedGraph.Visualizer.visualizeDirectedAndUndirected(weightedGraph);
        //Prim
        MinimumSpanningTreeGraph<String> mstGraph = new MinimumSpanningTreeGraph<>(weightedGraph);
        String startVertex = mstGraph.getVertices().iterator().next();
//        //Kruskal
//        MinimumSpanningTreeGraph<String> mstGraph2 = new MinimumSpanningTreeGraph<>(weightedGraph);
        //Prim
        double totalCost = mstGraph.computePrimMST(startVertex);
        System.out.println("\nPrim algorithm:");
        mstGraph.printMST();
        lab_4.WeightedGraph.Visualizer.visualizeDirectedAndUndirected(mstGraph.getMSTAsGraph());
//        //Kruskal
//        double kruskalCost = mstGraph.computeKruskalMST();
//        System.out.println("\nKruskal algorithm:");
//        mstGraph.printMST();
//        lab_4.WeightedGraph.Visualizer.visualizeDirectedAndUndirected(mstGraph.getMSTAsGraph());

        PrimAlgorithmVisualizer.visualizeUndirected(weightedGraph);
    }
}

