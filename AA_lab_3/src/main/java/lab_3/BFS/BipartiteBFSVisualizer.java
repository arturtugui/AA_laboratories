package lab_3.BFS;

import lab_3.Bipartite.BipartiteGraphGenerator;
import lab_3.Graph.Graph;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import static lab_3.BFS.BreadthFirstSearch.bfsWithOutput;
import static lab_3.Bipartite.BipartiteGraphGenerator.getBipartitePartitions;

/**
 * A specialized visualizer for BFS traversal on bipartite graphs that renders the two vertex sets
 * in different positions and colors to make the bipartite structure clear.
 */
public class BipartiteBFSVisualizer extends JFrame {
    private Graph<String> graph;
    private Map<String, Point> vertexPositions;
    private Set<String> setU;
    private Set<String> setV;
    private static final int VERTEX_RADIUS = 20;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    // Bipartite sets colors
    private static final Color SET_U_COLOR = new Color(173, 216, 230); // Light blue
    private static final Color SET_V_COLOR = new Color(255, 182, 193); // Light pink

    // BFS visualization state
    private Set<String> visited;
    private Queue<String> queue;
    private String currentNode;
    private List<String> bfsPath;
    private Map<String, Color> nodeColors;
    private boolean animationRunning = false;
    private Timer animationTimer;
    private int delayMs = 1000; // Delay between steps in milliseconds
    private int maxQueueSize = 0; // Track maximum queue size

    // Color constants for BFS visualization
    private static final Color UNVISITED_COLOR_U = new Color(173, 216, 230, 128); // Transparent light blue
    private static final Color UNVISITED_COLOR_V = new Color(255, 182, 193, 128); // Transparent light pink
    private static final Color VISITED_COLOR = new Color(50, 205, 50, 200); // Green
    private static final Color CURRENT_COLOR = new Color(255, 69, 0, 220); // Red-orange
    private static final Color QUEUE_COLOR = new Color(255, 215, 0, 200); // Gold
    private static final Color EDGE_COLOR = Color.GRAY;
    private static final Color TRAVERSED_EDGE_COLOR = new Color(0, 0, 255, 220); // Blue

    private boolean isPaused = false;
    private JButton pauseButton;

    // Track traversed edges
    private Set<Edge> traversedEdges;

    /**
     * Creates a new bipartite BFS visualizer with the specified graph and partitions.
     *
     * @param graph The bipartite graph to visualize
     * @param setU The first partition (set U)
     * @param setV The second partition (set V)
     */
    public BipartiteBFSVisualizer(Graph<String> graph, Set<String> setU, Set<String> setV) {
        this.graph = graph;
        this.setU = setU;
        this.setV = setV;
        this.vertexPositions = new HashMap<>();
        this.visited = new HashSet<>();
        this.queue = new LinkedList<>();
        this.bfsPath = new ArrayList<>();
        this.nodeColors = new HashMap<>();
        this.traversedEdges = new HashSet<>();

        // Initialize the frame
        setTitle("Bipartite BFS Algorithm Visualization");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Generate positions for vertices in bipartite layout
        generateBipartiteLayout();

        // Initialize all nodes with unvisited color based on their set
        for (String vertex : graph.getVertices()) {
            if (setU.contains(vertex)) {
                nodeColors.put(vertex, UNVISITED_COLOR_U);
            } else if (setV.contains(vertex)) {
                nodeColors.put(vertex, UNVISITED_COLOR_V);
            }
        }

        // Create control panel
        JPanel controlPanel = createControlPanel();

        // Add graph panel
        BipartiteGraphPanel graphPanel = new BipartiteGraphPanel();

        // Set up the layout
        setLayout(new BorderLayout());
        add(graphPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        // Show the frame
        setVisible(true);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel();

        // Create node selection
        JComboBox<String> nodeSelector = new JComboBox<>();
        for (String vertex : graph.getVertices()) {
            nodeSelector.addItem(vertex);
        }

        // Speed slider
        JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, 100, 2000, delayMs);
        speedSlider.setInverted(true); // Invert so right means faster
        speedSlider.addChangeListener(e -> {
            delayMs = speedSlider.getValue();
            if (animationTimer != null) {
                animationTimer.setDelay(delayMs);
            }
        });

        // Start button
        JButton startButton = new JButton("Start BFS");
        startButton.addActionListener(e -> {
            if (!animationRunning) {
                String startNode = (String) nodeSelector.getSelectedItem();
                if (startNode != null) {
                    resetVisualization();
                    startBFSVisualization(startNode);
                    startButton.setText("Reset");
                } else {
                    startButton.setText("Start BFS");
                }
            } else {
                stopAnimation();
                resetVisualization();
                startButton.setText("Start BFS");
            }
        });

        pauseButton = new JButton("Pause");
        pauseButton.setEnabled(false); // Disabled until animation starts

        pauseButton.addActionListener(e -> {
            if (isPaused) {
                // Resume animation
                animationTimer.start();
                isPaused = false;
                pauseButton.setText("Pause");
            } else {
                // Pause animation
                animationTimer.stop();
                isPaused = true;
                pauseButton.setText("Resume");
            }
        });

        // Add components to panel
        panel.add(new JLabel("Start Node:"));
        panel.add(nodeSelector);
        panel.add(new JLabel("Speed:"));
        panel.add(speedSlider);
        panel.add(startButton);
        panel.add(pauseButton);

        return panel;
    }

