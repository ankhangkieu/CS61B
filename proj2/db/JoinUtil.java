package db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeMap;

public class JoinUtil implements Utility {
    @Override
    public Object apply(Object... args) {
        String name = "";
        Table joinTable;
        if (args.length != 2) {
            throw new RuntimeException("ERROR: Malformed query: join");
        }
        if (args[0] instanceof String) {
            name = (String) args[0];
        }
        joinTable = null;
        for (int i = 0; i < ((Object[]) args[1]).length; i += 1) {
            joinTable = join(name, joinTable, (Table) ((Object[]) args[1])[i]);
        }
        return joinTable;
    }

    private Table join(String name, Table x, Table y) {
        if (x == null) {
            return y;
        } else if (y == null) {
            return x;
        }
        TreeMap<Integer, Integer> pairIndex = findCommon(x, y);
        Row headerRow = merge(x.getHeaderRowCopy(), y.getHeaderRowCopy(), pairIndex);
        Table joinTable = new Table(name, headerRow);

        for (int i = 0; i < x.getRowSize(); i++) {
            for (int j = 0; j < y.getRowSize(); j++) {
                boolean matched = matchRow(x.getItemRowCopy(i), y.getItemRowCopy(j), pairIndex);
                if (matched) {
                    joinTable.addRow(merge(x.getItemRowCopy(i), y.getItemRowCopy(j), pairIndex));
                }
            }
        }
        return joinTable;
    }

    private boolean matchRow(Row xRow, Row yRow, TreeMap<Integer, Integer> pairIndex) {
        Set keySet = pairIndex.keySet();
        Object[] keyObj = keySet.toArray();
        Integer[] keys = Arrays.copyOf(keyObj, keyObj.length, Integer[].class);
        for (int k : keys) {
            Cell xCell = xRow.get(k);
            Cell yCell = yRow.get(pairIndex.get(k));
            if (!xCell.equals(yCell)) {
                return false;
            }
        }
        return true;
    }

    private TreeMap<Integer, Integer> findCommon(Table x, Table y) {
        TreeMap<Integer, Integer> pairIndex = new TreeMap<Integer, Integer>();
        Row xHeaderRow = x.getHeaderRowCopy();
        Row yHeaderRow = y.getHeaderRowCopy();
        for (int i = 0; i < xHeaderRow.size(); i++) {
            Cell xCell = xHeaderRow.get(i);
            for (int j = 0; j < yHeaderRow.size(); j++) {
                Cell yCell = yHeaderRow.get(j);
                if (xCell.equals(yCell)) {
                    pairIndex.put(i, j);
                }
            }
        }
        return pairIndex;
    }

    private Row merge(Row xRow, Row yRow, TreeMap<Integer, Integer> pairIndex) {
        Set keySet = pairIndex.keySet();
        ArrayList<Integer> valuesList = new ArrayList<>(pairIndex.values());
        Object[] keyObj = keySet.toArray();
        Object[] valueObj = valuesList.toArray();

        Integer[] keys = Arrays.copyOf(keyObj, keyObj.length, Integer[].class);
        Integer[] values = Arrays.copyOf(valueObj, valueObj.length, Integer[].class);

        Row newRow = new Row();
        for (int k : keys) {
            newRow.add(xRow.get(k));
        }

        for (int i = 0; i < xRow.size(); i++) {
            boolean added = false;
            for (int k : keys) {
                if (i == k) {
                    added = true;
                    break;
                }
            }
            if (!added) {
                newRow.add(xRow.get(i));
            }
        }
        for (int i = 0; i < yRow.size(); i++) {
            boolean added = false;
            for (int v : values) {
                if (i == v) {
                    added = true;
                    break;
                }
            }
            if (!added) {
                newRow.add(yRow.get(i));
            }
        }
        return newRow;
    }

    @Override
    public String toString() {
        return "This function joins two tables.";
    }
}
