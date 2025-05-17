package lab_4.DirectedAndUndirectedWeighted;

import lab_4.WeightedGraph.WeightedGraph;
import lab_4.WeightedGraph.WeightedEdge;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;

public class DirectedAndUndirectedWeightedVisualizer extends JFrame {
    private WeightedGraph<String> graph;
    private Map<String, Point2D.Double> vertexPositions;
    private final int VERTEX_RADIUS = 20;
    private final Color VERTEX_COLOR = new Color(100, 149, 237); // Cornflower blue
    private final Color EDGE_COLOR = new Color(105, 105, 105);   // Dim gray

    // Colors for directional edges
    private final Color ASCENDING_COLOR = new Color(46, 139, 87);  // Sea green (A→B)
    private final Color DESCENDING_COLOR = new Color(178, 34, 34); // Firebrick red (B→A)

    private final Font VERTEX_FONT = new Font("Arial", Font.BOLD, 14);
    private final Font WEIGHT_FONT = new Font("Arial", Font.PLAIN, 12);

    // For dragging vertices
    private String selectedVertex = null;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;

    public DirectedAndUndirectedWeightedVisualizer(WeightedGraph<String> graph) {
        this.graph = graph;
        this.vertexPositions = new HashMap<>();

        setTitle("Color-Coded Directed Graph Visualizer");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialize vertex positions in a circle layout
        initializeVertexPosititions();

        GraphPanel graphPanel = new GraphPanel();
        add(graphPanel);

        // Add mouse listeners for vertex dragging
        addMouseInteractions(graphPanel);

        // Add legend at the bottom
        if(graph.isDirected()){
            JPanel legendPanel = createLegendPanel();
            add(legendPanel, BorderLayout.SOUTH);
        }

        setVisible(true);
    }

    private JPanel createLegendPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Create legend items
        JLabel ascendingLabel = new JLabel("Ascending order (A→B)");
        ascendingLabel.setForeground(ASCENDING_COLOR);
        ascendingLabel.setFont(new Font("Arial", Font.BOLD, 12));

        JLabel descendingLabel = new JLabel("Descending order (B→A)");
        descendingLabel.setForeground(DESCENDING_COLOR);
        descendingLabel.setFont(new Font("Arial", Font.BOLD, 12));

        // Add a separator between labels
        panel.add(ascendingLabel);
        panel.add(Box.createRigidArea(new Dimension(30, 0)));
        panel.add(descendingLabel);

        return panel;
    }

    private void initializeVertexPosititions() {
        Set<String> vertices = graph.getVertices();
        int numVertices = vertices.size();

        if (numVertices == 0) return;

        // Calculate circle center and radius
        int centerX = 400;
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
        }

        private void drawEdges(Graphics2D g2d) {
            g2d.setStroke(new BasicStroke(1.5f));

            // Process all edges
            for (String fromVertex : graph.getVertices()) {
                Point2D.Double fromPos = vertexPositions.get(fromVertex);

                for (WeightedEdge<String> edge : graph.getNeighbors(fromVertex)) {
                    String toVertex = edge.target;
                    Point2D.Double toPos = vertexPositions.get(toVertex);

                    // Skip duplicate edges in undirected graphs
                    if (!graph.isDirected() && fromVertex.compareTo(toVertex) > 0) {
                        continue;
                    }

                    // Determine edge color based on vertex order (ascending or descending)
                    boolean isAscending = fromVertex.compareTo(toVertex) < 0;
                    Color edgeColor = isAscending ? ASCENDING_COLOR : DESCENDING_COLOR;

                    drawEdge(g2d, fromPos, toPos, fromVertex, toVertex, edge.weight, edgeColor);
                }
            }
        }

        private void drawEdge(Graphics2D g2d, Point2D.Double from, Point2D.Double to,
                              String fromVertex, String toVertex, double weight, Color edgeColor) {
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

            // Draw arrow for directed graphs
            if (graph.isDirected()) {
                drawArrowHead(g2d, startX, startY, endX, endY, edgeColor);
            }

            // Draw the weight at the middle of the edge
            drawWeight(g2d, startX, startY, endX, endY, weight, edgeColor);
        }

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

        private void drawWeight(Graphics2D g2d, double startX, double startY,
                                double endX, double endY, double weight, Color weightColor) {
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

            g2d.drawString(weightStr, (float)(offsetX - textWidth/2), (float)(offsetY + textHeight/4));
        }

        private void drawVertices(Graphics2D g2d) {
            g2d.setFont(VERTEX_FONT);

            for (String vertex : graph.getVertices()) {
                Point2D.Double pos = vertexPositions.get(vertex);

                // Draw vertex circle
                g2d.setColor(VERTEX_COLOR);
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
    }

    public static void main(String[] args) {
        // Create a sample weighted graph for testing
        WeightedGraph<String> graph = new WeightedGraph<String>(true); // Directed graph

        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addVertex("D");
        graph.addVertex("E");

        // Add edges in both directions to demonstrate color coding
        graph.addEdge("A", "B", 5.2); // A→B (ascending)
        graph.addEdge("B", "A", 4.2); // B→A (descending)
        graph.addEdge("A", "C", 3.1); // A→C (ascending)
        graph.addEdge("C", "A", 2.3); // C→A (descending)
        graph.addEdge("B", "D", 2.0); // B→D (ascending)
        graph.addEdge("C", "D", 1.5); // C→D (ascending)
        graph.addEdge("D", "E", 4.3); // D→E (ascending)
        graph.addEdge("E", "A", 2.7); // E→A (descending)

        // Create and display the visualizer
        SwingUtilities.invokeLater(() -> new DirectedAndUndirectedWeightedVisualizer(graph));
    }
}