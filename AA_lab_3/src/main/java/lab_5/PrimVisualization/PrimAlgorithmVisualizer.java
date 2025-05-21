package lab_5.PrimVisualization;

import lab_4.WeightedGraph.WeightedGraph;
import lab_4.WeightedGraph.WeightedEdge;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;

/**
 * A visual representation of Prim's algorithm for finding a Minimum Spanning Tree in an undirected weighted graph.
 * This visualizer shows the step-by-step process of building an MST using Prim's algorithm.
 */
public class PrimAlgorithmVisualizer extends JFrame {
    private WeightedGraph<String> graph;
    private Map<String, Point2D.Double> vertexPositions;
    private final int VERTEX_RADIUS = 20;

    // Colors for visualization
    private final Color VERTEX_COLOR = new Color(100, 149, 237);         // Cornflower blue
    private final Color VERTEX_IN_MST_COLOR = new Color(50, 205, 50);    // Lime green
    private final Color VERTEX_SELECTED_COLOR = new Color(255, 165, 0);  // Orange
    private final Color EDGE_COLOR = new Color(105, 105, 105);           // Dim gray
    private final Color EDGE_IN_MST_COLOR = new Color(34, 139, 34);      // Forest green
    private final Color EDGE_CONSIDERED_COLOR = new Color(255, 140, 0);  // Dark orange
    private final Color EDGE_REJECTED_COLOR = new Color(178, 34, 34);    // Firebrick red

    private final Font VERTEX_FONT = new Font("Arial", Font.BOLD, 14);
    private final Font WEIGHT_FONT = new Font("Arial", Font.PLAIN, 12);
    private final Font INFO_FONT = new Font("Arial", Font.PLAIN, 14);

    // For dragging vertices
    private String selectedVertex = null;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;

    // For Prim's algorithm
    private Set<String> verticesInMST;
    private Map<String, WeightedEdge<String>> edgesInMST;
    private PriorityQueue<PrimAlgorithmVisualizer.EdgeEntry> edgeQueue;
    private Set<String> verticesVisited;
    private Set<EdgePair> consideredEdges;
    private Set<EdgePair> rejectedEdges;
    private double totalMSTCost;
    private boolean algorithmComplete;
    private String currentVertex;
    private EdgeEntry currentEdge;

    // For the animation
    private Timer algorithmTimer;
    private JButton nextStepButton;
    private JButton autoRunButton;
    private JLabel statusLabel;
    private JPanel controlPanel;
    private JButton resetButton;
    private boolean autoRunning;
    private GraphPanel graphPanel;

    /**
     * Constructor for PrimAlgorithmVisualizer
     * @param graph The undirected weighted graph to visualize
     */
    public PrimAlgorithmVisualizer(WeightedGraph<String> graph) {
        if (graph.isDirected()) {
            throw new IllegalArgumentException("Prim's algorithm visualization only works for undirected graphs");
        }

        this.graph = graph;
        this.vertexPositions = new HashMap<>();

        setTitle("Prim's Algorithm Visualization");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialize vertex positions in a circle layout
        initializeVertexPositions();

        // Initialize algorithm data structures
        resetAlgorithm();

        // Create UI components
        createUI();

        setVisible(true);
    }

    private void createUI() {
        setLayout(new BorderLayout());

        // Create graph panel
        graphPanel = new GraphPanel();
        add(graphPanel, BorderLayout.CENTER);

        // Add mouse interactions for dragging vertices
        addMouseInteractions(graphPanel);

        // Create control panel
        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        nextStepButton = new JButton("Next Step");
        nextStepButton.addActionListener(e -> performNextStep());

        autoRunButton = new JButton("Auto Run");
        autoRunButton.addActionListener(e -> toggleAutoRun());

        resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> resetVisualization());

        statusLabel = new JLabel("Click 'Next Step' to start Prim's algorithm");
        statusLabel.setFont(INFO_FONT);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        controlPanel.add(nextStepButton);
        controlPanel.add(autoRunButton);
        controlPanel.add(resetButton);
        controlPanel.add(statusLabel);

