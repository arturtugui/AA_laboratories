package DFS;

import Graph.Graph;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class DFSVisualizer extends JFrame {
    private Graph<String> graph;
    private Map<String, Point> vertexPositions;
    private static final int VERTEX_RADIUS = 20;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    // DFS visualization state
    private Set<String> visited;
    private Stack<String> stack;
    private String currentNode;
    private List<String> dfsPath;
    private Map<String, Color> nodeColors;
    private boolean animationRunning = false;
    private Timer animationTimer;
    private int delayMs = 1000; // Delay between steps in milliseconds

    // Color constants
    private static final Color UNVISITED_COLOR = Color.LIGHT_GRAY;
    private static final Color VISITED_COLOR = Color.GREEN;
    private static final Color CURRENT_COLOR = Color.RED;
    private static final Color STACK_COLOR = Color.YELLOW;
    private static final Color EDGE_COLOR = Color.BLACK;
    private static final Color TRAVERSED_EDGE_COLOR = Color.BLUE;

    private boolean isPaused = false;
    private JButton pauseButton;

    // Track traversed edges
    private Set<Edge> traversedEdges;

    public DFSVisualizer(Graph<String> graph) {
        this.graph = graph;
        this.vertexPositions = new HashMap<>();
        this.visited = new HashSet<>();
        this.stack = new Stack<>();
        this.dfsPath = new ArrayList<>();
        this.nodeColors = new HashMap<>();
        this.traversedEdges = new HashSet<>();

        // Initialize the frame
        setTitle("DFS Algorithm Visualization");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Auto-generate positions for vertices in a circle layout
        generateVertexPositions();

        // Initialize all nodes with unvisited color
        for (String vertex : graph.getVertices()) {
            nodeColors.put(vertex, UNVISITED_COLOR);
        }

        // Create control panel
        JPanel controlPanel = createControlPanel();

        // Add graph panel
        GraphPanel graphPanel = new GraphPanel();

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
        JButton startButton = new JButton("Start DFS");
        startButton.addActionListener(e -> {
            if (!animationRunning) {
                String startNode = (String) nodeSelector.getSelectedItem();
                if (startNode != null) {
                    resetVisualization();
                    startDFSVisualization(startNode);
                    startButton.setText("Reset");
                } else {
                    startButton.setText("Start DFS");
                }
            } else {
                stopAnimation();
                resetVisualization();
                startButton.setText("Start DFS");
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
        stack.clear();
        dfsPath.clear();
        traversedEdges.clear();
        currentNode = null;

        // Reset colors
        for (String vertex : graph.getVertices()) {
            nodeColors.put(vertex, UNVISITED_COLOR);
        }

        isPaused = false;
        pauseButton.setEnabled(false);
        pauseButton.setText("Pause");

        repaint();
    }

    public void startDFSVisualization(String startNode) {
        // Initialize DFS
        stack.push(startNode);
        nodeColors.put(startNode, STACK_COLOR);
        animationRunning = true;

        animationTimer = new Timer(delayMs, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performDFSStep();
                repaint();

                // Stop if DFS is complete
                if (stack.isEmpty() && currentNode == null) {
                    stopAnimation();
                }
            }
        });

        animationTimer.start();

        pauseButton.setEnabled(true);
        pauseButton.setText("Pause");
    }

    private void performDFSStep() {
        // If we just finished processing a node
        if (currentNode != null) {
            nodeColors.put(currentNode, VISITED_COLOR);
            currentNode = null;
        }

        // Get next node from stack if available
        if (!stack.isEmpty()) {
            currentNode = stack.pop();
            dfsPath.add(currentNode);

            // Only process if not already visited
            if (!visited.contains(currentNode)) {
                visited.add(currentNode);
                nodeColors.put(currentNode, CURRENT_COLOR);

                // Get neighbors and add to stack in reverse order
                List<String> neighbors = graph.getAdjacencyList().getOrDefault(currentNode, new ArrayList<>());
                for (int i = neighbors.size() - 1; i >= 0; i--) {
                    String neighbor = neighbors.get(i);
                    if (!visited.contains(neighbor) && !stack.contains(neighbor)) {
                        stack.push(neighbor);
                        traversedEdges.add(new Edge(currentNode, neighbor));
                        nodeColors.put(neighbor, STACK_COLOR);
                    }
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

    private void generateVertexPositions() {
        Set<String> vertices = graph.getVertices();
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
        private JTextArea infoTextArea;

        public GraphPanel() {
            setBackground(Color.WHITE);
            setLayout(new BorderLayout());

            // Create text area for showing DFS info
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

            // Draw edges first (so they appear behind vertices)
            drawEdges(g2d);

            // Draw vertices
            drawVertices(g2d);

            // Update info text
            updateInfoText();
        }

        private void updateInfoText() {
            StringBuilder info = new StringBuilder();
            info.append("DFS Path: ");
            for (String node : dfsPath) {
                info.append(node).append(" ");
            }
            info.append("\n");

            info.append("Current Stack: ");
            Stack<String> tempStack = new Stack<>();
            tempStack.addAll(stack);
            while (!tempStack.isEmpty()) {
                info.append(tempStack.pop()).append(" ");
            }
            info.append("\n");

            info.append("Visited Nodes: ");
            for (String node : visited) {
                info.append(node).append(" ");
            }

            infoTextArea.setText(info.toString());
        }

        private void drawVertices(Graphics2D g2d) {
            for (String vertex : graph.getVertices()) {
                Point position = vertexPositions.get(vertex);
                if (position == null) continue;

                // Get vertex color based on state
                Color color = nodeColors.getOrDefault(vertex, UNVISITED_COLOR);

                // Draw vertex circle
                g2d.setColor(color);
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
                if (fromPos == null) continue;

                for (String to : adjacencyList.get(from)) {
                    Point toPos = vertexPositions.get(to);
                    if (toPos == null) continue;

                    // Check if this edge has been traversed
                    boolean isTraversed = traversedEdges.contains(new Edge(from, to));
                    g2d.setColor(isTraversed ? TRAVERSED_EDGE_COLOR : EDGE_COLOR);
                    g2d.setStroke(new BasicStroke(isTraversed ? 2.5f : 1.5f));

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

    // Helper method to access the adjacency list from the Graph class
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

    public static void main(String[] args) {
        // Example usage
        Graph<String> graph = new Graph<String>(false); // false for undirected graph

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

        // Create visualization
        //SwingUtilities.invokeLater(() -> new DFSVisualizer(graph));
        SwingUtilities.invokeLater(() -> DFSVisualizer.visualizeDFS(graph, "A"));
    }

    /**
     * Static method to visualize DFS on any graph
     * @param graph The graph to visualize
     * @param startNode The node to start DFS from
     */
    public static void visualizeDFS(Graph<String> graph, String startNode) {
        SwingUtilities.invokeLater(() -> {
            DFSVisualizer visualizer = new DFSVisualizer(graph);
            // Start DFS after a short delay to ensure the GUI is ready
            Timer timer = new Timer(500, e -> visualizer.startDFSVisualization(startNode));
            timer.setRepeats(false);
            timer.start();
        });
    }
}