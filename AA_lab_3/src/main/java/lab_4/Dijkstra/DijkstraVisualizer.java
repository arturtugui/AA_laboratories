package lab_4.Dijkstra;

import lab_3.DirectedAndUndirected.DirectedUndirectedGraphGenerator;
import lab_3.Graph.Graph;
import lab_4.WeightedGraph.WeightedEdge;
import lab_4.WeightedGraph.WeightedGraph;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

import static lab_4.WeightedGraph.GraphToWeightedGraphConverter.convertToWeightedGraph;

/**
 * A visualizer for Dijkstra's algorithm that shows step-by-step execution
 * of finding shortest paths from a source vertex to all other vertices.
 */
public class DijkstraVisualizer extends JFrame { //unused
    private final WeightedGraph<String> graph;
    private final String source;
    private final Map<String, Point> vertexPositions;
    private final Map<String, Double> distances;
    private final Map<String, String> previousVertices;
    private final Set<String> settled;
    private final PriorityQueue<String> queue;
    private String currentVertex;
    private final JPanel graphPanel;
    private final JTextArea infoArea;
    private final JButton nextStepButton;
    private final JButton autoRunButton;
    private Timer timer;
    private boolean algorithmCompleted;
    private int stepCount;

    // Colors for visualization
    private final Color SOURCE_COLOR = Color.GREEN;
    private final Color CURRENT_COLOR = Color.RED;
    private final Color SETTLED_COLOR = Color.ORANGE;
    private final Color QUEUED_COLOR = Color.YELLOW;
    private final Color DEFAULT_COLOR = Color.LIGHT_GRAY;
    private final Color EDGE_COLOR = Color.GRAY;
    private final Color PATH_EDGE_COLOR = Color.BLUE;

