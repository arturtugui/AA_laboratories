package lab_4.Dijkstra;

import lab_4.WeightedGraph.WeightedEdge;
import lab_4.WeightedGraph.WeightedGraph;

import java.util.*;

/**
 * Implementation of Dijkstra's algorithm for finding shortest paths
 * in a weighted graph with non-negative edge weights.
 */

public class DijkstraAlgorithm<V> {
    private final WeightedGraph<V> graph;

    /**
     * Constructor for the Dijkstra's algorithm implementation.
     *
     * @param graph The weighted graph to run the algorithm on
     */
    public DijkstraAlgorithm(WeightedGraph<V> graph) {
        this.graph = graph;
    }

    /**
     * Find the shortest paths from the source vertex to all other vertices.
     *
     * @param source The source vertex
     * @return A map containing the shortest distance to each vertex
     */
    //DijkstraImplementation
    public Map<V, Double> findShortestPaths(V source) {
        // Validation check
        if (!graph.getVertices().contains(source)) {
            throw new IllegalArgumentException("Source vertex not found in graph");
        }

        // Map to store the shortest distance to each vertex
        Map<V, Double> distances = new HashMap<>();

        // Map to store the previous vertex in the optimal path
        Map<V, V> previousVertices = new HashMap<>();

        // Priority queue to get the vertex with minimum distance
        // Using a custom comparator to compare vertices based on their distances
        PriorityQueue<V> queue = new PriorityQueue<>(
                Comparator.comparingDouble(v -> distances.getOrDefault(v, Double.POSITIVE_INFINITY))
        );

        // Set of vertices whose shortest distance is already finalized
        Set<V> settled = new HashSet<>();

        // Initialize distances
        for (V vertex : graph.getVertices()) {
            // Set initial distance as infinity for all vertices except source
            distances.put(vertex, vertex.equals(source) ? 0.0 : Double.POSITIVE_INFINITY);
        }

        // Add source to the priority queue
        queue.add(source);

        // Process vertices while queue is not empty
        while (!queue.isEmpty()) {
            // Get vertex with minimum distance
            V current = queue.poll();

            // If vertex is already processed, skip
            if (settled.contains(current)) {
                continue;
            }

            // Mark current vertex as processed
            settled.add(current);

            // Process all adjacent vertices
            for (WeightedEdge<V> edge : graph.getNeighbors(current)) {
                V neighbor = edge.target;

                // Skip if neighbor is already processed
                if (settled.contains(neighbor)) {
                    continue;
                }

                // Calculate new distance
                double newDistance = distances.get(current) + edge.weight;

                // If new distance is smaller, update the distance
                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    previousVertices.put(neighbor, current);

                    // Add neighbor to queue for processing
                    // Note: The same vertex might be added multiple times,
                    // but the priority queue will process the instance with smaller distance first
                    queue.add(neighbor);
                }
            }
        }

