package lab_3.Mains.MainBipartiteFixedNodeCount;

import lab_3.BFS.BreadthFirstSearch;
import lab_3.Bipartite.BipartiteGraphVisualizer;
import lab_3.DFS.DepthFirstSearch;
import lab_3.Graph.Graph;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.util.*;
import java.util.function.BiFunction;

import static lab_3.BFS.BipartiteBFSVisualizer.visualizeBipartiteBFS;
import static lab_3.BFS.BreadthFirstSearch.bfsWithOutput;
import static lab_3.Bipartite.BipartiteGraphGenerator.generateStringLabelBipartiteGraph;
import static lab_3.Bipartite.BipartiteGraphGenerator.getBipartitePartitions;
import static lab_3.DFS.BipartiteDFSVisualizer.visualizeBipartiteDFS;
import static lab_3.DFS.DepthFirstSearch.dfsWithOutput;


public class MainBipartiteFixedNodeCount {
    public static void main(String[] args) {
        String category = "Bipartite undirected graphs with 1000 nodes (Fixed node count, varying partition sizes)";

        List<BiFunction<Graph<String>, String, Integer>> functions = new ArrayList<>();
        functions.add(DepthFirstSearch::dfs);
        functions.add(BreadthFirstSearch::bfs);

        List<String> functNames = new ArrayList<>();
        functNames.add("lab_3/DFS");
        functNames.add("lab_3/BFS");

        int functionNamesSpace = 16;
        int cellsSpace = 12;

        // Fixed node count but varying partition sizes
        int totalNodes = 1000;
        int[] uSizes = {1, 2, 5, 10, 20, 40, 70, 100, 175, 250, 350, 500};
        float density = 0.7f; // in report was used with 0.1, 0.4 and 0.7

        Scanner scanner = new Scanner(System.in);
        int choice = 0;
        int lines = uSizes.length;

        Graph<String>[] graphs = new Graph[lines];

        for (int i = 0; i < lines; i++) {
            int u = uSizes[i];
            int v = totalNodes - u;

            // Calculating edges: use a fixed proportion of the maximum possible edges
            // Maximum possible edges in a bipartite graph = u * v
            float maxEdges = (float) u * (float) v;
            // Use ~10% of maximum edges, but ensure at least n-1 edges for connectivity
            int m = Math.max((int) (density * maxEdges), totalNodes - 1);

            graphs[i] = generateStringLabelBipartiteGraph(totalNodes, m, u);
        }

        doAlgorithmsComparison(functions, functNames, uSizes, category, functionNamesSpace, cellsSpace, graphs, totalNodes);

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
                    doAlgorithmsComparison(functions, functNames, uSizes, category, functionNamesSpace, cellsSpace, graphs, totalNodes);
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
                    SwingUtilities.invokeLater(() ->
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
                                              int[] uSizes,
                                              String category,
                                              int functionNamesSpace,
                                              int cellsSpace,
                                              Graph<String>[] graphs,
                                              int totalNodes) {
        double[] executionTimes = new double[uSizes.length];
        int[][] maxSizes = new int[functions.size()][uSizes.length];

        if (functions.size() != funcNames.size()) {
            System.out.println("Error: Number of functions does not match the number of function names.");
            return;
        }

        System.out.println("\n\n" + funcNames.get(0) + " vs " + funcNames.get(1) + " analysis on " + category);

        System.out.println("Execution time (ms):");
        System.out.printf("%" + functionNamesSpace + "s", "U-partition size");

        for (int i = 0; i < uSizes.length; i++) {
            System.out.printf("%" + cellsSpace + "d", uSizes[i]);
        }
        System.out.println("\n");

        // Prepare series for plotting
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeriesCollection maxSizeDataset = new XYSeriesCollection();

        for (int j = 0; j < functions.size(); j++) {
            BiFunction<Graph<String>, String, Integer> func = functions.get(j);
            String funcName = funcNames.get(j);

            XYSeries timeSeries = new XYSeries(funcName + " Time");
            XYSeries sizeSeries = new XYSeries(funcName + " Size");

            for (int i = 0; i < uSizes.length; i++) {
                Graph<String> graph = graphs[i];

                long startTime = System.nanoTime();
                int maxSize = func.apply(graph, "U1");
                long endTime = System.nanoTime();

                long elapsedTime = (endTime - startTime) / 1_000_000;
                executionTimes[i] = elapsedTime;
                maxSizes[j][i] = maxSize;

                timeSeries.add(uSizes[i], elapsedTime);
                sizeSeries.add(uSizes[i], maxSize);
            }

            dataset.addSeries(timeSeries);
            maxSizeDataset.addSeries(sizeSeries);

            System.out.printf("%" + functionNamesSpace + "s", funcName);
            for (int i = 0; i < executionTimes.length; i++) {
                System.out.printf("%" + cellsSpace + ".2f", executionTimes[i]);
            }
            System.out.println();
        }

        System.out.println("\nMaximum stack/queue size:");
        System.out.printf("%" + functionNamesSpace + "s", "U-partition size");

        for (int i = 0; i < uSizes.length; i++) {
            System.out.printf("%" + cellsSpace + "d", uSizes[i]);
        }
        System.out.println("\n");

        for (int j = 0; j < functions.size(); j++) {
            String funcName = funcNames.get(j);
            System.out.printf("%" + functionNamesSpace + "s", funcName);

            for (int i = 0; i < uSizes.length; i++) {
                System.out.printf("%" + cellsSpace + "d", maxSizes[j][i]);
            }
            System.out.println();
        }

        // Plot execution time
        SwingUtilities.invokeLater(() -> {
            plotExecutionTime(dataset);
            plotMaxSize(maxSizeDataset);
        });
    }

    public static void plotExecutionTime(XYSeriesCollection dataset) {
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Execution Time Comparison", // Chart title
                "U-partition size",          // X-axis label
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

    public static void plotMaxSize(XYSeriesCollection dataset) {
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Maximum Stack/Queue Size Comparison", // Chart title
                "U-partition size",                   // X-axis label
                "Maximum Size",                       // Y-axis label
                dataset,                              // Data
                PlotOrientation.VERTICAL,
                true,  // legend
                true,  // tooltips
                false  // URLs
        );

        JFrame frame = new JFrame("Maximum Stack/Queue Size Plot");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(new ChartPanel(chart));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
