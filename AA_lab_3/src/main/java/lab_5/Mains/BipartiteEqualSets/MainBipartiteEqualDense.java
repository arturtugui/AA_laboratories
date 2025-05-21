package lab_5.Mains.BipartiteEqualSets;

import lab_3.Bipartite.BipartiteGraphGenerator;
import lab_3.Graph.Graph;
import lab_4.WeightedGraph.Visualizer;
import lab_4.WeightedGraph.WeightedGraph;
import lab_5.Algorithms.MinimumSpanningTreeGraph;
import lab_5.KruskalVisualization.BipartiteKruskalVisualizer;
import lab_5.Mains.AlgorithmsHelperLab5;
import lab_5.PrimVisualization.BipartitePrimVisualizer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.BiFunction;

import static lab_4.WeightedGraph.GraphToWeightedGraphConverter.convertToWeightedGraph;

public class MainBipartiteEqualDense {
    public static void main(String[] args) {
        String category = "Bipartite undirected dense graphs (Equal sets)";

        List<BiFunction<WeightedGraph<String>, String, Integer>> functions = new ArrayList<>();
        functions.add(AlgorithmsHelperLab5::runPrim);
        functions.add(AlgorithmsHelperLab5::runKruskal);

        List<String> functNames = new ArrayList<>();
        functNames.add("Prim");
        functNames.add("Kruskal");

        int functionNamesSpace = 10;
        int cellsSpace = 10;

        int[] nValues = {5, 10, 30, 75, 150, 300, 400, 500, 600, 800, 1000, 1200, 1600}; //fast
        //int[] nValues = {5, 10, 30, 75, 150, 300, 400, 500, 600, 800, 1000, 1200, 1600, 2000, 2400, 3200}; //slow
        //System.out.println(nValues.length);

        Scanner scanner = new Scanner(System.in);
        int choice = 0;
        int lines = nValues.length;

        WeightedGraph<String>[] graphs = new WeightedGraph[lines];

        for (int i = 0; i < lines; i++) {
            int n = nValues[i];
            float nFloat = (float) n;
            float mFloat = (float) 0.8 * nFloat/2 * (nFloat - (nFloat /2));
            int m = (int) Math.min(Math.max(mFloat, n-1), n*(n-1)/2);

            Graph<String> unweightedGraph = BipartiteGraphGenerator.generateStringLabelBipartiteGraph(n, m, n/2);
            WeightedGraph<String> weightedGraph = convertToWeightedGraph(unweightedGraph);
            graphs[i] = weightedGraph;
        }

        doAlgorithmsComparison(functions, functNames, nValues, category, functionNamesSpace, cellsSpace, graphs);

        do {
            System.out.println("\n\nOptions:");
            System.out.println("\t1. Show a particular graph");
            System.out.println("\t2. Redo algorithms comparison again");
            System.out.println("\t0. Exit");
            System.out.print("Enter your choice: ");

            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    readGraphPosition(scanner, choice, graphs);
                    break;
                case 2:
                    doAlgorithmsComparison(functions, functNames, nValues, category, functionNamesSpace, cellsSpace, graphs);
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

        } while (choice != 0);

        scanner.close();
    }

    public static void readGraphPosition(Scanner scanner, int choice, WeightedGraph<String>[] graphs) {
        int r;

        System.out.print("Enter the graph index (1 to " + graphs.length + "): ");
        r = scanner.nextInt() - 1;

        WeightedGraph<String> graph = graphs[r];

        graphOptions(scanner, choice, graph);
    }

