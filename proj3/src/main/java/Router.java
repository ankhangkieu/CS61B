import java.util.LinkedList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Collections;

/**
 * This class provides a shortestPath method for finding routes between two points
 * on the map. Start by using Dijkstra's, and if your code isn't fast enough for your
 * satisfaction (or the autograder), upgrade your implementation by switching it to A*.
 * Your code will probably not be fast enough to pass the autograder unless you use A*.
 * The difference between A* and Dijkstra's is only a couple of lines of code, and boils
 * down to the priority you use to order your vertices.
 */
public class Router {
    private static long s;
    private static long e;
    private static GraphDB graph;

    /**
     * Return a LinkedList of <code>Long</code>s representing the shortest path from st to dest,
     * where the longs are node IDs.
     */
    public static LinkedList<Long> shortestPath(
            GraphDB g, double stlon, double stlat, double destlon, double destlat) {
        Router.graph = g;
        s = g.closest(stlon, stlat);
        e = g.closest(destlon, destlat);
        if (s == e) {
            return new LinkedList<Long>(Arrays.asList(s));
        }
        return findPath();
    }

    private static class Node implements Comparable<Node> {
        long id;
        double distFromStart;
        Double priority;

        Node(long id, double distFromStart) {
            this.id = id;
            this.distFromStart = distFromStart;
            this.priority = distFromStart + estimateToEnd();
        }

        double estimateToEnd() {
            return graph.distance(id, e);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Node) {
                return id == ((Node) obj).id && distFromStart == ((Node) obj).distFromStart
                        && priority.equals(((Node) obj).priority);
            }
            return false;
        }

        @Override
        public int hashCode() {
            long temp = id;
            int hash = 0;
            while (temp > 0) {
                hash += hash * 13 + temp % 10;
                temp /= 10;
            }
            return hash;
        }

        @Override
        public int compareTo(Node obj) {
            return this.priority.compareTo(obj.priority);
        }
    }

    private static LinkedList<Long> findPath() {
        LinkedList<Long> result = new LinkedList<>();
        HashSet<Long> visited = new HashSet<>();
        PriorityQueue<Node> queue = new PriorityQueue<>();
        HashMap<Long, Long> comeFrom = new HashMap<>();
        HashMap<Long, Node> oldNode = new HashMap<>();
        Node start = new Node(s, 0);
        oldNode.put(s, start);
        comeFrom.put(s, null);
        queue.add(start);
        result.add(s);
        visited.add(s);
        while (!queue.isEmpty()) {
            Node cur = queue.poll();
            if (cur.id == e) {
                return buildPath(comeFrom);
            }
            visited.add(cur.id);
            for (long neighbor : graph.adjacent(cur.id)) {
                double newDist = cur.distFromStart + graph.distance(cur.id, neighbor);
                if (visited.contains(neighbor)) {
                    continue;
                }
                if (oldNode.containsKey(neighbor)) {
                    if (oldNode.get(neighbor).distFromStart <= newDist) {
                        continue;
                    }
                    queue.remove(oldNode.get(neighbor));
                }
                Node neighborNode = new Node(neighbor, newDist);
                oldNode.put(neighbor, neighborNode);
                comeFrom.put(neighbor, cur.id);
                queue.add(neighborNode);
            }
        }
        return null;
    }

    private static LinkedList<Long> buildPath(HashMap<Long, Long> comeFrom) {
        Long from = e;
        LinkedList<Long> path = new LinkedList<>();
        while (from != null) {
            path.add(from);
            from = comeFrom.get(from);
        }
        Collections.reverse(path);
        return path;
    }
}
