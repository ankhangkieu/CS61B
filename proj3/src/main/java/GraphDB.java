import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.Queue;
import java.util.ArrayDeque;

/**
 * Graph for storing all of the intersection (vertex) and road (edge) information.
 * Uses your GraphBuildingHandler to convert the XML files into a graph. Your
 * code must include the vertices, adjacent, distance, closest, lat, and lon
 * methods. You'll also need to include instance variables and methods for
 * modifying the graph (e.g. addNode and addEdge).
 *
 * @author Alan Yao, Josh Hug
 */
public class GraphDB {
    /**
     * Your instance variables for storing the graph. You should consider
     * creating helper classes, e.g. Node, Edge, etc.
     */
    static class Node {
        private String name = "";
        private Long id;
        private Double lat;
        private Double lon;
        private HashMap<Long, Way> connectTo = new HashMap<>();

        void setName(String name) {
            this.name = name;
        }

        Node(long id, Double lat, Double lon) {
            this.id = id;
            this.lat = lat;
            this.lon = lon;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Node) {
                return id == ((Node) obj).id;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }

    static class Way {
        private long v;
        private long w;
        private long id;
        private String name;
        private String maxSpeed;
        Way(long v, long w, long id, String name, String maxSpeed) {
            this.v = v;
            this.w = w;
            this.id = id;
            this.name = name;
            this.maxSpeed = maxSpeed;
        }
    }

    static class PrefixTreeNode {
        Character s;
        boolean isLeaf;
        ArrayList<Node> graphNodes = null;
        HashMap<Character, PrefixTreeNode> branches = new HashMap<>();

        PrefixTreeNode() {
            this(null, false);
        }

        PrefixTreeNode(Character s, boolean isLeaf) {
            this.s = s;
            this.isLeaf = isLeaf;
        }

        PrefixTreeNode addNode(Node graphNode) {
            return addNodeHelper(graphNode, cleanString(graphNode.name), 0);
        }

        private PrefixTreeNode addNodeHelper(Node graphNode, String item, int index) {
            if (item == null || item.isEmpty() || index >= item.length()) {
                return null;
            }
            char c = item.charAt(index);
            if (!branches.containsKey(c)) {
                branches.put(c, new PrefixTreeNode(c, false));
            }
            PrefixTreeNode result = branches.get(c).addNodeHelper(graphNode, item, index + 1);
            if (index == item.length() - 1) {
                if (!branches.get(c).isLeaf) {
                    branches.get(c).setLeaf();
                }
                branches.get(c).addFullName(graphNode);
            }
            return result == null ? branches.get(c) : result;
        }

        private void addFullName(Node graphNode) {
            graphNodes.add(graphNode);
        }

        private void setLeaf() {
            graphNodes = new ArrayList<>();
            isLeaf = true;
        }

        PrefixTreeNode findNode(String item) {
            return findNodeHelper(item, 0);
        }

        private PrefixTreeNode findNodeHelper(String item, int index) {
            if (item == null || item.isEmpty() || index > item.length()) {
                return null;
            }
            if (index == item.length()) {
                return this;
            }
            char c = cleanString(item).charAt(index);
            if (branches.containsKey(c)) {
                return branches.get(c).findNodeHelper(item, index + 1);
            }
            return null;
        }
    }

    private HashMap<Long, Node> vertex = new HashMap<>();
    //private HashMap<String, Node>
    private PrefixTreeNode rootPrefix = new PrefixTreeNode();
    private HashMap<String, PrefixTreeNode> locationPointer = new HashMap<>();