        add(controlPanel, BorderLayout.SOUTH);

        // Create legend
        JPanel legendPanel = createLegendPanel();
        add(legendPanel, BorderLayout.NORTH);

        // Create algorithm timer
        algorithmTimer = new Timer(1000, e -> {
            if (!algorithmComplete) {
                performNextStep();
            } else {
                stopAutoRun();
            }
        });
    }

    private JPanel createLegendPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel normalVertexLabel = createLegendItem("Unvisited Vertex", VERTEX_COLOR);
        JLabel mstVertexLabel = createLegendItem("Vertex in MST", VERTEX_IN_MST_COLOR);
        JLabel selectedVertexLabel = createLegendItem("Current Vertex", VERTEX_SELECTED_COLOR);
        JLabel normalEdgeLabel = createLegendItem("Normal Edge", EDGE_COLOR);
        JLabel mstEdgeLabel = createLegendItem("Edge in MST", EDGE_IN_MST_COLOR);
        JLabel consideredEdgeLabel = createLegendItem("Considered Edge", EDGE_CONSIDERED_COLOR);
        JLabel rejectedEdgeLabel = createLegendItem("Rejected Edge", EDGE_REJECTED_COLOR);

        panel.add(normalVertexLabel);
        panel.add(mstVertexLabel);
        panel.add(selectedVertexLabel);
        panel.add(normalEdgeLabel);
        panel.add(mstEdgeLabel);
        panel.add(consideredEdgeLabel);
        panel.add(rejectedEdgeLabel);

        return panel;
    }

    private JLabel createLegendItem(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        return label;
    }

    private void initializeVertexPositions() {
        Set<String> vertices = graph.getVertices();
        int numVertices = vertices.size();

        if (numVertices == 0) return;

        // Calculate circle center and radius
        int centerX = 450;
        int centerY = 300;
        int radius = 200;

        // Position vertices in a circle
        int i = 0;
        for (String vertex : vertices) {
            double angle = 2 * Math.PI * i / numVertices;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            vertexPositions.put(vertex, new Point2D.Double(x, y));
            i++;
        }
    }

    private void addMouseInteractions(GraphPanel panel) {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (algorithmComplete || !autoRunning) {
                    Point point = e.getPoint();

                    // Check if a vertex is selected
                    for (Map.Entry<String, Point2D.Double> entry : vertexPositions.entrySet()) {
                        Point2D.Double pos = entry.getValue();
                        if (distance(point.x, point.y, pos.x, pos.y) <= VERTEX_RADIUS) {
                            selectedVertex = entry.getKey();
                            dragOffsetX = (int) (point.x - pos.x);
                            dragOffsetY = (int) (point.y - pos.y);
                            break;
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                selectedVertex = null;
            }
        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedVertex != null && (algorithmComplete || !autoRunning)) {
                    Point point = e.getPoint();
                    vertexPositions.get(selectedVertex).x = point.x - dragOffsetX;
                    vertexPositions.get(selectedVertex).y = point.y - dragOffsetY;
                    panel.repaint();
                }
            }
        });
    }

    private double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    /**
     * Reset the algorithm to its initial state
     */
    private void resetAlgorithm() {
        verticesInMST = new HashSet<>();
        edgesInMST = new HashMap<>();
        edgeQueue = new PriorityQueue<>();
        verticesVisited = new HashSet<>();
        consideredEdges = new HashSet<>();
        rejectedEdges = new HashSet<>();
        totalMSTCost = 0;
        algorithmComplete = false;
        currentVertex = null;
        currentEdge = null;
    }

    /**
     * Reset the visualization to its initial state
     */
    private void resetVisualization() {
        resetAlgorithm();
        stopAutoRun();
        statusLabel.setText("Click 'Next Step' to start Prim's algorithm");
        graphPanel.repaint();
    }

    /**
     * Toggle auto-run mode
     */
    private void toggleAutoRun() {
        if (autoRunning) {
            stopAutoRun();
        } else {
            startAutoRun();
        }
    }

    /**
     * Start auto-run mode
     */
    private void startAutoRun() {
        autoRunButton.setText("Stop Auto Run");
        nextStepButton.setEnabled(false);
        resetButton.setEnabled(false);
        autoRunning = true;
        algorithmTimer.start();
    }

    /**
     * Stop auto-run mode
     */
    private void stopAutoRun() {
        algorithmTimer.stop();
        autoRunButton.setText("Auto Run");
        nextStepButton.setEnabled(true);
        resetButton.setEnabled(true);
        autoRunning = false;
    }

    /**
     * Perform the next step of Prim's algorithm
     */
    private void performNextStep() {
        if (algorithmComplete) {
            statusLabel.setText("Algorithm complete! MST cost: " + String.format("%.1f", totalMSTCost));
            return;
        }

        // If no vertices in MST yet, choose a random starting vertex
        if (verticesInMST.isEmpty()) {
            String startVertex = graph.getVertices().iterator().next();
            verticesInMST.add(startVertex);
            verticesVisited.add(startVertex);
            currentVertex = startVertex;

            // Add all edges from the start vertex to the priority queue
            for (WeightedEdge<String> edge : graph.getNeighbors(startVertex)) {
                if (!verticesVisited.contains(edge.target)) {
                    EdgeEntry entry = new EdgeEntry(startVertex, edge.target, edge.weight);
                    edgeQueue.add(entry);
                    consideredEdges.add(new EdgePair(startVertex, edge.target));
                }
            }

            statusLabel.setText("Started with vertex: " + startVertex);
        } else {
            // If no more edges to consider or all vertices visited
            if (edgeQueue.isEmpty() || verticesVisited.size() == graph.getVertices().size()) {
                algorithmComplete = true;
                statusLabel.setText("Algorithm complete! MST cost: " + String.format("%.1f", totalMSTCost));
                return;
            }

            // Get the edge with minimum weight
            currentEdge = edgeQueue.poll();
            String fromVertex = currentEdge.source;
            String toVertex = currentEdge.target;
            double weight = currentEdge.weight;

            // If the target vertex is already visited, reject this edge
            if (verticesVisited.contains(toVertex)) {
                consideredEdges.remove(new EdgePair(fromVertex, toVertex));
                rejectedEdges.add(new EdgePair(fromVertex, toVertex));
                statusLabel.setText("Rejected edge: " + fromVertex + " -> " + toVertex + " (weight: " + weight + ")");
                currentEdge = null;
                return;
            }

            // Add the edge to MST
            verticesInMST.add(toVertex);
            verticesVisited.add(toVertex);
            edgesInMST.put(toVertex, new WeightedEdge<>(fromVertex, weight));
            totalMSTCost += weight;
            currentVertex = toVertex;

            statusLabel.setText("Added edge: " + fromVertex + " -> " + toVertex + " (weight: " + weight + ")");

            // Add all edges from the current vertex to the priority queue
            for (WeightedEdge<String> edge : graph.getNeighbors(toVertex)) {
                if (!verticesVisited.contains(edge.target)) {
                    EdgeEntry entry = new EdgeEntry(toVertex, edge.target, edge.weight);
                    edgeQueue.add(entry);
                    consideredEdges.add(new EdgePair(toVertex, edge.target));
                }
            }
        }

        graphPanel.repaint();
    }

    /**
     * Edge entry for the priority queue used in Prim's algorithm
     */
    private class EdgeEntry implements Comparable<EdgeEntry> {
        String source;
        String target;
        double weight;

        public EdgeEntry(String source, String target, double weight) {
            this.source = source;
            this.target = target;
            this.weight = weight;
        }

        @Override
        public int compareTo(EdgeEntry other) {
            return Double.compare(this.weight, other.weight);
        }
    }

    /**
     * EdgePair class for tracking considered and rejected edges
     */
    private class EdgePair {
        String source;
        String target;

        public EdgePair(String source, String target) {
            // For undirected graphs, normalize the order
            if (source.compareTo(target) < 0) {
                this.source = source;
                this.target = target;
            } else {
                this.source = target;
                this.target = source;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            EdgePair edgePair = (EdgePair) o;
            return source.equals(edgePair.source) && target.equals(edgePair.target);
        }

        @Override
        public int hashCode() {
            return Objects.hash(source, target);
        }
    }

    /**
     * Panel for drawing the graph and algorithm visualization
     */
    private class GraphPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            // Enable anti-aliasing for smoother drawing
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw edges
            drawEdges(g2d);

            // Draw vertices
            drawVertices(g2d);

            // Draw algorithm info
            drawAlgorithmInfo(g2d);
        }

        private void drawEdges(Graphics2D g2d) {
            g2d.setStroke(new BasicStroke(1.5f));

            // Draw normal edges first
            for (String fromVertex : graph.getVertices()) {
                Point2D.Double fromPos = vertexPositions.get(fromVertex);

                for (WeightedEdge<String> edge : graph.getNeighbors(fromVertex)) {
                    String toVertex = edge.target;

                    // Only process each edge once (for undirected graphs)
                    if (fromVertex.compareTo(toVertex) > 0) {
                        continue;
                    }

                    Point2D.Double toPos = vertexPositions.get(toVertex);

                    // Check if this edge is in MST
                    boolean isInMST = (edgesInMST.containsKey(toVertex) && edgesInMST.get(toVertex).target.equals(fromVertex)) ||
                            (edgesInMST.containsKey(fromVertex) && edgesInMST.get(fromVertex).target.equals(toVertex));

                    // Check if this edge is being considered
                    boolean isConsidered = consideredEdges.contains(new EdgePair(fromVertex, toVertex));

                    // Check if this edge was rejected
                    boolean isRejected = rejectedEdges.contains(new EdgePair(fromVertex, toVertex));

                    Color edgeColor;
                    if (isInMST) {
                        edgeColor = EDGE_IN_MST_COLOR;
                        g2d.setStroke(new BasicStroke(3.0f));
                    } else if (isRejected) {
                        edgeColor = EDGE_REJECTED_COLOR;
                        g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
                    } else if (isConsidered) {
                        edgeColor = EDGE_CONSIDERED_COLOR;
                        g2d.setStroke(new BasicStroke(2.0f));
                    } else {
                        edgeColor = EDGE_COLOR;
                        g2d.setStroke(new BasicStroke(1.5f));
                    }

                    drawEdge(g2d, fromPos, toPos, edge.weight, edgeColor);
                }
            }
        }

        private void drawEdge(Graphics2D g2d, Point2D.Double from, Point2D.Double to, double weight, Color edgeColor) {
            // Calculate edge direction vector
            double dx = to.x - from.x;
            double dy = to.y - from.y;
            double length = Math.sqrt(dx * dx + dy * dy);

            // Normalize the direction vector
            double nx = dx / length;
            double ny = dy / length;

            // Calculate start and end points (adjusted to the edge of the vertex circles)
            double startX = from.x + nx * VERTEX_RADIUS;
            double startY = from.y + ny * VERTEX_RADIUS;
            double endX = to.x - nx * VERTEX_RADIUS;
            double endY = to.y - ny * VERTEX_RADIUS;

            // Set the color for this edge
            g2d.setColor(edgeColor);

            // Draw the edge line
            g2d.drawLine((int) startX, (int) startY, (int) endX, (int) endY);

            // Draw the weight at the middle of the edge
            drawWeight(g2d, startX, startY, endX, endY, weight, edgeColor);
        }

        private void drawWeight(Graphics2D g2d, double startX, double startY, double endX, double endY, double weight, Color weightColor) {
            // Calculate the middle point of the edge
            double midX = (startX + endX) / 2;
            double midY = (startY + endY) / 2;

            // Calculate the perpendicular vector to offset the weight text
            double dx = endX - startX;
            double dy = endY - startY;
            double length = Math.sqrt(dx * dx + dy * dy);
            double nx = -dy / length;  // Perpendicular normalized vector
            double ny = dx / length;

            // Offset the weight text from the edge line
            double offsetX = midX + nx * 12;
            double offsetY = midY + ny * 12;

            // Format the weight value (rounded to one decimal place if needed)
            String weightStr = (weight == Math.floor(weight)) ?
                    String.valueOf((int)weight) : String.format("%.1f", weight);

            // Set the color and font for weight text
            g2d.setColor(weightColor);
            g2d.setFont(WEIGHT_FONT);

            // Draw the weight text
            FontMetrics metrics = g2d.getFontMetrics();
            int textWidth = metrics.stringWidth(weightStr);
            int textHeight = metrics.getHeight();

            // Draw with white background for better readability
            g2d.setColor(new Color(255, 255, 255, 200));
            g2d.fillRect((int)(offsetX - textWidth/2 - 2), (int)(offsetY - textHeight/2), textWidth + 4, textHeight);

            g2d.setColor(weightColor);
            g2d.drawString(weightStr, (float)(offsetX - textWidth/2), (float)(offsetY + textHeight/4));
        }

        private void drawVertices(Graphics2D g2d) {
            g2d.setFont(VERTEX_FONT);

            for (String vertex : graph.getVertices()) {
                Point2D.Double pos = vertexPositions.get(vertex);

                // Determine the vertex color based on its state
                Color vertexColor;
                if (vertex.equals(currentVertex)) {
                    vertexColor = VERTEX_SELECTED_COLOR;  // Current vertex
                } else if (verticesInMST.contains(vertex)) {
                    vertexColor = VERTEX_IN_MST_COLOR;    // Vertex in MST
                } else {
                    vertexColor = VERTEX_COLOR;           // Normal vertex
                }

                // Draw vertex circle
                g2d.setColor(vertexColor);
                g2d.fillOval((int) (pos.x - VERTEX_RADIUS), (int) (pos.y - VERTEX_RADIUS),
                        2 * VERTEX_RADIUS, 2 * VERTEX_RADIUS);

                // Draw vertex border
                g2d.setColor(Color.BLACK);
                g2d.drawOval((int) (pos.x - VERTEX_RADIUS), (int) (pos.y - VERTEX_RADIUS),
                        2 * VERTEX_RADIUS, 2 * VERTEX_RADIUS);

                // Draw vertex label
                FontMetrics metrics = g2d.getFontMetrics();
                int textWidth = metrics.stringWidth(vertex);
                int textHeight = metrics.getHeight();

                g2d.setColor(Color.WHITE);
                g2d.drawString(vertex, (float) (pos.x - textWidth / 2),
                        (float) (pos.y + textHeight / 4));
            }
        }

        private void drawAlgorithmInfo(Graphics2D g2d) {
            g2d.setFont(INFO_FONT);
            g2d.setColor(Color.BLACK);

            String info = "MST Cost: " + String.format("%.1f", totalMSTCost);
            g2d.drawString(info, 20, 50);
        }
    }

    /**
     * Main method to test the visualizer
     */
    public static void main(String[] args) {
        // Create a sample undirected weighted graph
        WeightedGraph<String> graph = new WeightedGraph<>(false);

        // Add vertices
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addVertex("D");
        graph.addVertex("E");
        graph.addVertex("F");

        // Add edges
        graph.addEdge("A", "B", 4);
        graph.addEdge("A", "F", 2);
        graph.addEdge("B", "C", 6);
        graph.addEdge("B", "F", 5);
        graph.addEdge("C", "D", 3);
        graph.addEdge("C", "F", 1);
        graph.addEdge("D", "E", 2);
        graph.addEdge("E", "F", 4);

        // Launch the visualizer
        SwingUtilities.invokeLater(() -> new PrimAlgorithmVisualizer(graph));
    }

    public static void visualizeUndirected(WeightedGraph<String> graph) {
        SwingUtilities.invokeLater(() -> new PrimAlgorithmVisualizer(graph));
    }
}
