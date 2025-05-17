package lab_3.Bipartite;

import lab_3.Graph.Graph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * A specialized visualizer for bipartite graphs that renders the two vertex sets
 * in different positions and colors to make the bipartite structure clear.
 */
public class BipartiteGraphVisualizer extends JFrame {
    private Graph<String> graph;
    private Map<String, Point> vertexPositions;
    private Set<String> setU;
    private Set<String> setV;
    private static final int VERTEX_RADIUS = 20;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final Color SET_U_COLOR = new Color(173, 216, 230); // Light blue
    private static final Color SET_V_COLOR = new Color(255, 182, 193); // Light pink

    /**
     * Creates a new bipartite graph visualizer with the specified graph and partitions.
     *
     * @param graph The bipartite graph to visualize
     * @param setU The first partition (set U)
     * @param setV The second partition (set V)
     */
    public BipartiteGraphVisualizer(Graph<String> graph, Set<String> setU, Set<String> setV) {
        this.graph = graph;
        this.setU = setU;
        this.setV = setV;
        this.vertexPositions = new HashMap<>();

        // Initialize the frame
        setTitle("Bipartite Graph Visualization");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Generate positions for vertices in bipartite layout
        generateBipartiteLayout();

        // Add a custom panel for drawing
        add(new BipartiteGraphPanel());

        // Show the frame
        setVisible(true);
    }

    /**
     * Convenience method to create and display a bipartite graph visualizer.
     */
    public static void visualizeBipartiteGraph(Graph<String> graph, Set<String> setU, Set<String> setV) {
        new BipartiteGraphVisualizer(graph, setU, setV);
    }

    /**
     * Generates a bipartite layout with set U on the left and set V on the right.
     */
    private void generateBipartiteLayout() {
        int leftX = WIDTH / 4;
        int rightX = 3 * WIDTH / 4;
        int centerY = HEIGHT / 2;

        // Calculate the vertical spacing for each set
        int spacingU = Math.max(30, HEIGHT / (setU.size() + 1));
        int spacingV = Math.max(30, HEIGHT / (setV.size() + 1));

        // Arrange vertices in set U on the left side
        int i = 0;
        for (String vertex : setU) {
            int y = (i + 1) * spacingU;
            if (y > HEIGHT - 50) y = HEIGHT - 50 - (10 * (i % 5)); // Prevent going off screen
            vertexPositions.put(vertex, new Point(leftX, y));
            i++;
        }

        // Arrange vertices in set V on the right side
        i = 0;
        for (String vertex : setV) {
            int y = (i + 1) * spacingV;
            if (y > HEIGHT - 50) y = HEIGHT - 50 - (10 * (i % 5)); // Prevent going off screen
            vertexPositions.put(vertex, new Point(rightX, y));
            i++;
        }
    }

    /**
     * Panel for rendering the bipartite graph.
     */
    class BipartiteGraphPanel extends JPanel {
        public BipartiteGraphPanel() {
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
         * Draws the vertices with different colors for each partition.
         */
        private void drawVertices(Graphics2D g2d) {
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
            Point position = vertexPositions.get(vertex);
            if (position == null) return;

            // Draw vertex circle with the specified color
            g2d.setColor(color);
            g2d.fillOval(position.x - VERTEX_RADIUS,
                    position.y - VERTEX_RADIUS,
                    2 * VERTEX_RADIUS,
                    2 * VERTEX_RADIUS);

            // Draw vertex border
            g2d.setColor(color.darker());
            g2d.drawOval(position.x - VERTEX_RADIUS,
                    position.y - VERTEX_RADIUS,
                    2 * VERTEX_RADIUS,
                    2 * VERTEX_RADIUS);

            // Draw vertex label
            g2d.setColor(Color.BLACK);
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(vertex);
            int textHeight = fm.getHeight();

            g2d.drawString(vertex,
                    position.x - textWidth / 2,
                    position.y + textHeight / 4);
        }

        /**
         * Draws the edges between vertices.
         */
        private void drawEdges(Graphics2D g2d) {
            Map<String, List<String>> adjacencyList = getAdjacencyList();

            for (String from : adjacencyList.keySet()) {
                Point fromPos = vertexPositions.get(from);
                if (fromPos == null) continue;

                for (String to : adjacencyList.get(from)) {
                    Point toPos = vertexPositions.get(to);
                    if (toPos == null) continue;

                    // Draw edge with gradient color
                    drawEdgeWithGradient(g2d, from, to, fromPos, toPos);
                }
            }
        }

        /**
         * Draws an edge with a gradient color based on the vertex sets.
         */
        private void drawEdgeWithGradient(Graphics2D g2d, String from, String to, Point fromPos, Point toPos) {
            // Determine colors based on which sets the vertices belong to
            Color fromColor = setU.contains(from) ? SET_U_COLOR.darker() : SET_V_COLOR.darker();
            Color toColor = setU.contains(to) ? SET_U_COLOR.darker() : SET_V_COLOR.darker();

            // Calculate points where the line meets the vertex circles
            double dx = toPos.x - fromPos.x;
            double dy = toPos.y - fromPos.y;
            double length = Math.sqrt(dx * dx + dy * dy);

            // Calculate the points where the line intersects the circles
            double ratio1 = VERTEX_RADIUS / length;
            double ratio2 = (length - VERTEX_RADIUS) / length;

            int x1 = (int) (fromPos.x + dx * ratio1);
            int y1 = (int) (fromPos.y + dy * ratio1);
            int x2 = (int) (fromPos.x + dx * ratio2);
            int y2 = (int) (fromPos.y + dy * ratio2);

            // Draw the edge with a gradient
            g2d.setStroke(new BasicStroke(1.5f));
            GradientPaint gradient = new GradientPaint(x1, y1, fromColor, x2, y2, toColor);
            g2d.setPaint(gradient);
            g2d.drawLine(x1, y1, x2, y2);
        }

        /**
         * Returns the vertex at the specified point, or null if none is found.
         */
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

    /**
     * Helper method to access the adjacency list from the Graph.Graph class.
     */
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

    /**
     * Example main method to demonstrate the bipartite graph visualizer.
     */
    public static void main(String[] args) {
        // Create a simple bipartite graph
        Graph<String> graph = new Graph<String>(false); // undirected

        // Create two sets of vertices
        Set<String> setU = new HashSet<>(Arrays.asList("U1", "U2", "U3", "U4"));
        Set<String> setV = new HashSet<>(Arrays.asList("V1", "V2", "V3", "V4"));

        // Add all vertices to the graph
        for (String u : setU) graph.addVertex(u);
        for (String v : setV) graph.addVertex(v);

        // Add some edges between U and V
        graph.addEdge("U1", "V1");
        graph.addEdge("U1", "V2");
        graph.addEdge("U2", "V1");
        graph.addEdge("U2", "V3");
        graph.addEdge("U3", "V2");
        graph.addEdge("U3", "V4");
        graph.addEdge("U4", "V3");
        graph.addEdge("U4", "V4");

        // Create visualization
        SwingUtilities.invokeLater(() -> visualizeBipartiteGraph(graph, setU, setV));
    }
}