    private void resetVisualization() {
        // Reset all visualization state
        if (animationTimer != null) {
            animationTimer.stop();
        }
        animationRunning = false;
        visited.clear();
        queue.clear();
        bfsPath.clear();
        traversedEdges.clear();
        currentNode = null;
        maxQueueSize = 0;

        // Reset colors based on the bipartite sets
        for (String vertex : graph.getVertices()) {
            if (setU.contains(vertex)) {
                nodeColors.put(vertex, UNVISITED_COLOR_U);
            } else if (setV.contains(vertex)) {
                nodeColors.put(vertex, UNVISITED_COLOR_V);
            }
        }

        isPaused = false;
        pauseButton.setEnabled(false);
        pauseButton.setText("Pause");

        repaint();
    }

    public void startBFSVisualization(String startNode) {
        // Initialize BFS
        visited.add(startNode);
        queue.add(startNode);
        maxQueueSize = 1;
        nodeColors.put(startNode, QUEUE_COLOR);
        animationRunning = true;

        animationTimer = new Timer(delayMs, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performBFSStep();
                repaint();

                // Stop if BFS is complete
                if (queue.isEmpty() && currentNode == null) {
                    stopAnimation();
                }
            }
        });

        animationTimer.start();

        pauseButton.setEnabled(true);
        pauseButton.setText("Pause");
    }

    private void performBFSStep() {
        // If we just finished processing a node
        if (currentNode != null) {
            nodeColors.put(currentNode, VISITED_COLOR);
            currentNode = null;
        }

        // Get next node from queue if available
        if (!queue.isEmpty()) {
            currentNode = queue.poll();
            bfsPath.add(currentNode);
            nodeColors.put(currentNode, CURRENT_COLOR);

            // Get neighbors and add to queue
            List<String> neighbors = graph.getAdjacencyList().getOrDefault(currentNode, new ArrayList<>());
            for (String neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                    traversedEdges.add(new Edge(currentNode, neighbor));
                    nodeColors.put(neighbor, QUEUE_COLOR);

                    // Update max queue size
                    maxQueueSize = Math.max(maxQueueSize, queue.size());
                }
            }
        }
    }

    private void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
        animationRunning = false;

        pauseButton.setEnabled(false);
        pauseButton.setText("Pause"); // Reset label
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
     * Panel for rendering the bipartite graph with BFS visualization.
     */
    class BipartiteGraphPanel extends JPanel {
        private JTextArea infoTextArea;

        public BipartiteGraphPanel() {
            setBackground(Color.WHITE);
            setLayout(new BorderLayout());

            // Create text area for showing BFS info
            infoTextArea = new JTextArea(5, 20);
            infoTextArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(infoTextArea);

            // Add to the panel
            add(scrollPane, BorderLayout.NORTH);

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

            // Update info text
            updateInfoText();
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

        private void updateInfoText() {
            StringBuilder info = new StringBuilder();
            info.append("BFS Path: ");
            for (String node : bfsPath) {
                info.append(node).append(" ");
            }
            info.append("\n");

            info.append("Current Queue: ");
            for (String node : queue) {
                info.append(node).append(" ");
            }
            info.append("\n");

            info.append("Visited Nodes: ");
            for (String node : visited) {
                info.append(node).append(" ");
            }
            info.append("\n");

            info.append("Maximum Queue Size: ").append(maxQueueSize);

            infoTextArea.setText(info.toString());
        }

        private void drawVertices(Graphics2D g2d) {
            // Draw all vertices - color will be determined by BFS state
            for (String vertex : graph.getVertices()) {
                Point position = vertexPositions.get(vertex);
                if (position == null) continue;

                // Get the vertex color based on BFS state
                Color color = nodeColors.getOrDefault(vertex,
                        setU.contains(vertex) ? UNVISITED_COLOR_U : UNVISITED_COLOR_V);

                // Draw vertex circle
                g2d.setColor(color);
                g2d.fillOval(position.x - VERTEX_RADIUS,
                        position.y - VERTEX_RADIUS,
                        2 * VERTEX_RADIUS,
                        2 * VERTEX_RADIUS);

                // Draw vertex border - darker if part of current BFS state
                Color borderColor;
                if (color == VISITED_COLOR || color == CURRENT_COLOR || color == QUEUE_COLOR) {
                    borderColor = color.darker();
                } else {
                    borderColor = setU.contains(vertex) ? SET_U_COLOR.darker() : SET_V_COLOR.darker();
                }
                g2d.setColor(borderColor);
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
        }

        private void drawEdges(Graphics2D g2d) {
            Map<String, List<String>> adjacencyList = getAdjacencyList();

            for (String from : adjacencyList.keySet()) {
                Point fromPos = vertexPositions.get(from);
                if (fromPos == null) continue;

                for (String to : adjacencyList.get(from)) {
                    Point toPos = vertexPositions.get(to);
                    if (toPos == null) continue;

                    // Check if this edge has been traversed
                    boolean isTraversed = traversedEdges.contains(new Edge(from, to)) ||
                            traversedEdges.contains(new Edge(to, from));

                    // Draw edge with gradient color
                    drawEdge(g2d, from, to, fromPos, toPos, isTraversed);
                }
            }
        }

        /**
         * Draws an edge with appropriate styling based on whether it's been traversed.
         */
        private void drawEdge(Graphics2D g2d, String from, String to, Point fromPos, Point toPos, boolean isTraversed) {
            // Determine edge color
            Color edgeColor = isTraversed ? TRAVERSED_EDGE_COLOR : EDGE_COLOR;

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

            // Draw the edge
            g2d.setColor(edgeColor);
            g2d.setStroke(new BasicStroke(isTraversed ? 2.5f : 1.5f));
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
     * Helper method to access the adjacency list from the Graph class.
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

    // Helper class to represent an edge
    private static class Edge {
        String from;
        String to;

        public Edge(String from, String to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Edge edge = (Edge) obj;
            return from.equals(edge.from) && to.equals(edge.to);
        }

        @Override
        public int hashCode() {
            return Objects.hash(from, to);
        }
    }

    /**
     * Convenience method to visualize BFS on a bipartite graph.
     */
    public static void visualizeBFS(Graph<String> graph, Set<String> setU, Set<String> setV, String startNode) {
        SwingUtilities.invokeLater(() -> {
            BipartiteBFSVisualizer visualizer = new BipartiteBFSVisualizer(graph, setU, setV);
            // Start BFS after a short delay to ensure the GUI is ready
            Timer timer = new Timer(500, e -> visualizer.startBFSVisualization(startNode));
            timer.setRepeats(false);
            timer.start();
        });
    }

    public static void BipartiteVisualizeBFS(Graph<String> graph, String startNode, Set<String>[] partitions) {
        // Create visualization and start BFS from the specified node
        SwingUtilities.invokeLater(() -> {
            BipartiteBFSVisualizer visualizer = new BipartiteBFSVisualizer(graph, partitions[0], partitions[1]);
            Timer timer = new Timer(500, e -> visualizer.startBFSVisualization(startNode));
            timer.setRepeats(false);
            timer.start();
        });
    }

    public static <V> void visualizeBipartiteBFS(Graph<String> graph, V startNode) {
        if (!(startNode instanceof String)) {
            throw new IllegalArgumentException("Start node must be a String for visualization");
        }

        // Cast to String for visualization
        String start = (String) startNode;

        Set<String>[] partitions = getBipartitePartitions(graph);

        BipartiteVisualizeBFS(graph, start, partitions);
    }

    /**
     * Example main method to demonstrate the bipartite BFS visualizer.
     */
    public static void main(String[] args) {
        // Create a simple bipartite graph
        Graph<String> graph = BipartiteGraphGenerator.generateStringLabelBipartiteGraph(9, 12, 3);
        graph.printGraph();

        System.out.println("BFS traversal starting from node U1:");
        int maxQueueSize = bfsWithOutput(graph, "U1");
        System.out.println("Maximum queue size during BFS: " + maxQueueSize);

        visualizeBipartiteBFS(graph, "U1");
    }
}