    /**
     * Constructor for the DijkstraVisualizer.
     *
     * @param graph The weighted graph to run the algorithm on
     * @param source The source vertex
     */
    public DijkstraVisualizer(WeightedGraph<String> graph, String source) {
        super("Dijkstra Algorithm Visualizer");
        this.graph = graph;
        this.source = source;
        this.vertexPositions = new HashMap<>();
        this.distances = new HashMap<>();
        this.previousVertices = new HashMap<>();
        this.settled = new HashSet<>();
        this.queue = new PriorityQueue<>(
                Comparator.comparingDouble(String -> distances.getOrDefault(String, Double.POSITIVE_INFINITY))
        );
        this.algorithmCompleted = false;
        this.stepCount = 0;

        // Initialize distances
        for (String vertex : graph.getVertices()) {
            distances.put(vertex, vertex.equals(source) ? 0.0 : Double.POSITIVE_INFINITY);
        }

        // Add source to the priority queue
        queue.add(source);

        // Calculate positions for vertices in a circle
        calculateVertexPositions();

        // Set up the UI
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Graph visualization panel
        graphPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGraph(g);
            }
        };
        graphPanel.setPreferredSize(new Dimension(600, 400));

        // Information panel
        infoArea = new JTextArea();
        infoArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(infoArea);
        scrollPane.setPreferredSize(new Dimension(200, 400));

        // Control panel
        JPanel controlPanel = new JPanel();
        nextStepButton = new JButton("Next Step");
        nextStepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nextStep();
            }
        });

        autoRunButton = new JButton("Auto Run");
        autoRunButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (timer == null || !timer.isRunning()) {
                    startAutoRun();
                } else {
                    stopAutoRun();
                }
            }
        });

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetAlgorithm();
            }
        });

        controlPanel.add(nextStepButton);
        controlPanel.add(autoRunButton);
        controlPanel.add(resetButton);

        // Add components to the frame
        add(graphPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.EAST);
        add(controlPanel, BorderLayout.SOUTH);

        // Initialize the information area
        updateInfoArea();

        // Create a timer for auto-run
        timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nextStep();
                if (algorithmCompleted) {
                    stopAutoRun();
                }
            }
        });

        // Show the frame
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Calculate positions for vertices in a circle for visualization.
     */
    private void calculateVertexPositions() {
        List<String> vertices = new ArrayList<>(graph.getVertices());
        int centerX = 300;
        int centerY = 200;
        int radius = 150;

        for (int i = 0; i < vertices.size(); i++) {
            double angle = 2 * Math.PI * i / vertices.size();
            int x = (int) (centerX + radius * Math.cos(angle));
            int y = (int) (centerY + radius * Math.sin(angle));
            vertexPositions.put(vertices.get(i), new Point(x, y));
        }
    }

    /**
     * Draw the graph with current algorithm state.
     */
    private void drawGraph(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw edges
        for (String v : graph.getVertices()) {
            Point p1 = vertexPositions.get(v);
            for (WeightedEdge<String> edge : graph.getNeighbors(v)) {
                Point p2 = vertexPositions.get(edge.target);

                // Determine if this edge is part of a shortest path
                boolean isShortestPathEdge = previousVertices.containsKey(edge.target) &&
                        previousVertices.get(edge.target).equals(v);

                g2d.setColor(isShortestPathEdge ? PATH_EDGE_COLOR : EDGE_COLOR);
                g2d.setStroke(new BasicStroke(isShortestPathEdge ? 3f : 1f));

                g2d.drawLine(p1.x, p1.y, p2.x, p2.y);

                // Draw edge weight
                int midX = (p1.x + p2.x) / 2;
                int midY = (p1.y + p2.y) / 2;
                g2d.setColor(Color.BLACK);
                g2d.drawString(String.valueOf(edge.weight), midX, midY);

                // Draw arrow for directed graph
                if (graph.isDirected()) {
                    drawArrow(g2d, p1.x, p1.y, p2.x, p2.y);
                }
            }
        }

        // Draw vertices
        for (String v : graph.getVertices()) {
            Point p = vertexPositions.get(v);

            // Choose color based on vertex state
            if (v.equals(source)) {
                g2d.setColor(SOURCE_COLOR);
            } else if (v.equals(currentVertex)) {
                g2d.setColor(CURRENT_COLOR);
            } else if (settled.contains(v)) {
                g2d.setColor(SETTLED_COLOR);
            } else if (queue.contains(v)) {
                g2d.setColor(QUEUED_COLOR);
            } else {
                g2d.setColor(DEFAULT_COLOR);
            }

            // Draw vertex circle
            g2d.fillOval(p.x - 15, p.y - 15, 30, 30);
            g2d.setColor(Color.BLACK);
            g2d.drawOval(p.x - 15, p.y - 15, 30, 30);

            // Draw vertex label
            g2d.drawString(v.toString(), p.x - 5, p.y + 5);

            // Draw distance from source
            String distance = distances.get(v).equals(Double.POSITIVE_INFINITY) ?
                    "∞" : String.format("%.1f", distances.get(v));
            g2d.drawString(distance, p.x - 10, p.y - 20);
        }
    }

    /**
     * Draw an arrow head for directed edges.
     */
    private void drawArrow(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double angle = Math.atan2(dy, dx);
        int len = (int) Math.sqrt(dx * dx + dy * dy);

        // Calculate the point on the line that is 15 pixels from the target vertex
        int arrowX = x2 - (int)(15 * Math.cos(angle));
        int arrowY = y2 - (int)(15 * Math.sin(angle));

        // Create the arrow head
        int[] xPoints = new int[3];
        int[] yPoints = new int[3];
        xPoints[0] = arrowX;
        yPoints[0] = arrowY;
        xPoints[1] = arrowX - 10 - (int)(10 * Math.cos(angle - Math.PI/6));
        yPoints[1] = arrowY - (int)(10 * Math.sin(angle - Math.PI/6));
        xPoints[2] = arrowX - 10 - (int)(10 * Math.cos(angle + Math.PI/6));
        yPoints[2] = arrowY - (int)(10 * Math.sin(angle + Math.PI/6));

        g2d.fillPolygon(xPoints, yPoints, 3);
    }

    /**
     * Update the information area with current algorithm state.
     */
    private void updateInfoArea() {
        StringBuilder sb = new StringBuilder();
        sb.append("Step: ").append(stepCount).append("\n\n");
        sb.append("Source: ").append(source).append("\n\n");
        sb.append("Distance from source:\n");

        for (String v : graph.getVertices()) {
            String distance = distances.get(v).equals(Double.POSITIVE_INFINITY) ?
                    "∞" : String.format("%.1f", distances.get(v));
            sb.append(v).append(": ").append(distance).append("\n");
        }

        sb.append("\nSettled vertices: ").append(settled).append("\n");
        sb.append("\nCurrent queue: ").append(new ArrayList<>(queue)).append("\n");

        if (currentVertex != null) {
            sb.append("\nProcessing vertex: ").append(currentVertex).append("\n");
        }

        if (algorithmCompleted) {
            sb.append("\nAlgorithm completed!\n");
        }

        infoArea.setText(sb.toString());
    }

    /**
     * Execute one step of Dijkstra's algorithm.
     */
    private void nextStep() {
        if (algorithmCompleted) {
            return;
        }

        stepCount++;

        if (queue.isEmpty()) {
            algorithmCompleted = true;
            updateInfoArea();
            graphPanel.repaint();
            return;
        }

        // Get vertex with minimum distance
        currentVertex = queue.poll();

        // If vertex is already processed, skip
        if (settled.contains(currentVertex)) {
            updateInfoArea();
            graphPanel.repaint();
            return;
        }

        // Mark current vertex as processed
        settled.add(currentVertex);

        // Process all adjacent vertices
        for (WeightedEdge<String> edge : graph.getNeighbors(currentVertex)) {
            String neighbor = edge.target;

            // Skip if neighbor is already processed
            if (settled.contains(neighbor)) {
                continue;
            }

            // Calculate new distance
            double newDistance = distances.get(currentVertex) + edge.weight;

            // If new distance is smaller, update the distance
            if (newDistance < distances.get(neighbor)) {
                distances.put(neighbor, newDistance);
                previousVertices.put(neighbor, currentVertex);

                // Add neighbor to queue for processing
                if (!queue.contains(neighbor)) {
                    queue.add(neighbor);
                } else {
                    // Need to reorder the queue because priority has changed
                    queue.remove(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        updateInfoArea();
        graphPanel.repaint();
    }

    /**
     * Start auto-running the algorithm.
     */
    private void startAutoRun() {
        timer.start();
        autoRunButton.setText("Stop Auto Run");
        nextStepButton.setEnabled(false);
    }

    /**
     * Stop auto-running the algorithm.
     */
    private void stopAutoRun() {
        if (timer != null) {
            timer.stop();
        }
        autoRunButton.setText("Auto Run");
        nextStepButton.setEnabled(true);
    }

    /**
     * Reset the algorithm to its initial state.
     */
    private void resetAlgorithm() {
        stopAutoRun();

        // Clear data structures
        distances.clear();
        previousVertices.clear();
        settled.clear();
        queue.clear();

        // Reinitialize distances
        for (String vertex : graph.getVertices()) {
            distances.put(vertex, vertex.equals(source) ? 0.0 : Double.POSITIVE_INFINITY);
        }

        // Add source to the priority queue
        queue.add(source);

        // Reset state variables
        currentVertex = null;
        algorithmCompleted = false;
        stepCount = 0;

        // Update UI
        updateInfoArea();
        graphPanel.repaint();
    }

    /**
     * Main method with a usage example.
     */
    public static void main(String[] args) {
        // Set up the look and feel to match the system
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

//        // Create a sample weighted graph
//        WeightedGraph<String> graph = new WeightedGraph<>(true); // Directed graph
//
//        // Add vertices
//        graph.addVertex("A");
//        graph.addVertex("B");
//        graph.addVertex("C");
//        graph.addVertex("D");
//        graph.addVertex("E");
//
//        // Add edges with weights
//        graph.addEdge("A", "B", 4);
//        graph.addEdge("A", "C", 2);
//        graph.addEdge("B", "C", 3);
//        graph.addEdge("B", "E", 3);
//        graph.addEdge("B", "D", 2);
//        graph.addEdge("C", "B", 1);
//        graph.addEdge("C", "D", 4);
//        graph.addEdge("C", "E", 5);
//        graph.addEdge("E", "D", 1);

        Graph<String> graph = DirectedUndirectedGraphGenerator.generateStringLabelGraph(6, 16, true);
        WeightedGraph<String> weightedGraph = convertToWeightedGraph(graph);

        // Create and show visualizer
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new DijkstraVisualizer(weightedGraph, "A");
            }
        });
    }
}
