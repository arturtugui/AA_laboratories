import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Main {
    public static void main(String[] args) {
        ///Change Array here

        //// for sorted (QuickSort leads to stackoverflow for greater)
        //int[] nValues = {10, 100, 1000, 1500, 3000, 5000, 7500, 10000, 15000, 20000};

        //// for reverse sorted (QuickSort leads to stack overflow for greater, Insertion is also seen)
        //int[] nValues = {10, 100, 1000, 1500, 3000, 5000, 7500, 10000, 13000, 16000};

        //// for average case: with insertion
        //int[] nValues = {10, 100, 1000, 5000, 10000, 20000, 35000, 50000, 65000, 80000, 100000};
        //// for average case: without insertion,, and sortead and reverse without QuickSort and Insetuon
        int[] nValues = {100, 100000, 500000, 1000000, 1500000, 2500000, 3500000, 5000000, 6500000, 8000000, 10000000};


        List<Consumer<int[]>> sorters = new ArrayList<>();
        sorters.add(QuickSorter::quickSortHelper);
        sorters.add(QuickSorter::quickSortMotHelper);
        sorters.add(MergeSorter::mergeSortHelper);
        sorters.add(MergeSorter::mergeSortIterative);
        sorters.add(HeapSorter::heapSortHelper);
        sorters.add(HeapSorter::ternaryHeapSort);
        //sorters.add(InsertionSorter::insertionSortHelper);
        sorters.add(InsertionSorter::shellSortHelper);

        List<String> functNames = new ArrayList<>();

        functNames.add("Quick Sort");
        functNames.add("Quick Sort Mot");
        functNames.add("Merge Sort");
        functNames.add("Merge Sort Iterative");
        functNames.add("Heap Sort");
        functNames.add("Ternary Heap Sort");
        //functNames.add("Insertion Sort");
        functNames.add("Shell Sort");

        measureAndPlotExecutionTime(sorters, functNames, nValues);
    }

    public static int[] randomArray(int n) {
        Random random = new Random();
        int[] randomNumbers = new int[n];

        for (int i = 0; i < n; i++) {
            int randomNumber = random.nextInt();
            randomNumbers[i] = randomNumber;
        }

        return randomNumbers;
    }

    public static int[] sortedArray(int n) {
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = i + 1;
        }
        return arr;
    }

    public static int[] reverseSortedArray(int n) {
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = n - i;
        }
        return arr;
    }


    public static long measureTime(int[] arr, Consumer<int[]> func, String funcName) {
        long startTime = System.nanoTime();
        func.accept(arr);
        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        elapsedTime = elapsedTime / 1_000_000;

        //System.out.println("For " + funcName);
        //System.out.println("Execution time: " + elapsedTime + " ms");

        return elapsedTime;
    }

    public static void measureAndPlotExecutionTime(List<Consumer<int[]>> functions, List<String> funcNames, int[] nValues) {
        double[] executionTimes = new double[nValues.length];

        if (functions.size() != funcNames.size()) {
            System.out.println("Error: Number of functions does not match the number of function names.");
            return;
        }

        XYSeries[] seriesArray = new XYSeries[functions.size()];

        for (int i = 0; i < functions.size(); i++) {
            seriesArray[i] = new XYSeries(funcNames.get(i));
        }

        System.out.printf("Execution time (ms):\n");
        System.out.printf("%21s", "n values:");
        for (int h = 0; h < nValues.length; h++) {
            int exponent = (int) Math.floor(Math.log10(nValues[h]));
            double mantissa = nValues[h]/ Math.pow(10, exponent);

            System.out.printf("%12s", String.format("%.1f*10^%d", mantissa, exponent));
        }
        System.out.println("\n");

        for (int j = 0; j < functions.size(); j++) {
            Consumer<int[]> func = functions.get(j);
            String funcName = funcNames.get(j);

            for (int i = 0; i < nValues.length; i++) {
                ///Chage function here
                int[] arr = randomArray(nValues[i]);

                double executionTime = measureTime(arr, func, funcName);
                executionTimes[i] = executionTime;

                seriesArray[j].add(nValues[i], executionTime);
            }

            System.out.printf("%21s", funcName);
            for (int i = 0; i < executionTimes.length; i++) {
                System.out.printf("%12.2f", executionTimes[i]);
            }
            System.out.println();
        }

        plotExecutionTime(seriesArray);
    }

    public static void plotExecutionTime(XYSeries[] seriesArray) {
        XYSeriesCollection dataset = new XYSeriesCollection();

        for (XYSeries series : seriesArray) {
            dataset.addSeries(series);
        }

        JFreeChart chart = createChart(dataset);
        JFrame frame = new JFrame("Execution Time Comparison");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new ChartPanel(chart));
        frame.pack();
        frame.setVisible(true);
    }

    public static JFreeChart createChart(XYSeriesCollection dataset) {

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Execution Time Comparison", // Chart title
                "Size of array", // X-axis label
                "Execution Time (ms)", // Y-axis label
                dataset, // Data
                PlotOrientation.VERTICAL, // Plot orientation
                true, // Show legend
                true, // Tooltips
                false // URLs
        );


        chart.getXYPlot().getRenderer().setSeriesStroke(0, new BasicStroke(1.0f));

        return chart;
    }
}
