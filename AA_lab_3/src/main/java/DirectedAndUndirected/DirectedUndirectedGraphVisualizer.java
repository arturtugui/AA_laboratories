package DirectedAndUndirected;

import Graph.Graph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class DirectedUndirectedGraphVisualizer extends JFrame {
    private Graph<String> graph;
    private Map<String, Point> vertexPositions;
    private static final int VERTEX_RADIUS = 20;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    public DirectedUndirectedGraphVisualizer(Graph<String> graph) {
        this.graph = graph;
        this.vertexPositions = new HashMap<>();

        // Initialize the frame
        setTitle("Graph.Graph Visualization");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Auto-generate positions for vertices in a circle layout
        generateVertexPositions();

        // Add a custom panel for drawing
        add(new GraphPanel());

        // Show the frame
        setVisible(true);
    }

    private void generateVertexPositions() {
        Set<String> vertices = (Set<String>) graph.getVertices();
        int numVertices = vertices.size();

        if (numVertices == 0) return;

        // Arrange vertices in a circle
        int centerX = WIDTH / 2;
        int centerY = HEIGHT / 2;
        int radius = Math.min(WIDTH, HEIGHT) / 3;

        int i = 0;
        for (String vertex : vertices) {
            double angle = 2 * Math.PI * i / numVertices;
            int x = (int) (centerX + radius * Math.cos(angle));
            int y = (int) (centerY + radius * Math.sin(angle));
            vertexPositions.put(vertex, new Point(x, y));
            i++;
        }
    }

    class GraphPanel extends JPanel {
        public GraphPanel() {
            setBackground(Color.WHITE);

            // Add ability to drag vertices
            MouseAdapter mouseAdapter = new MouseAdapter() {
                private String selectedVertex = null;
                private int offsetX, offsetY;

                @Override
                public void mousePressed(MouseEvent e) {
                    Point p = e.getPoint();
                    selectedVertex = getVertexAt(p);

                    if (selectedVertex != null) {
                        Point vertexPos = vertexPositions.get(selectedVertex);
                        offsetX = p.x - vertexPos.x;
                        offsetY = p.y - vertexPos.y;
                    }
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    if (selectedVertex != null) {
                        vertexPositions.put(selectedVertex,
                                new Point(e.getX() - offsetX, e.getY() - offsetY));
                        repaint();
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    selectedVertex = null;
                }
            };

            addMouseListener(mouseAdapter);
            addMouseMotionListener(mouseAdapter);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            // Enable anti-aliasing for smoother drawing
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw edges first (so they appear behind vertices)
            drawEdges(g2d);

            // Draw vertices
            drawVertices(g2d);
        }

        private void drawVertices(Graphics2D g2d) {
            Set<String> vertices = (Set<String>) graph.getVertices();

            for (String vertex : vertices) {
                Point position = vertexPositions.get(vertex);

                // Draw vertex circle
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.fillOval(position.x - VERTEX_RADIUS,
                        position.y - VERTEX_RADIUS,
                        2 * VERTEX_RADIUS,
                        2 * VERTEX_RADIUS);

                // Draw vertex border
                g2d.setColor(Color.BLACK);
                g2d.drawOval(position.x - VERTEX_RADIUS,
                        position.y - VERTEX_RADIUS,
                        2 * VERTEX_RADIUS,
                        2 * VERTEX_RADIUS);

                // Draw vertex label
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(vertex);
                int textHeight = fm.getHeight();

                g2d.drawString(vertex,
                        position.x - textWidth / 2,
                        position.y + textHeight / 4);
            }
        }

        private void drawEdges(Graphics2D g2d) {
            Map<String, List<String>> adjacencyList = getAdjacencyList();
            boolean isDirected = graph.isDirected();

            for (String from : adjacencyList.keySet()) {
                Point fromPos = vertexPositions.get(from);

                for (String to : adjacencyList.get(from)) {
                    Point toPos = vertexPositions.get(to);

                    // Draw the edge line
                    g2d.setColor(Color.BLACK);
                    g2d.setStroke(new BasicStroke(1.5f));

                    if (isDirected) {
                        drawArrow(g2d, fromPos, toPos);
                    } else {
                        g2d.drawLine(fromPos.x, fromPos.y, toPos.x, toPos.y);
                    }
                }
            }
        }

        private void drawArrow(Graphics2D g2d, Point from, Point to) {
            int dx = to.x - from.x;
            int dy = to.y - from.y;
            double length = Math.sqrt(dx * dx + dy * dy);

            // Calculate points where the line meets the circles
            double fromRatio = VERTEX_RADIUS / length;
            double toRatio = (length - VERTEX_RADIUS) / length;

            int x1 = (int) (from.x + dx * fromRatio);
            int y1 = (int) (from.y + dy * fromRatio);
            int x2 = (int) (from.x + dx * toRatio);
            int y2 = (int) (from.y + dy * toRatio);

            // Draw the main line
            g2d.drawLine(x1, y1, x2, y2);

            // Draw arrowhead
            double arrowLength = 10;
            double angle = Math.atan2(dy, dx);
            double arrowAngle1 = angle - Math.PI / 6;
            double arrowAngle2 = angle + Math.PI / 6;

            int ax1 = (int) (x2 - arrowLength * Math.cos(arrowAngle1));
            int ay1 = (int) (y2 - arrowLength * Math.sin(arrowAngle1));
            int ax2 = (int) (x2 - arrowLength * Math.cos(arrowAngle2));
            int ay2 = (int) (y2 - arrowLength * Math.sin(arrowAngle2));

            g2d.drawLine(x2, y2, ax1, ay1);
            g2d.drawLine(x2, y2, ax2, ay2);
        }

        private String getVertexAt(Point point) {
            for (Map.Entry<String, Point> entry : vertexPositions.entrySet()) {
                Point vertexPos = entry.getValue();
                double distance = point.distance(vertexPos);

                if (distance <= VERTEX_RADIUS) {
                    return entry.getKey();
                }
            }
            return null;
        }
    }

    // Helper method to access the adjacency list from the Graph.Graph class
    @SuppressWarnings("unchecked")
    private Map<String, List<String>> getAdjacencyList() {
        try {
            java.lang.reflect.Field field = Graph.class.getDeclaredField("adjacencyList");
            field.setAccessible(true);
            return (Map<String, List<String>>) field.get(graph);
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public static void main(String[] args) {
        // Example usage
        Graph<String> graph = new Graph<String>(true); // true for directed graph

        // Add vertices
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addVertex("D");
        graph.addVertex("E");

        // Add edges
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("B", "D");
        graph.addEdge("C", "D");
        graph.addEdge("D", "E");
        graph.addEdge("E", "A");

        // Create visualization
        SwingUtilities.invokeLater(() -> new DirectedUndirectedGraphVisualizer(graph));
    }
}

