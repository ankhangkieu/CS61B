package db;

import java.util.ArrayList;

public class Table {
    private String name;
    private Row headerRow;
    private int colSize;
    private ArrayList<Row> rows;
    private int rowSize;

    public Table() {
        this.name = "";
        this.headerRow = new Row();
        this.colSize = 0;
        this.rows = new ArrayList<Row>();
        this.rowSize = 0;
    }

    public Table(String name, Row headerRow) {
        this.name = name;
        this.headerRow = headerRow;
        this.colSize = headerRow.size();
        this.rows = new ArrayList<Row>();
        this.rowSize = 0;
    }

    public boolean isEmpty() {
        return rowSize == 0;
    }

    public boolean deleteRow(int index) {
        if (isEmpty() || index >= rowSize) {
            return false;
        }
        rows.remove(index);
        rowSize -= 1;
        return true;
    }

    public boolean addRow(Row row) {
        boolean compatible = checkRowCompatible(row);
        if (compatible) {
            rows.add(row);
            rowSize += 1;
        }
        return compatible;
    }

    private boolean checkRowCompatible(Row row) {
        if (colSize != row.size()) {
            return false;
        }
        for (int i = 0; i < colSize; i++) {
            HeaderCell headerCell = (HeaderCell) headerRow.get(i);
            ItemCell itemCell = (ItemCell) row.get(i);
            if (!headerCell.checkCellCompatible(itemCell)) {
                return false;
            }
        }
        return true;
    }

    public int getRowSize() {
        return rowSize;
    }

    public int getColSize() {
        return colSize;
    }

    public Table getColAsTable(String colName) {
        int colIndex = this.headerRow.getColIndex(colName);
        if (colIndex == -1) {
            return null;
        }
        Cell headerCell = (headerRow.get(colIndex)).getCopyOfCell();
        Row newHeaderRow = new Row();
        newHeaderRow.add(headerCell);
        Table table = new Table("", newHeaderRow);

        for (int i = 0; i < rowSize; i++) {
            Row row = new Row();
            row.add((rows.get(i).get(colIndex)).getCopyOfCell());
            table.addRow(row);
        }
        return table;
    }

    public Row getHeaderRowCopy() {
        return new Row(headerRow);
    }

    public Row getItemRowCopy(int index) {
        if (isEmpty() || index >= rowSize) {
            return null;
        }
        return new Row(rows.get(index));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean hasColumn(String cName) {
        for (Cell c : headerRow) {
            if (((HeaderCell) c).getName().equals(cName)) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        StringBuilder tableString = new StringBuilder();
        tableString.append(headerRow.toString());
        for (int i = 0; i < rowSize; i++) {
            tableString.append('\n');
            tableString.append(rows.get(i).toString());
        }
        return tableString.toString();
    }
}
