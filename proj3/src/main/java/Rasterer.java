import java.util.ArrayList;
import java.util.Comparator;
import java.util.Queue;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {
    // Recommended: QuadTree instance variable. You'll need to make
    //              your own QuadTree since there is no built-in quadtree in Java.
    private class ImageNode implements Comparable<Object> {
        static final int MAXZOOM = 7;
        String ID;
        Double LonDDP;
        Double ulLat, ulLon, lrLat, lrLon;
        ImageNode one = null, two = null, three = null, four = null;

        ImageNode(String id, Double ulLat, Double ulLon, Double lrLat, Double lrLon) {
            this.ID = id;
            this.ulLat = ulLat;
            this.ulLon = ulLon;
            this.lrLat = lrLat;
            this.lrLon = lrLon;
            this.LonDDP = calculateLonDDP();
        }

        ImageNode(ImageNode parent, Integer cNum) {
            this.ID = parent.ID + cNum;
            if (cNum == 1 || cNum == 2) {
                this.ulLat = parent.ulLat;
                this.lrLat = (parent.ulLat + parent.lrLat) / 2;
            }
            if (cNum == 1 || cNum == 3) {
                this.ulLon = parent.ulLon;
                this.lrLon = (parent.ulLon + parent.lrLon) / 2;
            }
            if (cNum == 2 || cNum == 4) {
                this.ulLon = (parent.ulLon + parent.lrLon) / 2;
                this.lrLon = parent.lrLon;
            }
            if (cNum == 3 || cNum == 4) {
                this.ulLat = (parent.ulLat + parent.lrLat) / 2;
                this.lrLat = parent.lrLat;
            }
            this.LonDDP = calculateLonDDP();
        }

        String getName() {
            return imgRoot + this.ID + ".png";
        }

        double calculateLonDDP() {
            return (this.lrLon - this.ulLon) / MapServer.TILE_SIZE;
        }

        public boolean imageFit(Double lonDDP) {
            return compareTo(lonDDP) <= 0 || ID.length() == MAXZOOM;
        }

        public int compareTo(Object obj) {
            if (obj instanceof Double) {
                return this.LonDDP.compareTo((Double) obj);
            }
            if (obj instanceof ImageNode) {
                int latComp = this.lrLat.compareTo(((ImageNode) obj).lrLat);
                return latComp != 0 ? 0 - latComp : this.lrLon.compareTo(((ImageNode) obj).lrLon);
            }
            return -1;
        }

        public boolean overlaping(Double ullat, Double ullon, Double lrlat, Double lrlon) {
            if (ullat <= this.ulLat && ullat >= this.lrLat) {
                if (ullon <= this.lrLon && ullon >= this.ulLon
                        || lrlon <= this.lrLon && lrlon >= this.ulLon
                        || ullon <= this.ulLon && lrlon >= this.lrLon) {
                    return true;
                }
                return false;
            }
            if (lrlat <= this.ulLat && lrlat >= this.lrLat) {
                if (lrlon <= this.lrLon && lrlon >= this.ulLon
                        || ullon <= this.lrLon && ullon >= this.ulLon
                        || ullon <= this.ulLon && lrlon >= this.lrLon) {
                    return true;
                }
                return false;
            }
            if (ullat >= this.ulLat && lrlat <= this.lrLat) {
                if (ullon > this.lrLon || lrlon < this.ulLon) {
                    return false;
                }
                return true;
            }
            return false;
        }
    }

    private class ImageNodeComparator implements Comparator<ImageNode> {
        @Override
        public int compare(ImageNode o1, ImageNode o2) {
            return o1.compareTo(o2);
        }
    }

    private class QuadTreeImage {
        ImageNode root;

        QuadTreeImage(String id, Double ulLat, Double ulLon, Double lrLat, Double lrLon) {
            root = new ImageNode(id, ulLat, ulLon, lrLat, lrLon);
        }
    }

    private String imgRoot;
    private QuadTreeImage rootImage;

    /** imgRoot is the name of the directory containing the images.
     *  You may not actually need this for your class. */
    public Rasterer(String imgRoot) {
        // YOUR CODE HERE
        this.imgRoot = imgRoot;
        this.rootImage = new QuadTreeImage("", MapServer.ROOT_ULLAT, MapServer.ROOT_ULLON,
                                                MapServer.ROOT_LRLAT, MapServer.ROOT_LRLON);
        Queue<ImageNode> treeLevel = new ArrayDeque<>();
        treeLevel.add(rootImage.root);
        for (int i = 0; i < 7; i++) {
            Queue<ImageNode> nextLevel = new ArrayDeque<>();
            while (!treeLevel.isEmpty()) {
                ImageNode node = treeLevel.remove();
                node.one = new ImageNode(node, 1);
                node.two = new ImageNode(node, 2);
                node.three = new ImageNode(node, 3);
                node.four = new ImageNode(node, 4);
                nextLevel.add(node.one);
                nextLevel.add(node.two);
                nextLevel.add(node.three);
                nextLevel.add(node.four);
            }
            treeLevel = nextLevel;
        }
        this.rootImage.root.ID = "root";
    }

    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     * <p>
     *     The grid of images must obey the following properties, where image in the
     *     grid is referred to as a "tile".
     *     <ul>
     *         <li>The tiles collected must cover the most longitudinal distance per pixel
     *         (LonDPP) possible, while still covering less than or equal to the amount of
     *         longitudinal distance per pixel in the query box for the user viewport size. </li>
     *         <li>Contains all tiles that intersect the query bounding box that fulfill the
     *         above condition.</li>
     *         <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     *     </ul>
     * </p>
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     *
     * @return A map of results for the front end as specified:
     * "render_grid"   -> String[][], the files to display
     * "raster_ul_lon" -> Number, the bounding upper left longitude of the rastered image <br>
     * "raster_ul_lat" -> Number, the bounding upper left latitude of the rastered image <br>
     * "raster_lr_lon" -> Number, the bounding lower right longitude of the rastered image <br>
     * "raster_lr_lat" -> Number, the bounding lower right latitude of the rastered image <br>
     * "depth"         -> Number, the 1-indexed quadtree depth of the nodes of the rastered image.
     *                    Can also be interpreted as the length of the numbers in the image
     *                    string. <br>
     * "query_success" -> Boolean, whether the query was able to successfully complete. Don't
     *                    forget to set this to true! <br>
     * @see #REQUIRED_RASTER_REQUEST_PARAMS
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        // System.out.println(params);
        Map<String, Object> result = new HashMap<>();
        Double lonDDP = ((params.get("lrlon") - params.get("ullon")) / params.get("w"));
        Double ulLat = params.get("ullat"), ulLon = params.get("ullon");
        Double lrLat = params.get("lrlat"), lrLon = params.get("lrlon");

        ArrayList<ImageNode> nodeArrayList = buildGrid(
                lonDDP, ulLat, ulLon, lrLat, lrLon, rootImage.root);
        nodeArrayList.sort(new ImageNodeComparator());
        double topLat = nodeArrayList.get(0).ulLat;
        int width = 0;
        for (ImageNode node : nodeArrayList) {
            if (node.ulLat != topLat) {
                break;
            }
            width++;
        }
        String[][] grid = new String[nodeArrayList.size() / width][width];

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                grid[i][j] = nodeArrayList.get(i * width + j).getName();
            }
        }

        result.put("render_grid", grid);
        result.put("raster_ul_lon", nodeArrayList.get(0).ulLon);
        result.put("raster_ul_lat", nodeArrayList.get(0).ulLat);
        result.put("raster_lr_lon", nodeArrayList.get(nodeArrayList.size() - 1).lrLon);
        result.put("raster_lr_lat", nodeArrayList.get(nodeArrayList.size() - 1).lrLat);

        if (nodeArrayList.get(0).ID.equals("root")) {
            result.put("depth", 0);
        } else {
            result.put("depth", nodeArrayList.get(0).ID.length());
        }
        result.put("query_success", true);
        return result;
    }

    private ArrayList<ImageNode> buildGrid(
            Double lonDDP, Double ulLat, Double ulLon, Double lrLat, Double lrLon, ImageNode root) {
        ArrayList<ImageNode> result = new ArrayList<>();
        if (!root.overlaping(ulLat, ulLon, lrLat, lrLon)) {
            return result;
        }
        if (root.imageFit(lonDDP)) {
            result.add(root);
            return result;
        }
        result.addAll(buildGrid(lonDDP, ulLat, ulLon, lrLat, lrLon, root.one));
        result.addAll(buildGrid(lonDDP, ulLat, ulLon, lrLat, lrLon, root.two));
        result.addAll(buildGrid(lonDDP, ulLat, ulLon, lrLat, lrLon, root.three));
        result.addAll(buildGrid(lonDDP, ulLat, ulLon, lrLat, lrLon, root.four));
        return result;
    }

