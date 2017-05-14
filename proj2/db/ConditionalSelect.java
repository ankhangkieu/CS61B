package db;

public class ConditionalSelect {
    private static final String SYMBOL = "[<>=!]+",
            EQUAL = "\\w+\\s*==\\s*(('.*')|\\w+)",
            GREATEROREQUAL = "\\w+\\s*>=\\s*(('.*')|\\w+)",
            LESSOREQUAL = "\\w+\\s*<=\\s*(('.*')|\\w+)",
            GREATERTHAN = "\\w+\\s*>\\s*(('.*')|\\w+)",
            LESSTHAN = "\\w+\\s*<\\s*(('.*')|\\w+)",
            NOTEQUAL = "\\w+\\s*!=\\s*(('.*')|\\w+)";

    public static Table cSelect(Table tb, String[] conds) {
        if (conds.length == 1 && conds[0].equals("null")) {
            return tb;
        }
        for (String c : conds) {
            tb = filter(tb, c);
        }
        return tb;
    }

    private static Table filter(Table tb, String cond) {
        ComparisonOperator operator = checkOperation(cond);
        String[] strToken = cond.split(SYMBOL);
        String first = strToken[0].trim();
        String second = strToken[1].trim();
        if (!Character.isLetter(first.charAt(0))) {
            throw new RuntimeException("ERROR: Malformed conditional: " + cond);
        }
        if (!tb.hasColumn(first)) {
            throw new RuntimeException("ERROR: No such column with name: " + first);
        }
        if (tb.hasColumn(second)) {
            return colToCol(tb, operator, first, second);
        }
        if (second.startsWith("'") && second.endsWith("'")) {
            return colToObj(tb, operator, first, second);
        }
        Double value;
        try {
            value = Double.parseDouble(second);
        } catch (NumberFormatException nfe) {
            throw new RuntimeException("ERROR: No such column name: " + second);
        }
        return colToObj(tb, operator, first, value);
    }

    private static Table colToCol(Table tb, ComparisonOperator op, String first, String second) {
        Row headerRow = tb.getHeaderRowCopy();
        int firstIndex = headerRow.getColIndex(first);
        int secondIndex = headerRow.getColIndex(second);
        CellType firstType = headerRow.get(firstIndex).getcType();
        CellType secondType = headerRow.get(secondIndex).getcType();
        if (!firstType.comparable(secondType)) {
            throw new RuntimeException("ERROR: Incompatible types: "
                                        + firstType + " and " + secondType);
        }
        for (int i = 0; i < tb.getRowSize(); ) {
            ItemCell firstItem = (ItemCell) tb.getItemRowCopy(i).get(firstIndex);
            ItemCell secondItem = (ItemCell) tb.getItemRowCopy(i).get(secondIndex);
            if (firstItem.compare(secondItem, op)) {
                i++;
            } else {
                tb.deleteRow(i);
            }
        }
        return tb;
    }

    private static Table colToObj(Table tb, ComparisonOperator op, String first, Object second) {
        Row headerRow = tb.getHeaderRowCopy();
        int firstIndex = headerRow.getColIndex(first);
        CellType firstType = headerRow.get(firstIndex).getcType();
        if (!firstType.comparable(second)) {
            throw new RuntimeException("ERROR: Incompatible types: "
                                        + firstType + " and " + CellType.typeOf(second));
        }
        ItemCell secondItem = new ItemCell(second, 0);
        for (int i = 0; i < tb.getRowSize(); ) {
            ItemCell firstItem = (ItemCell) tb.getItemRowCopy(i).get(firstIndex);
            if (firstItem.compare(secondItem, op)) {
                i++;
            } else {
                tb.deleteRow(i);
            }
        }
        return tb;
    }

    private static ComparisonOperator checkOperation(String cond) {
        if (cond.matches(EQUAL)) {
            return ComparisonOperator.EQUAL;
        } else if (cond.matches(GREATEROREQUAL)) {
            return ComparisonOperator.GREATEROREQUAL;
        } else if (cond.matches(LESSOREQUAL)) {
            return ComparisonOperator.LESSOREQUAL;
        } else if (cond.matches(GREATERTHAN)) {
            return ComparisonOperator.GREATERTHAN;
        } else if (cond.matches(LESSTHAN)) {
            return ComparisonOperator.LESSTHAN;
        } else if (cond.matches(NOTEQUAL)) {
            return ComparisonOperator.NOTEQUAL;
        } else {
            throw new RuntimeException("ERROR: Malformed conditional: " + cond);
        }
    }
}
