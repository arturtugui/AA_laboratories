package lab_4.FloydWarshall;

import lab_4.WeightedGraph.WeightedEdge;
import lab_4.WeightedGraph.WeightedGraph;

import java.util.*;

public class FloydWarshall {

    /**
     * Runs the Floyd-Warshall algorithm on a given weighted graph
     * @param graph The weighted graph to find all-pairs shortest paths for
     * @return A Map containing the distance matrix and the next matrix
     */
    //FloydWarshallImplementation
    public static Map<String, double[][]> findAllPairsShortestPaths(WeightedGraph<String> graph) {
        List<String> vertices = new ArrayList<>(graph.getVertices());
        int n = vertices.size();

        Map<String, Integer> vertexToIndex = new HashMap<>();
        for (int i = 0; i < n; i++) {
            vertexToIndex.put(vertices.get(i), i);
        }

        double[][] dist = new double[n][n];
        double[][] next = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                dist[i][j] = Double.POSITIVE_INFINITY;
                next[i][j] = -1;
            }
            dist[i][i] = 0;
        }

        for (String u : graph.getVertices()) {
            int uIdx = vertexToIndex.get(u);
            for (WeightedEdge<String> edge : graph.getNeighbors(u)) {
                int vIdx = vertexToIndex.get(edge.target);
                dist[uIdx][vIdx] = edge.weight;
                next[uIdx][vIdx] = vIdx;
            }
        }

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                        next[i][j] = next[i][k];
                    }
                }
            }
        }

        Map<String, double[][]> result = new HashMap<>();
        result.put("distance", dist);
        result.put("next", next);

        return result;
    }

    /**
     * Reconstructs the shortest path between two vertices based on the next matrix
     * @param start The starting vertex
     * @param end The ending vertex
     * @param vertices The list of all vertices
     * @param next The next matrix from Floyd-Warshall algorithm
     * @return The list of vertices in the shortest path
     */
    public static List<String> reconstructPath(String start, String end, List<String> vertices, double[][] next) {
        Map<String, Integer> vertexToIndex = new HashMap<>();
        for (int i = 0; i < vertices.size(); i++) {
            vertexToIndex.put(vertices.get(i), i);
        }

        int startIdx = vertexToIndex.get(start);
        int endIdx = vertexToIndex.get(end);

        List<String> path = new ArrayList<>();

        // If there's no path between start and end
        if (next[startIdx][endIdx] == -1) {
            return path;
        }

        // Build the path
        path.add(start);
        while (startIdx != endIdx) {
            startIdx = (int) next[startIdx][endIdx];
            path.add(vertices.get(startIdx));
        }

        return path;
    }

    /**
     * Prints all shortest paths and their distances
     * @param graph The weighted graph
     * @param result The result from Floyd-Warshall algorithm
     */
    public static void printAllShortestPaths(WeightedGraph<String> graph, Map<String, double[][]> result) {
        List<String> vertices = new ArrayList<>(graph.getVertices());
        double[][] dist = result.get("distance");
        double[][] next = result.get("next");

        System.out.println("All-Pairs Shortest Paths:");
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = 0; j < vertices.size(); j++) {
                if (i != j) {
                    String start = vertices.get(i);
                    String end = vertices.get(j);

                    if (dist[i][j] == Double.POSITIVE_INFINITY) {
                        System.out.println("No path from " + start + " to " + end);
                    } else {
                        List<String> shortestPath = reconstructPath(start, end, vertices, next);
                        System.out.println("Path from " + start + " to " + end + ": " +
                                shortestPath + ", Distance: " + dist[i][j]);
                    }
                }
            }
        }
    }

    /**
     * Detect negative cycles in the graph using Floyd-Warshall
     * @param graph The weighted graph to check for negative cycles
     * @return true if a negative cycle exists, false otherwise
     */
    public static boolean hasNegativeCycle(WeightedGraph<String> graph) {
        List<String> vertices = new ArrayList<>(graph.getVertices());
        int n = vertices.size();

        // Create a vertex-to-index mapping
        Map<String, Integer> vertexToIndex = new HashMap<>();
        for (int i = 0; i < n; i++) {
            vertexToIndex.put(vertices.get(i), i);
        }

        // Initialize distance matrix
        double[][] dist = new double[n][n];

        // Initialize with infinity for all pairs
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                dist[i][j] = Double.POSITIVE_INFINITY;
            }
            dist[i][i] = 0; // Distance from a vertex to itself is 0
        }

        // Fill in the direct edge weights
        for (String u : graph.getVertices()) {
            int uIdx = vertexToIndex.get(u);
            for (WeightedEdge<String> edge : graph.getNeighbors(u)) {
                int vIdx = vertexToIndex.get(edge.target);
                dist[uIdx][vIdx] = edge.weight;
            }
        }

        // Floyd-Warshall algorithm
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][k] != Double.POSITIVE_INFINITY &&
                            dist[k][j] != Double.POSITIVE_INFINITY &&
                            dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }

        // Check for negative cycles - if any vertex has a negative distance to itself
        for (int i = 0; i < n; i++) {
            if (dist[i][i] < 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get the shortest distance between two vertices
     * @param graph The weighted graph
     * @param from The source vertex
     * @param to The destination vertex
     * @return The shortest distance, or Double.POSITIVE_INFINITY if no path exists
     */
    public static double getShortestDistance(WeightedGraph<String> graph, String from, String to) {
        Map<String, double[][]> result = findAllPairsShortestPaths(graph);
        double[][] dist = result.get("distance");

        List<String> vertices = new ArrayList<>(graph.getVertices());
        Map<String, Integer> vertexToIndex = new HashMap<>();
        for (int i = 0; i < vertices.size(); i++) {
            vertexToIndex.put(vertices.get(i), i);
        }

        int fromIdx = vertexToIndex.get(from);
        int toIdx = vertexToIndex.get(to);

        return dist[fromIdx][toIdx];
    }

    public static void main(String[] args) {
        // Create and populate a sample graph
        WeightedGraph<String> graph = new WeightedGraph<>(true); // Directed graph

        graph.addVertex("1");
        graph.addVertex("2");
        graph.addVertex("3");
        graph.addVertex("4");

        graph.addEdge("1", "3", -2);
        graph.addEdge("2", "1", 4);
        graph.addEdge("2", "3", 3);
        graph.addEdge("3", "4", 2);
        graph.addEdge("4", "2", -1);

        System.out.println("Graph Structure:");
        graph.printGraph();
        System.out.println();

        // Run Floyd-Warshall algorithm
        Map<String, double[][]> result = findAllPairsShortestPaths(graph);

        // Print the results
        printAllShortestPaths(graph, result);

        // Example of getting specific shortest distance
        System.out.println("\nShortest distance from 1 to 4: " +
                getShortestDistance(graph, "1", "4"));
    }
}