    public static void graphOptions(Scanner scanner, int choice, WeightedGraph<String> graph) {
        do {
            System.out.println("\n\nFor the given graph:");
            System.out.println("\t1. Print adjacency list");
            System.out.println("\t2. Show the graph (Visual)");
            System.out.println("\t3. Print and show the MST of Prim's algorithm (Visual)");
            System.out.println("\t4. Print and show the MST of Kruskal's algorithm (Visual)");
            System.out.println("\t5. Show how Prim's algorithm is performed (Visual)");
            System.out.println("\t6. Show how Kruskal's algorithm is performed (Visual)");
            System.out.println("\t0. Exit graph options");
            System.out.print("Enter your choice: ");

            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("\nGraph adjacency list:");
                    graph.printGraph();
                    break;
                case 2:
                    Visualizer.visualizeBipartite(graph);
                    break;
                case 3:
                    MinimumSpanningTreeGraph<String> mstGraph = new MinimumSpanningTreeGraph<>(graph);
                    String startVertex = mstGraph.getVertices().iterator().next();
                    double primCost = mstGraph.computePrimMST(startVertex);
                    System.out.println("\nPrim algorithm:");
                    mstGraph.printMST();
                    Visualizer.visualizeBipartite(mstGraph.getMSTAsGraph());
                    break;
                case 4:
                    MinimumSpanningTreeGraph<String> mstGraph2 = new MinimumSpanningTreeGraph<>(graph);
                    double kruskalCost = mstGraph2.computeKruskalMST();
                    System.out.println("\nKruskal algorithm:");
                    mstGraph2.printMST();
                    Visualizer.visualizeBipartite(mstGraph2.getMSTAsGraph());
                    break;
                case 5:
                    MinimumSpanningTreeGraph<String> mstGraph3 = new MinimumSpanningTreeGraph<>(graph);
                    String startVertex3 = mstGraph3.getVertices().iterator().next();
                    double primCost3 = mstGraph3.computePrimMST(startVertex3);
                    BipartitePrimVisualizer.visualizeBipartite(graph);
                    break;
                case 6:
                    MinimumSpanningTreeGraph<String> mstGraph4 = new MinimumSpanningTreeGraph<>(graph);
                    double kruskalCost4 = mstGraph4.computeKruskalMST();
                    BipartiteKruskalVisualizer.visualizeBipartite(graph);
                case 0:
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

        } while (choice != 0);
    }

    public static void doAlgorithmsComparison(List<BiFunction<WeightedGraph<String>, String, Integer>> functions,
                                              List<String> funcNames,
                                              int[] nValues,
                                              String category,
                                              int functionNamesSpace,
                                              int cellsSpace,
                                              WeightedGraph<String>[] graphs) {
        double[] executionTimes = new double[nValues.length];

        if (functions.size() != funcNames.size()) {
            System.out.println("Error: Number of functions does not match the number of function names.");
            return;
        }

        if(funcNames.size() == 2){
            System.out.println("\n\n" + funcNames.get(0) + " vs " + funcNames.get(1) + " analysis on " + category);

        }

        System.out.println("Execution time (ms):");
        System.out.printf("%" + functionNamesSpace + "s", "n values:");

        for (int nValue : nValues) {
            System.out.printf("%" + cellsSpace + "s", nValue);
        }
        System.out.println("\n");

        // Prepare series for plotting
        XYSeriesCollection dataset = new XYSeriesCollection();

        for (int j = 0; j < functions.size(); j++) {
            BiFunction<WeightedGraph<String>, String, Integer> func = functions.get(j);
            String funcName = funcNames.get(j);

            XYSeries series = new XYSeries(funcName);

            for (int i = 0; i < nValues.length; i++) {
                WeightedGraph<String> graph = graphs[i];

                long startTime = System.nanoTime();
                func.apply(graph, "U1");
                long endTime = System.nanoTime();

                long elapsedTime = (endTime - startTime) / 1_000_000;
                executionTimes[i] = elapsedTime;

                series.add(nValues[i], elapsedTime);
            }

            dataset.addSeries(series);

            System.out.printf("%" + functionNamesSpace + "s", funcName);
            for (int i = 0; i < executionTimes.length; i++) {
                System.out.printf("%" + cellsSpace + ".2f", executionTimes[i]);
            }
            System.out.println();
        }

        // Plot only execution time
        SwingUtilities.invokeLater(() -> plotExecutionTime(dataset, category));
    }

    public static void plotExecutionTime(XYSeriesCollection dataset, String category) {
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Execution Time Comparison for " + category, // Chart title
                "Graph size (nodes)",        // X-axis label
                "Execution Time (ms)",       // Y-axis label
                dataset,                     // Data
                PlotOrientation.VERTICAL,
                true,  // legend
                true,  // tooltips
                false  // URLs
        );

        JFrame frame = new JFrame("Execution Time Plot for" + category);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(new ChartPanel(chart));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

