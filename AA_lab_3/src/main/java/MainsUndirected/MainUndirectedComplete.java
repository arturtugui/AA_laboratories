package MainsUndirected;

import BFS.BreadthFirstSearch;
import DFS.DepthFirstSearch;
import DirectedAndUndirected.DirectedUndirectedGraphGenerator;
import DirectedAndUndirected.DirectedUndirectedGraphVisualizer;
import Graph.Graph;
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

import static BFS.BFSVisualizer.visualizeBFS;
import static BFS.BreadthFirstSearch.bfsWithOutput;
import static DFS.DFSVisualizationRunner.visualizeDFS;
import static DFS.DepthFirstSearch.dfsWithOutput;

public class MainUndirectedComplete {
    public static void main(String[] args) {
        String category = "Undirected complete graphs";

        List<BiFunction<Graph<String>, String, Integer>> functions = new ArrayList<>();
        functions.add(DepthFirstSearch::dfs);
        functions.add(BreadthFirstSearch::bfs);

        List<String> functNames = new ArrayList<>();
        functNames.add("DFS");
        functNames.add("BFS");

        int functionNamesSpace = 10;
        int cellsSpace = 12;

        int[] nValues = {10, 50, 100, 250, 500, 750, 1000, 1250, 1500};
        //int[] nValues = {10, 50, 100, 250, 500, 750, 1000, 1250, 1750, 2500, 3500, 5000};


        Scanner scanner = new Scanner(System.in);
        int choice = 0;
        int lines = nValues.length;

        Graph<String>[] graphs = new Graph[lines];

        for (int i = 0; i < lines; i++) {
            int n = nValues[i];
            float mFloat = (float) (n * (n-1) / 2);
            int m = (int) mFloat;

            graphs[i] = DirectedUndirectedGraphGenerator.generateStringLabelGraph(n, m, false);
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

    public static void readGraphPosition(Scanner scanner, int choice, Graph<String>[] graphs) {
        int r;

        System.out.print("Enter the graph index (1 to " + graphs.length + "): ");
        r = scanner.nextInt() - 1;

        Graph<String> graph = graphs[r];

        graphOptions(scanner, choice, graph);
    }

    public static void graphOptions(Scanner scanner, int choice, Graph<String> graph) {
        do {
            System.out.println("\n\nFor the given graph:");
            System.out.println("\t1. Show adjacency list");
            System.out.println("\t2. Show the graph (Visual)");
            System.out.println("\t3. Output how DFS traverses it");
            System.out.println("\t4. Output how BFS traverses it");
            System.out.println("\t5. Show how DFS traverses it (Visual)");
            System.out.println("\t6. Show how BFS traverses it (Visual)");
            System.out.println("\t0. Exit graph options");
            System.out.print("Enter your choice: ");

            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    graph.printGraph();
                    break;
                case 2:
                    SwingUtilities.invokeLater(() -> new DirectedUndirectedGraphVisualizer(graph));
                    break;
                case 3:
                    System.out.println("\nDFS traversal starting from node A:");
                    int maxStackSize = dfsWithOutput(graph, "A");
                    System.out.println("Maximum stack size during DFS: " + maxStackSize);
                    break;
                case 4:
                    System.out.println("\nBFS traversal starting from node A:");
                    int maxQueueSize = bfsWithOutput(graph, "A");
                    System.out.println("Maximum queue size during BFS: " + maxQueueSize);
                    break;
                case 5:
                    visualizeDFS(graph, "A");
                    break;
                case 6:
                    visualizeBFS(graph, "A");
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

        } while (choice != 0);
    }

    public static void doAlgorithmsComparison(List<BiFunction<Graph<String>, String, Integer>> functions,
                                              List<String> funcNames,
                                              int[] nValues,
                                              String category,
                                              int functionNamesSpace,
                                              int cellsSpace,
                                              Graph<String>[] graphs) {
        double[] executionTimes = new double[nValues.length];
        int[][] maxSizes = new int[functions.size()][nValues.length];

        if (functions.size() != funcNames.size()) {
            System.out.println("Error: Number of functions does not match the number of function names.");
            return;
        }

        System.out.println("\n\n" + funcNames.get(0) + " vs " + funcNames.get(1) + " analysis on " + category);

        System.out.println("Execution time (ms):");
        System.out.printf("%" + functionNamesSpace + "s", "n values:");

        for (int nValue : nValues) {
            System.out.printf("%" + cellsSpace + "s", nValue);
        }
        System.out.println("\n");

        // Prepare series for plotting
        XYSeriesCollection dataset = new XYSeriesCollection();

        for (int j = 0; j < functions.size(); j++) {
            BiFunction<Graph<String>, String, Integer> func = functions.get(j);
            String funcName = funcNames.get(j);

            XYSeries series = new XYSeries(funcName);

            for (int i = 0; i < nValues.length; i++) {
                Graph<String> graph = graphs[i];

                long startTime = System.nanoTime();
                int maxSize = func.apply(graph, "A");
                long endTime = System.nanoTime();

                long elapsedTime = (endTime - startTime) / 1_000_000;
                executionTimes[i] = elapsedTime;
                maxSizes[j][i] = maxSize;

                series.add(nValues[i], elapsedTime);
            }

            dataset.addSeries(series);

            System.out.printf("%" + functionNamesSpace + "s", funcName);
            for (int i = 0; i < executionTimes.length; i++) {
                System.out.printf("%" + cellsSpace + ".2f", executionTimes[i]);
            }
            System.out.println();
        }

        System.out.println("\nMaximum stack/queue size:");
        System.out.printf("%" + functionNamesSpace + "s", "n values:");

        for (int nValue : nValues) {
            System.out.printf("%" + cellsSpace + "s", nValue);
        }
        System.out.println();

        for (int j = 0; j < functions.size(); j++) {
            String funcName = funcNames.get(j);
            System.out.printf("%" + functionNamesSpace + "s", funcName);

            for (int i = 0; i < nValues.length; i++) {
                System.out.printf("%" + cellsSpace + "d", maxSizes[j][i]);
            }
            System.out.println();
        }

        // Plot only execution time
        SwingUtilities.invokeLater(() -> plotExecutionTime(dataset));
    }

    public static void plotExecutionTime(XYSeriesCollection dataset) {
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Execution Time Comparison", // Chart title
                "Graph size (nodes)",        // X-axis label
                "Execution Time (ms)",       // Y-axis label
                dataset,                     // Data
                PlotOrientation.VERTICAL,
                true,  // legend
                true,  // tooltips
                false  // URLs
        );

        JFrame frame = new JFrame("Execution Time Plot");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(new ChartPanel(chart));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
