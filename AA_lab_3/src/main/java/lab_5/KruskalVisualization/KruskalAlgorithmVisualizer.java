package lab_5.KruskalVisualization;

import lab_4.WeightedGraph.WeightedGraph;
import lab_4.WeightedGraph.WeightedEdge;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

/**
 * A visual representation of Kruskal's algorithm for finding a Minimum Spanning Tree in an undirected weighted graph.
 * This visualizer shows the step-by-step process of building an MST using Kruskal's algorithm.
 */
public class KruskalAlgorithmVisualizer extends JFrame {
    private WeightedGraph<String> graph;
    private Map<String, Point2D.Double> vertexPositions;
    private final int VERTEX_RADIUS = 20;

    // Colors for visualization
    private final Color VERTEX_COLOR = new Color(100, 149, 237);         // Cornflower blue
    private final Color VERTEX_IN_MST_COLOR = new Color(50, 205, 50);    // Lime green
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

    // For Kruskal's algorithm
    private List<EdgeTriple> sortedEdges;
    private int currentEdgeIndex;
    private Set<EdgePair> edgesInMST;
    private Set<EdgePair> consideredEdges;
    private Set<EdgePair> rejectedEdges;
    private double totalMSTCost;
    private boolean algorithmComplete;
    private DisjointSets<String> disjointSets;
    private Set<String> verticesInMST;
    private EdgeTriple currentEdge;

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
     * Constructor for KruskalAlgorithmVisualizer
     * @param graph The undirected weighted graph to visualize
     */
    public KruskalAlgorithmVisualizer(WeightedGraph<String> graph) {
        if (graph.isDirected()) {
            throw new IllegalArgumentException("Kruskal's algorithm visualization only works for undirected graphs");
        }

        this.graph = graph;
        this.vertexPositions = new HashMap<>();

        setTitle("Kruskal's Algorithm Visualization");
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

        statusLabel = new JLabel("Click 'Next Step' to start Kruskal's algorithm");
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
        JLabel normalEdgeLabel = createLegendItem("Normal Edge", EDGE_COLOR);
        JLabel mstEdgeLabel = createLegendItem("Edge in MST", EDGE_IN_MST_COLOR);
        JLabel consideredEdgeLabel = createLegendItem("Current Edge", EDGE_CONSIDERED_COLOR);
        JLabel rejectedEdgeLabel = createLegendItem("Rejected Edge (Creates Cycle)", EDGE_REJECTED_COLOR);

        panel.add(normalVertexLabel);
        panel.add(mstVertexLabel);
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
        // Initialize algorithm data structures
        sortedEdges = new ArrayList<>();
        currentEdgeIndex = -1;
        edgesInMST = new HashSet<>();
        consideredEdges = new HashSet<>();
        rejectedEdges = new HashSet<>();
        totalMSTCost = 0;
        algorithmComplete = false;
        disjointSets = new DisjointSets<>(graph.getVertices());
        verticesInMST = new HashSet<>();
        currentEdge = null;

        // Collect all edges and sort them by weight
        for (String source : graph.getVertices()) {
            for (WeightedEdge<String> edge : graph.getNeighbors(source)) {
                // For undirected graphs, add each edge only once
                if (source.compareTo(edge.target) < 0) {
                    sortedEdges.add(new EdgeTriple(source, edge.target, edge.weight));
                }
            }
        }

        // Sort edges by weight
        Collections.sort(sortedEdges);
    }

