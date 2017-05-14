package db;

public class HeaderCell extends Cell {
    String name;

    public HeaderCell(String name, CellType ctype, int colIndex) {
        super(ctype, colIndex);
        this.name = name;
    }

    public HeaderCell(String name, String type, int colIndex) {
        super(CellType.typeOf(type), colIndex);
        if (!Character.isLetter(name.charAt(0))) {
            throw new RuntimeException("ERROR: Invalid column name: null");
        }
        this.name = name;
    }

    public HeaderCell(HeaderCell x, HeaderCell y, Operation op, String name) {
        super(CellType.typeOf(x, y, op), 0);
        this.name = name;
    }

    public String getName() {
        return this.name;
    }


    @Override
    public Cell getCopyOfCell() {
        return new HeaderCell(name, cType, colIndex);
    }

    @Override
    public boolean update(Object item) {
        return false;
    }

    @Override
    public boolean equals(Cell other) {
        return other instanceof HeaderCell && this.name.equals(((HeaderCell) other).name)
                && this.cType.equals(((HeaderCell) other).cType);
    }

    public boolean checkCellCompatible(ItemCell itemCell) {
        return this.cType.equals(itemCell.cType);
    }

    @Override
    public String toString() {
        return this.name + " " + this.cType;
    }
}
