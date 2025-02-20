import java.awt.*;
import java.util.function.Function;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        int[] nValues = {5, 10, 15, 20, 25, 30, 35, 40};
        int[] nValues2 = {500, 750, 1250, 2000, 3000, 5000, 7500, 10000, 12500, 15000};
//
        measureAndPlotExecutionTime(FibonacciGenerator::recursiveFib, nValues, "Recursive method", true, Main::fittingSeries1);
        measureAndPlotExecutionTime(FibonacciGenerator::topDownDPFib, nValues2, "Top-down DP method", false, Main::fittingSeries2);
        measureAndPlotExecutionTime(FibonacciGenerator::bottomUpDPFib, nValues2, "Bottom-up DP method", false, Main::fittingSeries3);
        measureAndPlotExecutionTime(FibonacciGenerator::matrixPowerFib, nValues2, "Matrix Power method", false, Main::fittingSeries4);
        measureAndPlotExecutionTime(FibonacciGenerator::fastDoublingFibHelper, nValues2, "Fast Doubling method", false, Main::fittingSeries5);
        measureAndPlotExecutionTime(FibonacciGenerator::bitwiseDoublingFib, nValues2, "Bitwise Doubling method", false, Main::fittingSeries6);
        measureAndPlotExecutionTime(FibonacciGenerator::binetFormulaFib2, nValues2, "Binet Formula method", true, Main::fittingSeries7);
    }

    public static void measureAndPlotExecutionTime(Function<Integer, Long> func, int[] nValues, String funcName, Boolean divide, Function<XYSeries, XYSeries> fittingSeriesFunc) {
        double[] executionTimes = new double[nValues.length];

        for (int i = 0; i < nValues.length; i++) {
            executionTimes[i] = measureTime(nValues[i], func, funcName, divide);
        }

        System.out.print("n values: ");
        for (int i = 0; i < nValues.length; i++) {
            System.out.print(nValues[i] + " ");
        }
        System.out.println();

        System.out.print(funcName + " execution times: ");
        for (int i = 0; i < executionTimes.length; i++) {
            System.out.print(executionTimes[i] + " ");
        }
        System.out.println();

        plotExecutionTime(nValues, executionTimes, funcName, divide, fittingSeriesFunc);
    }

    public static long measureTime(int n, Function<Integer, Long> func, String funcName, Boolean divide) {
        long startTime = System.nanoTime();
        long result = func.apply(n);
        long endTime = System.nanoTime();
        long elapsedTime = (long) (endTime - startTime);
        if(divide) {
            elapsedTime = (long) (elapsedTime / 1_000_000.0);
        }

        //System.out.println("For " + funcName + ": " + result);
//        System.out.println("Execution time: " + elapsedTime / 1_000_000.0 + " ms\n");

        return elapsedTime;
    }

    public static void plotExecutionTime(int[] nValues, double[] executionTimes, String funcName, Boolean divide, Function<XYSeries, XYSeries> fittingSeriesFunc) {
        XYSeries series = new XYSeries(funcName);

        for (int i = 0; i < nValues.length; i++) {
            series.add(nValues[i], executionTimes[i]);
        }

        JFreeChart chart = createChart(series, funcName, divide, fittingSeriesFunc);
        JFrame frame = new JFrame(funcName + " Execution Time");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new ChartPanel(chart));
        frame.pack();
        frame.setVisible(true);
    }

    public static JFreeChart createChart(XYSeries series, String funcName, Boolean divide, Function<XYSeries, XYSeries> fittingSeriesFunc) {
        XYSeriesCollection dataset = new XYSeriesCollection(series);


        if (fittingSeriesFunc != null) {
            XYSeries fittingSeries = fittingSeriesFunc.apply(series);
            dataset.addSeries(fittingSeries);
        }

        String Yaxis;
        if(divide) {
            Yaxis = "Execution time (ms)";
        }
        else {
            Yaxis ="Execution time (ns)";
        }


        JFreeChart chart = ChartFactory.createXYLineChart(
                funcName + " Execution Time", // Chart title
                "n-th Fibonacci Term", // X-axis label
                Yaxis, // Y-axis label
                dataset, // Data
                PlotOrientation.VERTICAL, // Plot orientation
                true, // Show legend
                true, // Tooltips
                false // URLs
        );

        chart.getXYPlot().getRenderer().setSeriesStroke(0, new BasicStroke(2.0f));
        if (fittingSeriesFunc != null) {
            chart.getXYPlot().getRenderer().setSeriesStroke(1, new BasicStroke(2.0f));
        }

        return chart;
    }

    public static XYSeries fittingSeries1(XYSeries series){
        XYSeries expSeries = new XYSeries("2^(x-30)");
        for (int i = 0; i < series.getItemCount(); i++) {
            double x = series.getX(i).doubleValue();
            expSeries.add(x, Math.pow(2, x-30));
        }

        return expSeries;
    }

    public static XYSeries fittingSeries2(XYSeries series){
        XYSeries expSeries = new XYSeries("150x");
        for (int i = 0; i < series.getItemCount(); i++) {
            double x = series.getX(i).doubleValue();
            expSeries.add(x, 150 * x);
        }

        return expSeries;
    }

    public static XYSeries fittingSeries3(XYSeries series){
        XYSeries expSeries = new XYSeries("30x");
        for (int i = 0; i < series.getItemCount(); i++) {
            double x = series.getX(i).doubleValue();
            expSeries.add(x, 30*x);
        }

        return expSeries;
    }

    public static XYSeries fittingSeries4(XYSeries series){
        XYSeries expSeries = new XYSeries("950 * log(x)");
        for (int i = 0; i < series.getItemCount(); i++) {
            double x = series.getX(i).doubleValue();
            expSeries.add(x, 950*Math.log(x));
        }

        return expSeries;
    }

    public static XYSeries fittingSeries5(XYSeries series){
        XYSeries expSeries = new XYSeries("1500 * log(x)");
        for (int i = 0; i < series.getItemCount(); i++) {
            double x = series.getX(i).doubleValue();
            expSeries.add(x, 1500 * Math.log(x));
        }

        return expSeries;
    }

    public static XYSeries fittingSeries6(XYSeries series){
        XYSeries expSeries = new XYSeries("300 * log(x)");
        for (int i = 0; i < series.getItemCount(); i++) {
            double x = series.getX(i).doubleValue();
            expSeries.add(x, 300 * Math.log(x));
        }

        return expSeries;
    }

    public static XYSeries fittingSeries7(XYSeries series){
        XYSeries expSeries = new XYSeries("5 ms");
        for (int i = 0; i < series.getItemCount(); i++) {
            double x = series.getX(i).doubleValue();
            expSeries.add(x, 5);
        }

        return expSeries;
    }
}
