package lab_4.BipartiteWeighted;

import lab_3.Graph.Graph;
import lab_4.WeightedGraph.WeightedGraph;
import lab_4.WeightedGraph.WeightedEdge;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;

/**
 * A specialized visualizer for weighted bipartite graphs that renders the two vertex sets
 * in different positions and colors, and displays the weights on edges.
 */
public class BipartiteWeightedVisualizer extends JFrame {
    private WeightedGraph<String> graph;
    private Map<String, Point2D.Double> vertexPositions;
    private Set<String> setU;
    private Set<String> setV;
    private final int VERTEX_RADIUS = 20;
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private final Color SET_U_COLOR = new Color(173, 216, 230); // Light blue
    private final Color SET_V_COLOR = new Color(255, 182, 193); // Light pink
    private final Color EDGE_COLOR = new Color(105, 105, 105);  // Dim gray
    private final Font VERTEX_FONT = new Font("Arial", Font.BOLD, 14);
    private final Font WEIGHT_FONT = new Font("Arial", Font.PLAIN, 12);

    // For dragging vertices
    private String selectedVertex = null;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;

    /**
     * Creates a new bipartite weighted graph visualizer with the specified graph and partitions.
     *
     * @param graph The bipartite weighted graph to visualize
     * @param setU The first partition (set U)
     * @param setV The second partition (set V)
     */
    public BipartiteWeightedVisualizer(WeightedGraph<String> graph, Set<String> setU, Set<String> setV) {
        this.graph = graph;
        this.setU = setU;
        this.setV = setV;
        this.vertexPositions = new HashMap<>();

        // Initialize the frame
        setTitle("Weighted Bipartite Graph Visualization");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Generate positions for vertices in bipartite layout
        generateBipartiteLayout();

        // Create the graph panel
        BipartiteGraphPanel graphPanel = new BipartiteGraphPanel();
        add(graphPanel);

        // Add mouse listeners for vertex dragging
        addMouseInteractions(graphPanel);

        // Show the frame
        setVisible(true);
    }

    /**
     * Convenience method to create and display a bipartite weighted graph visualizer.
     */
    public static void visualizeBipartiteWeightedGraph(WeightedGraph<String> graph, Set<String> setU, Set<String> setV) {
        SwingUtilities.invokeLater(() -> new BipartiteWeightedVisualizer(graph, setU, setV));
    }

    /**
     * Generates a bipartite layout with set U on the left and set V on the right.
     */
    private void generateBipartiteLayout() {
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

            @Override
            public void mouseReleased(MouseEvent e) {
                selectedVertex = null;
            }
        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedVertex != null) {
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
     * Panel for rendering the bipartite weighted graph.
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

            // Draw edges first (so they appear behind vertices)
            drawEdges(g2d);

            // Draw vertices
            drawVertices(g2d);
        }

        /**
         * Draws labels for the two partitions.
         */
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

        /**
         * Draws the edges between vertices with weights.
         */
        private void drawEdges(Graphics2D g2d) {
            g2d.setStroke(new BasicStroke(1.5f));

            // Process all edges
            for (String fromVertex : graph.getVertices()) {
                Point2D.Double fromPos = vertexPositions.get(fromVertex);
                if (fromPos == null) continue;

                // Get all neighbors (connected vertices) with edge weights
                for (WeightedEdge<String> edge : graph.getNeighbors(fromVertex)) {
                    String toVertex = edge.target;
                    Point2D.Double toPos = vertexPositions.get(toVertex);
                    if (toPos == null) continue;

                    // Skip duplicate edges in undirected graphs
                    if (!graph.isDirected() && fromVertex.compareTo(toVertex) > 0) {
                        continue;
                    }

                    // Draw edge with appropriate color based on which sets the vertices belong to
                    Color fromColor = setU.contains(fromVertex) ? SET_U_COLOR.darker() : SET_V_COLOR.darker();
                    Color toColor = setU.contains(toVertex) ? SET_U_COLOR.darker() : SET_V_COLOR.darker();

                    drawWeightedEdge(g2d, fromPos, toPos, fromVertex, toVertex, edge.weight, fromColor, toColor);
                }
            }
        }

        /**
         * Draws a weighted edge between two vertices with a gradient color.
         */
        private void drawWeightedEdge(Graphics2D g2d, Point2D.Double from, Point2D.Double to,
                                      String fromVertex, String toVertex, double weight,
                                      Color fromColor, Color toColor) {
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

            // Draw the edge line with gradient
            GradientPaint gradient = new GradientPaint(
                    (float)startX, (float)startY, fromColor,
                    (float)endX, (float)endY, toColor
            );
            g2d.setPaint(gradient);
            g2d.drawLine((int) startX, (int) startY, (int) endX, (int) endY);

            // Draw arrow for directed graphs
            if (graph.isDirected()) {
                drawArrowHead(g2d, startX, startY, endX, endY, toColor);
            }

            // Draw the weight at the middle of the edge
            drawWeight(g2d, startX, startY, endX, endY, weight);
        }