    /**
     * Example constructor shows how to create and start an XML parser.
     * You do not need to modify this constructor, but you're welcome to do so.
     *
     * @param dbPath Path to the XML file to be parsed.
     */
    public GraphDB(String dbPath) {
        try {
            File inputFile = new File(dbPath);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            GraphBuildingHandler gbh = new GraphBuildingHandler(this);
            saxParser.parse(inputFile, gbh);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        createPrefixTree();
        clean();
    }

    void addNode(Node v) {
        vertex.put(v.id, v);
    }

    void addEdge(ArrayList<Long> nodes, long id, String name, String maxSpeed) {
        for (long v : nodes) {
            if (!containsNode(v)) {
                return;
            }
        }
        long v = nodes.get(0);
        for (int i = 1; i < nodes.size(); i++) {
            long w = nodes.get(i);
            Way way = new Way(v, w, id, name, maxSpeed);
            vertex.get(v).connectTo.put(w, way);
            vertex.get(w).connectTo.put(v, way);
            v = w;
        }
    }

    boolean containsNode(long v) {
        return vertex.containsKey(v);
    }

    int V() {
        return vertex.size();
    }

    List<String> getPrefixList(String prefix) {
        PrefixTreeNode head = rootPrefix.findNode(prefix);
        if (head == null) {
            return null;
        }
        LinkedList<String> prefixList = new LinkedList<>();
        Queue<PrefixTreeNode> queue = new ArrayDeque<>();
        queue.add(head);
        while (!queue.isEmpty()) {
            Queue<PrefixTreeNode> nextLevel = new ArrayDeque<>();
            for (PrefixTreeNode cur : queue) {
                if (cur.isLeaf) {
                    for (Node n : cur.graphNodes) {
                        prefixList.add(n.name);
                    }
                }
                for (PrefixTreeNode child : cur.branches.values()) {
                    nextLevel.add(child);
                }
            }
            queue = nextLevel;
        }
        return prefixList;
    }

    /**
     * Collect all locations that match a cleaned <code>locationName</code>, and return
     * information about each node that matches.
     * @param locationName A full name of a location searched for.
     * @return A list of locations whose cleaned name matches the
     * cleaned <code>locationName</code>, and each location is a map of parameters for the Json
     * response as specified: <br>
     * "lat" -> Number, The latitude of the node. <br>
     * "lon" -> Number, The longitude of the node. <br>
     * "name" -> String, The actual name of the node. <br>
     * "id" -> Number, The id of the node. <br>
     */
    List<Map<String, Object>> getLocationsByName(String locationName) {
        PrefixTreeNode head = locationPointer.get(locationName);
        if (head == null || !head.isLeaf) {
            return null;
        }
        List<Map<String, Object>> locations = new LinkedList<>();
        for (Node n : head.graphNodes) {
            Map<String, Object> loc = new HashMap<>();
            loc.put("lat", n.lat);
            loc.put("lon", n.lon);
            loc.put("name", n.name);
            loc.put("id", n.id);
            locations.add(loc);
        }
        return locations;
    }

    /**
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     *
     * @param s Input string.
     * @return Cleaned string.
     */
    static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    private void createPrefixTree() {
        for (Node n : vertex.values()) {
            locationPointer.put(cleanString(n.name), rootPrefix.addNode(n));
        }
    }

    private void removeNode(long v) {
        if (vertex.containsKey(v)) {
            vertex.remove(v);
        }
    }

    /**
     * Remove nodes with no connections from the graph.
     * While this does not guarantee that any two nodes in the remaining graph are connected,
     * we can reasonably assume this since typically roads are connected.
     */
    private void clean() {
        ArrayList<Long> vertexToRemove = new ArrayList<>();
        for (long v : vertices()) {
            if (vertex.get(v).connectTo.isEmpty()) {
                vertexToRemove.add(v);
            }
        }
        for (long v : vertexToRemove) {
            vertex.remove(v);
        }
    }

    /**
     * Returns an iterable of all vertex IDs in the graph.
     */
    Iterable<Long> vertices() {
        return vertex.keySet();
    }

    /**
     * Returns ids of all vertices adjacent to v.
     */
    Iterable<Long> adjacent(long v) {
        if (vertex.containsKey(v)) {
            return vertex.get(v).connectTo.keySet();
        }
        return null;
    }

    /**
     * Returns the Euclidean distance between vertices v and w, where Euclidean distance
     * is defined as sqrt( (lonV - lonV)^2 + (latV - latV)^2 ).
     */
    double distance(long v, long w) {
        if (!vertex.containsKey(v) || !vertex.containsKey(w)) {
            return -1;
        }
        double latDiff = lat(v) - lat(w);
        double lonDiff = lon(v) - lon(w);
        return Math.pow(Math.pow(latDiff, 2) + Math.pow(lonDiff, 2), 0.5);
    }

    /**
     * Returns the vertex id closest to the given longitude and latitude.
     */
    long closest(double lon, double lat) {
        long v = -1;
        Node temp = new Node(v, lat, lon);
        double curMin = -1;
        long closest = -1;
        addNode(temp);
        for (long w : vertices()) {
            if (w != -1) {
                double dist = distance(v, w);
                if (curMin == -1 || dist < curMin) {
                    curMin = dist;
                    closest = w;
                }
            }
        }
        removeNode(v);
        return closest;
    }

    /**
     * Longitude of vertex v.
     */
    double lon(long v) {
        return vertex.containsKey(v) ? vertex.get(v).lon : 0;
    }

    /**
     * Latitude of vertex v.
     */
    double lat(long v) {
        return vertex.containsKey(v) ? vertex.get(v).lat : 0;
    }
}
