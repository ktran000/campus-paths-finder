package graphs.shortestpaths;

import graphs.Edge;
import graphs.Graph;

import java.util.*;

/**
 * Topological sorting implementation of the {@link ShortestPathSolver} interface for <b>directed acyclic graphs</b>.
 *
 * @param <V> the type of vertices.
 * @see ShortestPathSolver
 */
public class ToposortDAGSolver<V> implements ShortestPathSolver<V> {
    private final Map<V, Edge<V>> edgeTo;
    private final Map<V, Double> distTo;

    /**
     * Constructs a new instance by executing the toposort-DAG-shortest-paths algorithm on the graph from the start.
     *
     * @param graph the input graph.
     * @param start the start vertex.
     */
    public ToposortDAGSolver(Graph<V> graph, V start) {
        edgeTo = new HashMap<>();
        distTo = new HashMap<>();

        // Initialize the start vertex (Start vertex has no previous edge in
        // shortest path)
        edgeTo.put(start, null);
        // Distance to start vertex is 0
        distTo.put(start, 0.0);

        // Get vertices in topological order
        // List to store vertices in post-order (reverse topological order)
        List<V> postOrder = new ArrayList<>();
        // Set to keep track of visited vertices during DFS
        Set<V> visited = new HashSet<>();
        // Perform DFS to get vertices in topological order
        dfsPostOrder(graph, start, visited, postOrder);
        Collections.reverse(postOrder);

        // Process vertices in topological order
        for (V from : postOrder) {
            // Only process vertices that are reachable from start
            if (distTo.containsKey(from)) {  // Only process reachable vertices
                for (Edge<V> e : graph.neighbors(from)) {
                    V to = e.to;
                    // Get the current known distance to 'to' vertex
                    double oldDist = distTo.getOrDefault(to, Double.POSITIVE_INFINITY);
                    // Calculate new distance to 'to' vertex through current path
                    double newDist = distTo.get(from) + e.weight;
                    // If new distance is shorter, update path information
                    if (newDist < oldDist) {
                        // Record edge leading to 'to'
                        edgeTo.put(to, e);
                        // Update the shortest distance to 'to'
                        distTo.put(to, newDist);
                    }
                }
            }
        }
    }

    /**
     * Recursively adds nodes from the graph to the result in DFS postorder from the start vertex.
     *
     * @param graph   the input graph.
     * @param start   the start vertex.
     * @param visited the set of visited vertices.
     * @param result  the destination for adding nodes.
     */
    private void dfsPostOrder(Graph<V> graph, V start, Set<V> visited, List<V> result) {
        visited.add(start);
        for (Edge<V> edge : graph.neighbors(start)) {
            if (!visited.contains(edge.to)) {
                dfsPostOrder(graph, edge.to, visited, result);
            }
        }
        result.add(start);
    }

    @Override
    public List<V> solution(V goal) {
        List<V> path = new ArrayList<>();
        V curr = goal;
        path.add(curr);
        while (edgeTo.get(curr) != null) {
            curr = edgeTo.get(curr).from;
            path.add(curr);
        }
        Collections.reverse(path);
        return path;
    }
}
