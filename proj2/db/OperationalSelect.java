package db;

public class OperationalSelect {
    public static Table oSelect(Table x, Table y, Operation op, String name) {
        Table newTable;
        Row headerRow;
        HeaderCell headerCell = new HeaderCell((HeaderCell) x.getHeaderRowCopy().get(0),
                (HeaderCell) y.getHeaderRowCopy().get(0), op, name);
        headerRow = new Row();
        headerRow.add(headerCell);

        newTable = new Table("", headerRow);
        for (int i = 0; i < x.getRowSize(); i++) {
            Row itemRow = rowOperation(x.getItemRowCopy(i), y.getItemRowCopy(i), op);
            newTable.addRow(itemRow);
        }
        return newTable;
    }

    private static Row rowOperation(Row x, Row y, Operation op) {
        Row newRow = new Row();
        ItemCell xCell = (ItemCell) x.get(0);
        ItemCell yCell = (ItemCell) y.get(0);
        newRow.add(new ItemCell(xCell, yCell, op));
        return newRow;
    }
}