    /**
     * Reset the visualization to its initial state
     */
    private void resetVisualization() {
        resetAlgorithm();
        stopAutoRun();
        statusLabel.setText("Click 'Next Step' to start Kruskal's algorithm");
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
     * Perform the next step of Kruskal's algorithm
     */
    private void performNextStep() {
        if (algorithmComplete) {
            statusLabel.setText("Algorithm complete! MST cost: " + String.format("%.1f", totalMSTCost));
            return;
        }

        // Process the next edge
        currentEdgeIndex++;

        // Check if we've processed all edges
        if (currentEdgeIndex >= sortedEdges.size()) {
            algorithmComplete = true;
            statusLabel.setText("Algorithm complete! MST cost: " + String.format("%.1f", totalMSTCost));
            return;
        }

        // Get the current edge
        currentEdge = sortedEdges.get(currentEdgeIndex);
        String source = currentEdge.source;
        String target = currentEdge.target;
        double weight = currentEdge.weight;

        // Check if adding this edge creates a cycle
        if (disjointSets.isSameSet(source, target)) {
            // Edge creates a cycle, reject it
            rejectedEdges.add(new EdgePair(source, target));
            statusLabel.setText("Rejected edge: " + source + " → " + target + " (weight: " +
                    String.format("%.1f", weight) + ") - Creates a cycle");
        } else {
            // Edge doesn't create a cycle, add it to MST
            edgesInMST.add(new EdgePair(source, target));
            totalMSTCost += weight;

            // Union the sets
            disjointSets.union(source, target);

            // Add vertices to MST set
            verticesInMST.add(source);
            verticesInMST.add(target);

            statusLabel.setText("Added edge: " + source + " → " + target + " (weight: " +
                    String.format("%.1f", weight) + ") to MST");
        }

        // Add edge to considered set (will be colored differently for visualization)
        consideredEdges.add(new EdgePair(source, target));

        graphPanel.repaint();
    }

    /**
     * Edge triple class for storing edges with weights in sorted order
     */
    private class EdgeTriple implements Comparable<EdgeTriple> {
        String source;
        String target;
        double weight;

        public EdgeTriple(String source, String target, double weight) {
            this.source = source;
            this.target = target;
            this.weight = weight;
        }

        @Override
        public int compareTo(EdgeTriple other) {
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
     * Disjoint Sets (Union-Find) implementation for Kruskal's algorithm
     */
    private static class DisjointSets<V> {
        private final Map<V, V> parent;
        private final Map<V, Integer> rank;

        public DisjointSets(Set<V> vertices) {
            parent = new HashMap<>();
            rank = new HashMap<>();

            // Initialize each vertex as a separate set
            for (V vertex : vertices) {
                parent.put(vertex, vertex);
                rank.put(vertex, 0);
            }
        }

        /**
         * Find the representative of the set containing the given element
         * with path compression
         */
        public V find(V vertex) {
            if (!parent.get(vertex).equals(vertex)) {
                parent.put(vertex, find(parent.get(vertex)));
            }
            return parent.get(vertex);
        }

        /**
         * Union two sets by rank
         */
        public void union(V x, V y) {
            V rootX = find(x);
            V rootY = find(y);

            if (rootX.equals(rootY)) return;

            // Attach smaller rank tree under root of high rank tree
            if (rank.get(rootX) < rank.get(rootY)) {
                parent.put(rootX, rootY);
            } else if (rank.get(rootX) > rank.get(rootY)) {
                parent.put(rootY, rootX);
            } else {
                // If ranks are same, make one as root and increment its rank
                parent.put(rootY, rootX);
                rank.put(rootX, rank.get(rootX) + 1);
            }
        }

        /**
         * Check if two elements are in the same set
         */
        public boolean isSameSet(V x, V y) {
            return find(x).equals(find(y));
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
                    boolean isInMST = edgesInMST.contains(new EdgePair(fromVertex, toVertex));

                    // Check if this edge is being considered (the current edge being processed)
                    boolean isConsidered = currentEdge != null &&
                            ((currentEdge.source.equals(fromVertex) && currentEdge.target.equals(toVertex)) ||
                                    (currentEdge.source.equals(toVertex) && currentEdge.target.equals(fromVertex)));

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
                Color vertexColor = verticesInMST.contains(vertex) ? VERTEX_IN_MST_COLOR : VERTEX_COLOR;

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

            // Display current progress
            String progressInfo = "Processed edges: " + (currentEdgeIndex + 1) + " / " + sortedEdges.size();
            g2d.drawString(progressInfo, 20, 30);

            // Display current MST cost
            String costInfo = "MST Cost: " + String.format("%.1f", totalMSTCost);
            g2d.drawString(costInfo, 20, 50);

            // Display current edge being considered (if any)
            if (currentEdge != null && !algorithmComplete) {
                String edgeInfo = "Current edge: " + currentEdge.source + " → " + currentEdge.target +
                        " (weight: " + String.format("%.1f", currentEdge.weight) + ")";
                g2d.drawString(edgeInfo, 20, 70);
            }
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
        SwingUtilities.invokeLater(() -> new KruskalAlgorithmVisualizer(graph));
    }

    public static void visualizeUndirected(WeightedGraph<String> graph) {
        SwingUtilities.invokeLater(() -> new KruskalAlgorithmVisualizer(graph));
    }
}
