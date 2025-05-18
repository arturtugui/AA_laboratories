package lab_4.Mains.MainsDirected;

import lab_3.DirectedAndUndirected.DirectedUndirectedGraphGenerator;
import lab_3.Graph.Graph;
import lab_4.Dijkstra.DijkstraAlgorithm;
import lab_4.DirectedAndUndirectedWeighted.DirectedAndUndirectedWeightedVisualizer;
import lab_4.Mains.AlgorithmsHelper;
import lab_4.WeightedGraph.WeightedGraph;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.BiFunction;

import static lab_4.FloydWarshall.FloydWarshall.findAllPairsShortestPaths;
import static lab_4.FloydWarshall.FloydWarshall.printAllShortestPaths;
import static lab_4.WeightedGraph.GraphToWeightedGraphConverter.convertToWeightedGraph;

public class MainDirectedDense {
    public static void main(String[] args) {
        String category = "Directed dense graphs";

        List<BiFunction<WeightedGraph<String>, String, Integer>> functions = new ArrayList<>();
        functions.add(AlgorithmsHelper::runDijkstra);
        functions.add(AlgorithmsHelper::runDijkstraOnAll);
        functions.add(AlgorithmsHelper::runFloydWarshall);

        List<String> functNames = new ArrayList<>();
        functNames.add("Dijkstra on node A");
        functNames.add("Dijkstra on All nodes");
        functNames.add("Floyd-Warshall");

        int functionNamesSpace = 21;
        int cellsSpace = 12;

        int[] nValues = {5, 10, 20, 40, 80, 150, 200, 300, 400, 500};

        Scanner scanner = new Scanner(System.in);
        int choice = 0;
        int lines = nValues.length;

        WeightedGraph<String>[] graphs = new WeightedGraph[lines];

        for (int i = 0; i < lines; i++) {
            int n = nValues[i];
            float mFloat = (float) ((n * (n-1) * 0.7));
            int m = (int) Math.min(Math.max(mFloat, n-1), n*(n-1));

            Graph<String> unweightedGraph = DirectedUndirectedGraphGenerator.generateStringLabelGraph(n, m, true);
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
            System.out.println("\t1. Show adjacency list");
            System.out.println("\t2. Show the graph (Visual)");
            System.out.println("\t3. Perform Dijkstra from node A");
            System.out.println("\t4. Perform Dijkstra from a node");
            System.out.println("\t5. Perform Floyd-Warshall on the graph");
            System.out.println("\t0. Exit graph options");
            System.out.print("Enter your choice: ");

            choice = scanner.nextInt();

            DijkstraAlgorithm<String> dijkstra1;
            switch (choice) {
                case 1:
                    graph.printGraph();
                    break;
                case 2:
                    SwingUtilities.invokeLater(() -> new DirectedAndUndirectedWeightedVisualizer(graph));
                    break;
                case 3:
                    System.out.println("\nDijkstra performed for node A:");
                    dijkstra1 = new DijkstraAlgorithm<>(graph);
                    dijkstra1.printShortestPaths("A");
                    break;
                case 4:
                    System.out.print("\nEnter the node: ");
                    scanner.nextLine();
                    String node = scanner.nextLine().trim();

                    if (graph.hasVertex(node)) {
                        System.out.println("\nDijkstra performed for node " + node + ":");
                        dijkstra1 = new DijkstraAlgorithm<>(graph);
                        dijkstra1.printShortestPaths(node);
                    } else {
                        System.out.println("\nThe graph does not contain node: " + node);
                    }
                    break;
                case 5:
                    System.out.println("\nShortest path with Floyd-Warshall:");
                    Map<String, double[][]> result = findAllPairsShortestPaths(graph);
                    printAllShortestPaths(graph, result);
                    break;
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

        System.out.println("\n\n" + funcNames.get(0) + " vs " + funcNames.get(1) + " vs " + funcNames.get(2) + " analysis on " + category);


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
                func.apply(graph, "A");
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

