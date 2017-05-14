package db;

import edu.princeton.cs.algs4.In;
import java.util.StringTokenizer;

public class LoadTableUtil implements Utility {
    public static void main(String[] args) {
        LoadTableUtil util = new LoadTableUtil();
        Object obj = util.apply("a");
        if (obj instanceof String) {
            System.out.println(obj);
        } else {
            Table tb = (Table) obj;
            System.out.println(tb);
        }
    }

    @Override
    public Table apply(Object... args) {
        Table tb;
        In in;
        StringTokenizer strToken;
        if (args.length == 1 && args[0] instanceof String) {
            try {
                in = new In((args[0]) + ".tbl");
            } catch (IllegalArgumentException iae) {
                throw new RuntimeException("ERROR: No such TBL file: " + args[0] + ".tbl");
            }
            if (in.isEmpty()) {
                throw new RuntimeException("ERROR: Malformed column declaration: ");
            }
            strToken = new StringTokenizer(in.readLine(), ",\n");

            Row headerRow = createHeaderRowWithToken(strToken);
            tb = new Table((String) args[0], headerRow);

            String[] lines = in.readAllLines();
            int firstNonEmptyLine = -1;
            for (int i = 0; i < lines.length && firstNonEmptyLine != 0; i++) {
                if (!lines[i].isEmpty()) {
                    firstNonEmptyLine = i;
                }
            }
            if (firstNonEmptyLine > 0) {
                throw new RuntimeException("ERROR: Malformed data entry: ");
            }
            for (String line : lines) {
                strToken = new StringTokenizer(line, ",");
                Row itemRow = createItemRowWithToken(strToken);
                if (!tb.addRow(itemRow)) {
                    throw new RuntimeException("ERROR: Row does not match table");
                }
            }
            in.close();
            return tb;
        }
        throw new RuntimeException("ERROR: Malformed query: load");
    }

    private Row createHeaderRowWithToken(StringTokenizer headerRowToken) {
        if (!headerRowToken.hasMoreTokens()) {
            throw new RuntimeException("ERROR: Malformed column declaration: ");
        }
        Row headerRow = new Row();

        for (int i = 0; headerRowToken.hasMoreTokens(); i++) {
            String header = headerRowToken.nextToken();
            StringTokenizer headerToken = new StringTokenizer(header, " \t");
            if (headerToken.countTokens() != 2) {
                throw new RuntimeException("ERROR: Malformed column declaration: " + header);
            }
            String headerName = headerToken.nextToken();
            String headerType = headerToken.nextToken();
            HeaderCell headerCell = new HeaderCell(headerName, headerType, i);
            headerRow.add(headerCell);
        }
        return headerRow;
    }

    private Row createItemRowWithToken(StringTokenizer itemRowToken) {
        if (!itemRowToken.hasMoreTokens()) {
            throw new RuntimeException("ERROR: Malformed data entry: ");
        }
        Row itemRow = new Row();
        for (int i = 0; itemRowToken.hasMoreTokens(); i++) {
            String itemToken = itemRowToken.nextToken().trim();
            ItemCell itemCell;
            try {
                if (itemToken.length() >= 2
                        && itemToken.startsWith("'")
                        && itemToken.endsWith("'")) {
                    itemCell = new ItemCell(itemToken, i);
                } else if (itemToken.contains(".")) {
                    itemCell = new ItemCell(Double.parseDouble(itemToken), i);
                } else if (itemToken.equals("NOVALUE")) {
                    itemCell = new ItemCell(itemToken, i);
                } else {
                    itemCell = new ItemCell(Integer.parseInt(itemToken), i);
                }
            } catch (NumberFormatException nfe) {
                throw new RuntimeException("ERROR: Malformed data entry: " + itemToken);
            }
            itemRow.add(itemCell);
        }
        return itemRow;
    }

    @Override
    public String toString() {
        return "This function reads a tbl file and create a table in the Database";
    }
}
