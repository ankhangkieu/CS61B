package db;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class SelectUtil implements Utility {
    private static final String AS = "\\s+as\\s+";

    @Override
    public Object apply(Object... args) {
        if (args.length != 3) {
            throw new RuntimeException("ERROR: Wrong call to this function.");
        }
        String[] cols = (String[]) args[0];
        Table mergedTable = (Table) args[1];
        String[] conds = (String[]) args[2];
        String[] nameExp;
        ArrayList<Table> oneDimTables = new ArrayList<Table>();
        ArrayList<String> colNames = new ArrayList<String>();

        Table newTable = null;
        if (cols.length == 1 && cols[0].equals("*")) {
            newTable = mergedTable;
        } else {
            for (String c : cols) {
                nameExp = c.split(AS);
                if (nameExp.length == 1) {
                    String cName = nameExp[0];
                    if (colNames.contains(cName)) {
                        throw new RuntimeException("ERROR: Duplicate column name: " + cName);
                    }
                    if (!mergedTable.hasColumn(cName)) {
                        throw new RuntimeException("ERROR: No such column with name: " + cName);
                    }
                    colNames.add(cName);
                    oneDimTables.add(mergedTable.getColAsTable(cName));
                } else if (nameExp.length == 2) {
                    String op = nameExp[0];
                    String newName = nameExp[1];
                    if (colNames.contains(newName)) {
                        throw new RuntimeException("ERROR: Duplicate column name: " + newName);
                    }
                    colNames.add(newName);

                    Operation operation = checkOperation(op, c);

                    StringTokenizer opTokens = new StringTokenizer(op, "\t+-/* ");
                    if (opTokens.countTokens() != 2) {
                        throw new RuntimeException("ERROR: Malformed column expression: " + c);
                    }
                    String cName1 = opTokens.nextToken();
                    String cName2 = opTokens.nextToken();
                    if (!mergedTable.hasColumn(cName1)) {
                        throw new RuntimeException("ERROR: No such column with name: " + cName1);
                    }
                    if (!mergedTable.hasColumn(cName2)) {
                        throw new RuntimeException("ERROR: No such column with name: " + cName2);
                    }
                    Table x = mergedTable.getColAsTable(cName1);
                    Table y = mergedTable.getColAsTable(cName2);

                    Table operatedTable =
                            OperationalSelect.oSelect(x, y, operation, newName);

                    oneDimTables.add(operatedTable);

                } else {
                    throw new RuntimeException("ERROR: Malformed column expression: " + c);
                }
            }
        }

        for (Table t : oneDimTables) {
            newTable = merge(newTable, t);
        }

        return ConditionalSelect.cSelect(newTable, conds);
    }

    private Table merge(Table x, Table y) {
        if (x == null) {
            return y;
        }
        if (y == null) {
            return x;
        }
        Row xHeader = x.getHeaderRowCopy();
        Row yHeader = y.getHeaderRowCopy();
        for (int i = 0; i < yHeader.size(); i++) {
            xHeader.add(yHeader.get(i).getCopyOfCell());
        }
        Table tb = new Table("", xHeader);
        for (int i = 0; i < x.getRowSize(); i++) {
            Row xItemRow = x.getItemRowCopy(i);
            Row yItemRow = y.getItemRowCopy(i);
            for (int j = 0; j < yItemRow.size(); j++) {
                xItemRow.add(yItemRow.get(j).getCopyOfCell());
            }
            tb.addRow(xItemRow);
        }
        return tb;
    }

    private Operation checkOperation(String op, String expr) {
        char opChar = ' ';
        Operation operation = null;
        boolean hasOperation = false;
        if (op.contains("+")) {
            hasOperation = true;
            operation = Operation.ADD;
            opChar = '+';
        }
        if (op.contains("-")) {
            if (hasOperation) {
                throw new RuntimeException("ERROR: Malformed column expression: " + expr);
            }
            hasOperation = true;
            operation = Operation.SUBTRACT;
            opChar = '-';
        }
        if (op.contains("*")) {
            if (hasOperation) {
                throw new RuntimeException("ERROR: Malformed column expression: " + expr);
            }
            hasOperation = true;
            operation = Operation.MULTIPLY;
            opChar = '*';
        }
        if (op.contains("/")) {
            if (hasOperation) {
                throw new RuntimeException("ERROR: Malformed column expression: " + expr);
            }
            hasOperation = true;
            operation = Operation.DIVIDE;
            opChar = '/';
        }
        if (!hasOperation || op.lastIndexOf(opChar) != op.indexOf(opChar)) {
            throw new RuntimeException("ERROR: Malformed column expression: " + expr);
        }
        return operation;
    }

    @Override
    public String toString() {
        return "This function selects the columns from a table.";
    }
}
