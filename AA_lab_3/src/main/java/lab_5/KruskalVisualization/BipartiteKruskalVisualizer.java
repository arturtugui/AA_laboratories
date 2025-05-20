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

import static lab_4.BipartiteWeighted.BipartiteWeightedVisualizer.getBipartiteWeightedPartitions;

/**
 * A visual representation of Kruskal's algorithm for finding a Minimum Spanning Tree in a bipartite weighted graph.
 * This visualizer shows the step-by-step process of building an MST using Kruskal's algorithm while maintaining
 * the bipartite layout structure.
 */
public class BipartiteKruskalVisualizer extends JFrame {
    private WeightedGraph<String> graph;
    private Map<String, Point2D.Double> vertexPositions;
    private Set<String> setU;
    private Set<String> setV;
    private final int VERTEX_RADIUS = 20;
    private final int WIDTH = 900;
    private final int HEIGHT = 700;

    // Colors for visualization
    private final Color SET_U_COLOR = new Color(173, 216, 230); // Light blue
    private final Color SET_V_COLOR = new Color(255, 182, 193); // Light pink
    private final Color SET_U_IN_MST_COLOR = new Color(65, 105, 225); // Royal blue
    private final Color SET_V_IN_MST_COLOR = new Color(219, 112, 147); // Medium pink
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

    // For Kruskal's algorithm
    private Set<String> verticesInMST;
    private Set<EdgeEntry> edgesInMST;
    private List<EdgeEntry> allEdgesSorted;
    private int currentEdgeIndex;
    private Map<String, String> parentMap; // For disjoint set operations
    private Map<String, Integer> rankMap;  // For disjoint set operations
    private Set<EdgeEntry> consideredEdges;
    private Set<EdgeEntry> rejectedEdges;
    private double totalMSTCost;
    private boolean algorithmComplete;
    private EdgeEntry currentEdge;

    // For the animation
    private Timer algorithmTimer;
    private JButton nextStepButton;
    private JButton autoRunButton;
    private JLabel statusLabel;
    private JPanel controlPanel;
    private JButton resetButton;
    private boolean autoRunning;
    private BipartiteGraphPanel graphPanel;

    /**
     * Constructor for BipartiteKruskalVisualizer
     * @param graph The undirected weighted graph to visualize
     * @param setU First set of vertices in the bipartite graph
     * @param setV Second set of vertices in the bipartite graph
     */
    public BipartiteKruskalVisualizer(WeightedGraph<String> graph, Set<String> setU, Set<String> setV) {
        if (graph.isDirected()) {
            throw new IllegalArgumentException("Kruskal's algorithm visualization only works for undirected graphs");
        }

        this.graph = graph;
        this.setU = setU;
        this.setV = setV;
        this.vertexPositions = new HashMap<>();

        setTitle("Bipartite Kruskal's Algorithm Visualization");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialize vertex positions in bipartite layout
        initializeBipartiteLayout();

        // Initialize algorithm data structures
        resetAlgorithm();

        // Create UI components
        createUI();

        setVisible(true);
    }

