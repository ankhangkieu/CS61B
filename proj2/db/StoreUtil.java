package db;

import edu.princeton.cs.algs4.Out;

public class StoreUtil implements Utility {
    @Override
    public Object apply(Object... args) {
        Out out;
        if (args.length != 1 || !(args[0] instanceof Table)) {
            throw new RuntimeException("ERROR: Wrong call for Store query.");
        }
        out = new Out(((Table) args[0]).getName() + ".tbl");
        out.println(args[0]);
        out.close();
        return "";
    }

    @Override
    public String toString() {
        return "This function stores table data to a tbl tile.";
    }
}
