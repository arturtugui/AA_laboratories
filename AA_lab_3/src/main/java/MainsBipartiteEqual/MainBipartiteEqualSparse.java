package MainsBipartiteEqual;

import BFS.BreadthFirstSearch;
import Bipartite.BipartiteGraphVisualizer;
import DFS.DepthFirstSearch;
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
import java.util.Set;
import java.util.function.BiFunction;

import static BFS.BFSVisualizer.visualizeBFS;
import static BFS.BipartiteBFSVisualizer.visualizeBipartiteBFS;
import static BFS.BreadthFirstSearch.bfsWithOutput;
import static Bipartite.BipartiteGraphGenerator.generateStringLabelBipartiteGraph;
import static Bipartite.BipartiteGraphGenerator.getBipartitePartitions;
import static DFS.BipartiteDFSVisualizer.visualizeBipartiteDFS;
import static DFS.DFSVisualizationRunner.visualizeDFS;
import static DFS.DepthFirstSearch.dfsWithOutput;

public class MainBipartiteEqualSparse {
    public static void main(String[] args) {
        String category = "Bipartite undirected sparse graphs (Equal sets)";

        List<BiFunction<Graph<String>, String, Integer>> functions = new ArrayList<>();
        functions.add(DepthFirstSearch::dfs);
        functions.add(BreadthFirstSearch::bfs);

        List<String> functNames = new ArrayList<>();
        functNames.add("DFS");
        functNames.add("BFS");

        int functionNamesSpace = 10;
        int cellsSpace = 12;

        //1st one has 12 values, these have 9
        int[] nValues = {10, 50, 100, 250, 500, 750, 1000, 1250, 1500};


        Scanner scanner = new Scanner(System.in);
        int choice = 0;
        int lines = nValues.length;

        Graph<String>[] graphs = new Graph[lines];

        for (int i = 0; i < lines; i++) {
            int n = nValues[i];
            float nFloat = (float) n;
            float mFloat = (float) 0.5 * nFloat/2 * (nFloat - (nFloat /2));
            int m = (int) mFloat;

            graphs[i] = generateStringLabelBipartiteGraph(n, m, n/2);
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
                    Set<String>[] partitions = getBipartitePartitions(graph);
                    javax.swing.SwingUtilities.invokeLater(() ->
                            BipartiteGraphVisualizer.visualizeBipartiteGraph(graph, partitions[0], partitions[1]));
                    break;
                case 3:
                    System.out.println("\nDFS traversal starting from node U1:");
                    int maxStackSize = dfsWithOutput(graph, "U1");
                    System.out.println("Maximum stack size during DFS: " + maxStackSize);
                    break;
                case 4:
                    System.out.println("\nBFS traversal starting from node U1:");
                    int maxQueueSize = bfsWithOutput(graph, "U1");
                    System.out.println("Maximum queue size during BFS: " + maxQueueSize);
                    break;
                case 5:
                    visualizeBipartiteDFS(graph, "U1");
                    break;
                case 6:
                    visualizeBipartiteBFS(graph, "U1");
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
                int maxSize = func.apply(graph, "U1");
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
