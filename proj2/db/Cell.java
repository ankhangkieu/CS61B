package db;

public abstract class Cell {
    protected CellType cType;
    protected int colIndex;

    public Cell(CellType cType, int colIndex) {
        this.cType = cType;
        this.colIndex = colIndex;
    }

    public CellType getcType() {
        return cType;
    }

    public abstract Cell getCopyOfCell();

    public abstract boolean update(Object item);

    public abstract boolean equals(Cell other);

    public abstract String toString();
}