        /**
         * Draws an arrow head for directed edges.
         */
        private void drawArrowHead(Graphics2D g2d, double startX, double startY,
                                   double endX, double endY, Color arrowColor) {
            double arrowLength = 10;
            double arrowWidth = 6;

            // Calculate arrow direction vector
            double dx = endX - startX;
            double dy = endY - startY;
            double length = Math.sqrt(dx * dx + dy * dy);

            // Normalize the direction vector
            double nx = dx / length;
            double ny = dy / length;

            // Calculate perpendicular vector
            double perpX = -ny;
            double perpY = nx;

            // Calculate arrow head points
            double midX = endX - nx * arrowLength;
            double midY = endY - ny * arrowLength;

            double leftX = midX + perpX * arrowWidth;
            double leftY = midY + perpY * arrowWidth;

            double rightX = midX - perpX * arrowWidth;
            double rightY = midY - perpY * arrowWidth;

            // Create and draw the arrow head
            Path2D.Double arrowHead = new Path2D.Double();
            arrowHead.moveTo(endX, endY);
            arrowHead.lineTo(leftX, leftY);
            arrowHead.lineTo(rightX, rightY);
            arrowHead.closePath();

            g2d.setColor(arrowColor);
            g2d.fill(arrowHead);
        }

        /**
         * Draws the weight label on an edge.
         */
        private void drawWeight(Graphics2D g2d, double startX, double startY,
                                double endX, double endY, double weight) {
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
            g2d.setColor(Color.BLACK);
            g2d.setFont(WEIGHT_FONT);

            // Draw the weight text
            FontMetrics metrics = g2d.getFontMetrics();
            int textWidth = metrics.stringWidth(weightStr);
            int textHeight = metrics.getHeight();

            g2d.drawString(weightStr, (float)(offsetX - textWidth/2), (float)(offsetY + textHeight/4));
        }

        /**
         * Draws the vertices with appropriate colors for each partition.
         */
        private void drawVertices(Graphics2D g2d) {
            g2d.setFont(VERTEX_FONT);

            // Draw vertices in set U
            for (String vertex : setU) {
                drawVertex(g2d, vertex, SET_U_COLOR);
            }

            // Draw vertices in set V
            for (String vertex : setV) {
                drawVertex(g2d, vertex, SET_V_COLOR);
            }
        }

        /**
         * Draws a single vertex with the specified color.
         */
        private void drawVertex(Graphics2D g2d, String vertex, Color color) {
            Point2D.Double pos = vertexPositions.get(vertex);
            if (pos == null) return;

            // Draw vertex circle with the specified color
            g2d.setColor(color);
            g2d.fillOval((int) (pos.x - VERTEX_RADIUS), (int) (pos.y - VERTEX_RADIUS),
                    2 * VERTEX_RADIUS, 2 * VERTEX_RADIUS);

            // Draw vertex border
            g2d.setColor(color.darker());
            g2d.drawOval((int) (pos.x - VERTEX_RADIUS), (int) (pos.y - VERTEX_RADIUS),
                    2 * VERTEX_RADIUS, 2 * VERTEX_RADIUS);

            // Draw vertex label
            g2d.setColor(Color.BLACK);
            FontMetrics metrics = g2d.getFontMetrics();
            int textWidth = metrics.stringWidth(vertex);
            int textHeight = metrics.getHeight();

            g2d.drawString(vertex, (float) (pos.x - textWidth / 2),
                    (float) (pos.y + textHeight / 4));
        }
    }

    /**
     * Example main method to demonstrate the bipartite weighted graph visualizer.
     */
    public static <V> Set<V>[] getBipartiteWeightedPartitions(WeightedGraph<V> graph) {
        Set<V> setU = new HashSet<>();
        Set<V> setV = new HashSet<>();

        for (V vertex : graph.getVertices()) {
            String name = vertex.toString();
            if (name.startsWith("U")) {
                setU.add(vertex);
            } else if (name.startsWith("V")) {
                setV.add(vertex);
            }
        }

        return new Set[]{setU, setV};
    }

    public static void main(String[] args) {
        // Create a simple bipartite weighted graph
        WeightedGraph<String> graph = new WeightedGraph<String>(false); // undirected

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

        // Create visualization
        SwingUtilities.invokeLater(() -> visualizeBipartiteWeightedGraph(graph, setU, setV));
    }
}