    private void createUI() {
        setLayout(new BorderLayout());

        // Create graph panel
        graphPanel = new BipartiteGraphPanel();
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
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 15, 10));

        JLabel uVertexLabel = createLegendItem("Set U Vertex", SET_U_COLOR);
        JLabel vVertexLabel = createLegendItem("Set V Vertex", SET_V_COLOR);
        JLabel uMstVertexLabel = createLegendItem("Set U in MST", SET_U_IN_MST_COLOR);
        JLabel vMstVertexLabel = createLegendItem("Set V in MST", SET_V_IN_MST_COLOR);
        JLabel selectedVertexLabel = createLegendItem("Selected Vertex", VERTEX_SELECTED_COLOR);
        JLabel normalEdgeLabel = createLegendItem("Normal Edge", EDGE_COLOR);
        JLabel mstEdgeLabel = createLegendItem("Edge in MST", EDGE_IN_MST_COLOR);
        JLabel consideredEdgeLabel = createLegendItem("Considered Edge", EDGE_CONSIDERED_COLOR);
        JLabel rejectedEdgeLabel = createLegendItem("Rejected Edge", EDGE_REJECTED_COLOR);

        panel.add(uVertexLabel);
        panel.add(vVertexLabel);
        panel.add(uMstVertexLabel);
        panel.add(vMstVertexLabel);
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

    private void initializeBipartiteLayout() {
        int leftX = WIDTH / 4;
        int rightX = 3 * WIDTH / 4;

        // Calculate the vertical spacing for each set
        int spacingU = Math.max(30, HEIGHT / (setU.size() + 1));
        int spacingV = Math.max(30, HEIGHT / (setV.size() + 1));

        // Arrange vertices in set U on the left side
        int i = 0;
        for (String vertex : setU) {
            int y = (i + 1) * spacingU;
            if (y > HEIGHT - 50) y = HEIGHT - 50 - (10 * (i % 5)); // Prevent going off screen
            vertexPositions.put(vertex, new Point2D.Double(leftX, y));
            i++;
        }

        // Arrange vertices in set V on the right side
        i = 0;
        for (String vertex : setV) {
            int y = (i + 1) * spacingV;
            if (y > HEIGHT - 50) y = HEIGHT - 50 - (10 * (i % 5)); // Prevent going off screen
            vertexPositions.put(vertex, new Point2D.Double(rightX, y));
            i++;
        }
    }

    private void addMouseInteractions(BipartiteGraphPanel panel) {
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
        edgesInMST = new HashSet<>();
        allEdgesSorted = new ArrayList<>();
        currentEdgeIndex = 0;
        parentMap = new HashMap<>();
        rankMap = new HashMap<>();
        consideredEdges = new HashSet<>();
        rejectedEdges = new HashSet<>();
        totalMSTCost = 0;
        algorithmComplete = false;
        currentEdge = null;

        // Initialize disjoint sets
        for (String vertex : graph.getVertices()) {
            parentMap.put(vertex, vertex);  // Each vertex is initially its own parent
            rankMap.put(vertex, 0);         // Initial rank is 0
        }

        // Collect and sort all edges
        for (String source : graph.getVertices()) {
            for (WeightedEdge<String> edge : graph.getNeighbors(source)) {
                String target = edge.target;
                // For undirected graphs, add each edge only once
                if (source.compareTo(target) < 0) {
                    allEdgesSorted.add(new EdgeEntry(source, target, edge.weight));
                }
            }
        }

        // Sort edges by weight (ascending)
        Collections.sort(allEdgesSorted);
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
     * Find the representative of a vertex (with path compression)
     */
    private String find(String vertex) {
        if (!parentMap.get(vertex).equals(vertex)) {
            parentMap.put(vertex, find(parentMap.get(vertex)));
        }
        return parentMap.get(vertex);
    }

    /**
     * Union two sets (by rank)
     */
    private void union(String x, String y) {
        String rootX = find(x);
        String rootY = find(y);

        if (rootX.equals(rootY)) return;

        // Attach smaller rank tree under root of high rank tree
        if (rankMap.get(rootX) < rankMap.get(rootY)) {
            parentMap.put(rootX, rootY);
        } else if (rankMap.get(rootX) > rankMap.get(rootY)) {
            parentMap.put(rootY, rootX);
        } else {
            // If ranks are same, make one as root and increment its rank
            parentMap.put(rootY, rootX);
            rankMap.put(rootX, rankMap.get(rootX) + 1);
        }
    }

    /**
     * Perform the next step of Kruskal's algorithm
     */
    private void performNextStep() {
        if (algorithmComplete) {
            statusLabel.setText("Algorithm complete! MST cost: " + String.format("%.1f", totalMSTCost));
            return;
        }

        // Check if we've processed all edges
        if (currentEdgeIndex >= allEdgesSorted.size()) {
            algorithmComplete = true;
            statusLabel.setText("Algorithm complete! MST cost: " + String.format("%.1f", totalMSTCost));
            return;
        }

        // Get the next edge to consider
        currentEdge = allEdgesSorted.get(currentEdgeIndex++);
        String source = currentEdge.source;
        String target = currentEdge.target;
        double weight = currentEdge.weight;

        // Mark this edge as being considered
        consideredEdges.add(currentEdge);

        // Check if including this edge would create a cycle
        String rootSource = find(source);
        String rootTarget = find(target);

        if (!rootSource.equals(rootTarget)) {
            // This edge doesn't create a cycle, so include it in the MST
            edgesInMST.add(currentEdge);
            union(source, target);
            totalMSTCost += weight;

            // Mark both vertices as being in the MST
            verticesInMST.add(source);
            verticesInMST.add(target);

            statusLabel.setText("Added edge: " + source + " -> " + target + " (weight: " + weight + ")");
        } else {
            // This edge would create a cycle, so reject it
            rejectedEdges.add(currentEdge);
            consideredEdges.remove(currentEdge);
            statusLabel.setText("Rejected edge: " + source + " -> " + target + " (weight: " + weight + ") - would create a cycle");
        }

        graphPanel.repaint();
    }

    /**
     * Edge entry class for representing graph edges
     */
    private class EdgeEntry implements Comparable<EdgeEntry> {
        String source;
        String target;
        double weight;

        public EdgeEntry(String source, String target, double weight) {
            // For undirected graphs, normalize the order
            if (source.compareTo(target) < 0) {
                this.source = source;
                this.target = target;
            } else {
                this.source = target;
                this.target = source;
            }
            this.weight = weight;
        }

        @Override
        public int compareTo(EdgeEntry other) {
            return Double.compare(this.weight, other.weight);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            EdgeEntry edgeEntry = (EdgeEntry) o;
            return Double.compare(edgeEntry.weight, weight) == 0 &&
                    source.equals(edgeEntry.source) &&
                    target.equals(edgeEntry.target);
        }

        @Override
        public int hashCode() {
            return Objects.hash(source, target, weight);
        }
    }

    /**
     * Panel for drawing the bipartite graph and algorithm visualization
     */
    private class BipartiteGraphPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            // Enable anti-aliasing for smoother drawing
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw partition labels
            drawPartitionLabels(g2d);

            // Draw edges
            drawEdges(g2d);

            // Draw vertices
            drawVertices(g2d);

            // Draw algorithm info
            drawAlgorithmInfo(g2d);
        }

        private void drawPartitionLabels(Graphics2D g2d) {
            Font originalFont = g2d.getFont();
            Font labelFont = new Font(originalFont.getName(), Font.BOLD, 16);
            g2d.setFont(labelFont);

            // Draw Set U label
            g2d.setColor(SET_U_COLOR.darker());
            g2d.drawString("Set U", WIDTH / 4 - 25, 30);

            // Draw Set V label
            g2d.setColor(SET_V_COLOR.darker());
            g2d.drawString("Set V", 3 * WIDTH / 4 - 25, 30);

            // Reset font
            g2d.setFont(originalFont);
        }

        private void drawEdges(Graphics2D g2d) {
            g2d.setStroke(new BasicStroke(1.5f));

            // Draw all edges
            for (String fromVertex : graph.getVertices()) {
                Point2D.Double fromPos = vertexPositions.get(fromVertex);
                if (fromPos == null) continue;

                for (WeightedEdge<String> edge : graph.getNeighbors(fromVertex)) {
                    String toVertex = edge.target;
                    Point2D.Double toPos = vertexPositions.get(toVertex);
                    if (toPos == null) continue;

                    // Only process each edge once (for undirected graphs)
                    if (fromVertex.compareTo(toVertex) > 0) {
                        continue;
                    }

                    // Create an EdgeEntry to check against our sets
                    EdgeEntry thisEdge = new EdgeEntry(fromVertex, toVertex, edge.weight);

                    // Check if this edge is in MST
                    boolean isInMST = edgesInMST.contains(thisEdge);

                    // Check if this edge is being considered
                    boolean isConsidered = consideredEdges.contains(thisEdge);

                    // Check if this edge was rejected
                    boolean isRejected = rejectedEdges.contains(thisEdge);

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

            // Draw vertices in both sets
            for (String vertex : graph.getVertices()) {
                Point2D.Double pos = vertexPositions.get(vertex);
                if (pos == null) continue;

                // Check if this vertex is involved in the current edge being considered
                boolean isHighlighted = currentEdge != null &&
                        (vertex.equals(currentEdge.source) || vertex.equals(currentEdge.target));

                // Determine the vertex color based on its set and state
                Color vertexColor;
                if (isHighlighted) {
                    vertexColor = VERTEX_SELECTED_COLOR;  // Highlight vertices in the current edge
                } else if (verticesInMST.contains(vertex)) {
                    // Vertex in MST, color depends on which set it belongs to
                    if (setU.contains(vertex)) {
                        vertexColor = SET_U_IN_MST_COLOR;
                    } else {
                        vertexColor = SET_V_IN_MST_COLOR;
                    }
                } else {
                    // Normal vertex, color depends on which set it belongs to
                    if (setU.contains(vertex)) {
                        vertexColor = SET_U_COLOR;
                    } else {
                        vertexColor = SET_V_COLOR;
                    }
                }

                // Draw vertex circle
                g2d.setColor(vertexColor);
                g2d.fillOval((int) (pos.x - VERTEX_RADIUS), (int) (pos.y - VERTEX_RADIUS),
                        2 * VERTEX_RADIUS, 2 * VERTEX_RADIUS);

                // Draw vertex border
                g2d.setColor(vertexColor.darker());
                g2d.drawOval((int) (pos.x - VERTEX_RADIUS), (int) (pos.y - VERTEX_RADIUS),
                        2 * VERTEX_RADIUS, 2 * VERTEX_RADIUS);

                // Draw vertex label
                FontMetrics metrics = g2d.getFontMetrics();
                int textWidth = metrics.stringWidth(vertex);
                int textHeight = metrics.getHeight();

                g2d.setColor(Color.BLACK);
                g2d.drawString(vertex, (float) (pos.x - textWidth / 2),
                        (float) (pos.y + textHeight / 4));
            }
        }

        private void drawAlgorithmInfo(Graphics2D g2d) {
            g2d.setFont(INFO_FONT);
            g2d.setColor(Color.BLACK);

            String info = "MST Cost: " + String.format("%.1f", totalMSTCost);
            int edgesInMSTCount = edgesInMST.size();
            int totalVertices = graph.getVertices().size();

            // In a connected graph, MST should have n-1 edges where n is the number of vertices
            info += " | Edges: " + edgesInMSTCount + " / " + (totalVertices - 1);

            g2d.drawString(info, 20, 50);
        }
    }

    /**
     * Convenience method to create and display a bipartite Kruskal algorithm visualizer.
     */
    public static void visualizeBipartiteKruskal(WeightedGraph<String> graph, Set<String> setU, Set<String> setV) {
        SwingUtilities.invokeLater(() -> new BipartiteKruskalVisualizer(graph, setU, setV));
    }

    /**
     * Main method to test the visualizer
     */
    public static void main(String[] args) {
        // Create a sample undirected weighted bipartite graph
        WeightedGraph<String> graph = new WeightedGraph<>(false);

        // Create two sets of vertices
        Set<String> setU = new HashSet<>(Arrays.asList("U1", "U2", "U3", "U4"));
        Set<String> setV = new HashSet<>(Arrays.asList("V1", "V2", "V3", "V4"));

        // Add all vertices to the graph
        for (String u : setU) graph.addVertex(u);
        for (String v : setV) graph.addVertex(v);

        // Add some weighted edges between U and V
        graph.addEdge("U1", "V1", 2.5);
        graph.addEdge("U1", "V2", 1.8);
        graph.addEdge("U2", "V1", 3.0);
        graph.addEdge("U2", "V3", 2.3);
        graph.addEdge("U3", "V2", 4.1);
        graph.addEdge("U3", "V4", 1.5);
        graph.addEdge("U4", "V3", 3.7);
        graph.addEdge("U4", "V4", 2.9);

        // Launch the visualizer
        SwingUtilities.invokeLater(() -> new BipartiteKruskalVisualizer(graph, setU, setV));
    }

    public static void visualizeBipartite(WeightedGraph<String> bipartiteGraph) {
        Set<String>[] partitions = getBipartiteWeightedPartitions(bipartiteGraph);
        SwingUtilities.invokeLater(() -> new BipartiteKruskalVisualizer(bipartiteGraph, partitions[0], partitions[1]));
    }
}