//    private static void printGrid(String [][] grid) {
//        for (int i  = 0;  i < grid.length; i++) {
//            for (int j = 0; j < grid[0].length; j++) {
//                System.out.print(grid[i][j] +  "  ");
//            }
//            System.out.println();
//        }
//    }
//
//    public static void main(String [] args) {
//        Rasterer a = new Rasterer("img/");
//        Queue<ImageNode> treeLevel = new ArrayDeque<>();
//        treeLevel.add(a.rootImage.root);
//        while (!treeLevel.isEmpty()) {
//            Queue<ImageNode> nextLevel = new ArrayDeque<>();
//            while (!treeLevel.isEmpty()) {
//                ImageNode node = treeLevel.remove();
//                System.out.println(node.getName());
//                if (node.one != null){
//                    nextLevel.add(node.one);
//                    nextLevel.add(node.two);
//                    nextLevel.add(node.three);
//                    nextLevel.add(node.four);
//                }
//            }
//            treeLevel = nextLevel;
//        }
//        Map<String, Double> params = new HashMap<>();
//        params.put("ullat", 37.88746545843562);
//        params.put("ullon", -122.2591326176749);
//        params.put("lrlat", 37.83495035769344);
//        params.put("lrlon", -122.2119140625);
//        params.put("w", 929.0);
//        params.put("h", 944.0);
//        Map<String, Object> result = a.getMapRaster(params);
//        String[][] grid = (String[][]) result.get("render_grid");
//        printGrid(grid);
//        System.out.println(result.get("raster_ul_lon"));
//        System.out.println(result.get("depth"));
//        System.out.println(result.get("raster_lr_lon"));
//        System.out.println(result.get("raster_lr_lat"));
//        System.out.println(result.get("raster_ul_lat"));
//    }
}