        return distances;
    }

    /**
     * Find the shortest path from source to destination.
     *
     * @param source The source vertex
     * @param destination The destination vertex
     * @return A list representing the shortest path from source to destination
     */
    public List<V> findShortestPath(V source, V destination) {
        // Validation check
        if (!graph.getVertices().contains(source) || !graph.getVertices().contains(destination)) {
            throw new IllegalArgumentException("Source or destination vertex not found in graph");
        }

        // Map to store the shortest distance to each vertex
        Map<V, Double> distances = new HashMap<>();

        // Map to store the previous vertex in the optimal path
        Map<V, V> previousVertices = new HashMap<>();

        // Priority queue to get the vertex with minimum distance
        PriorityQueue<V> queue = new PriorityQueue<>(
                Comparator.comparingDouble(v -> distances.getOrDefault(v, Double.POSITIVE_INFINITY))
        );

        // Set of vertices whose shortest distance is already finalized
        Set<V> settled = new HashSet<>();

        // Initialize distances
        for (V vertex : graph.getVertices()) {
            // Set initial distance as infinity for all vertices except source
            distances.put(vertex, vertex.equals(source) ? 0.0 : Double.POSITIVE_INFINITY);
        }

        // Add source to the priority queue
        queue.add(source);

        // Process vertices while queue is not empty and destination is not reached
        boolean destinationFound = false;
        while (!queue.isEmpty() && !destinationFound) {
            // Get vertex with minimum distance
            V current = queue.poll();

            // If current vertex is the destination, we're done
            if (current.equals(destination)) {
                destinationFound = true;
                break;
            }

            // If vertex is already processed, skip
            if (settled.contains(current)) {
                continue;
            }

            // Mark current vertex as processed
            settled.add(current);

            // Process all adjacent vertices
            for (WeightedEdge<V> edge : graph.getNeighbors(current)) {
                V neighbor = edge.target;

                // Skip if neighbor is already processed
                if (settled.contains(neighbor)) {
                    continue;
                }

                // Calculate new distance
                double newDistance = distances.get(current) + edge.weight;

                // If new distance is smaller, update the distance
                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    previousVertices.put(neighbor, current);

                    // Add neighbor to queue for processing
                    queue.add(neighbor);
                }
            }
        }

        // Reconstruct the path from source to destination
        List<V> path = new ArrayList<>();
        if (distances.get(destination).equals(Double.POSITIVE_INFINITY)) {
            // No path exists
            return path;
        }

        // Start from destination and work backwards
        V current = destination;
        while (current != null) {
            path.add(current);
            current = previousVertices.get(current);
        }

        // Reverse the path to get from source to destination
        Collections.reverse(path);

        return path;
    }

    /**
     * Print the shortest paths from source to all other vertices.
     *
     * @param source The source vertex
     */
    public void printShortestPaths(V source) {
        Map<V, Double> distances = findShortestPaths(source);

        System.out.println("Shortest paths from " + source + ":");
        for (V vertex : graph.getVertices()) {
            double distance = distances.get(vertex);
            if (distance == Double.POSITIVE_INFINITY) {
                System.out.println("  to " + vertex + ": No path exists");
            } else {
                System.out.println("  to " + vertex + ": " + distance);
            }
        }
    }

    /**
     * Print the shortest path from source to destination.
     *
     * @param source The source vertex
     * @param destination The destination vertex
     */
    public void printShortestPath(V source, V destination) {
        List<V> path = findShortestPath(source, destination);
        Map<V, Double> distances = findShortestPaths(source);

        if (path.isEmpty()) {
            System.out.println("No path exists from " + source + " to " + destination);
            return;
        }

        System.out.println("Shortest path from " + source + " to " + destination + ":");
        System.out.println("  Path: " + path);
        System.out.println("  Distance: " + distances.get(destination));
    }

    /**
     * Main method with a usage example.
     */
    public static void main(String[] args) {
        // Create a sample weighted graph
        WeightedGraph<String> graph = new WeightedGraph<>(true); // Directed graph

        // Add vertices
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addVertex("D");
        graph.addVertex("E");

        // Add edges with weights
        graph.addEdge("A", "B", 4);
        graph.addEdge("A", "C", 2);
        graph.addEdge("B", "C", 3);
        graph.addEdge("B", "E", 3);
        graph.addEdge("B", "D", 2);

        graph.addEdge("C", "B", 1);
        graph.addEdge("C", "D", 4);
        graph.addEdge("C", "E", 5);
        graph.addEdge("E", "D", 1);

        // Run Dijkstra's algorithm
        DijkstraAlgorithm<String> dijkstra = new DijkstraAlgorithm<>(graph);

        // Print shortest paths from vertex A
        dijkstra.printShortestPaths("A");
        //Map<V, Double> distances = findShortestPaths(source);

        // Print shortest path from A to E
        dijkstra.printShortestPath("A", "E");
        //Map<V, Double> distances = findShortestPath(source, destination);
    }

}