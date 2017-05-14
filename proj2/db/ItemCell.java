package db;

public class ItemCell extends Cell {
    private Object item;

    public ItemCell(Object item, int colIndex) {
        super(CellType.typeOf(item), colIndex);
        this.item = item;
    }

    public ItemCell(ItemCell x, ItemCell y, Operation op) {
        super(CellType.typeOf(x, y), 0);
        if (cType.isNAN()) {
            item = "NaN";
            return;
        }
        if (cType.isNOVALUE()) {
            item = "NOVALUE";
            return;
        }
        if (op.equals(Operation.ADD)) {
            this.item = add(x.item, y.item);
        } else if (op.equals(Operation.SUBTRACT)) {
            this.item = sub(x.item, y.item);
        } else if (op.equals(Operation.MULTIPLY)) {
            this.item = mul(x.item, y.item);
        } else {
            this.item = div(x.item, y.item);
        }
    }

    private Object add(Object xItem, Object yItem) {
        if (xItem instanceof String && yItem instanceof String) {
            String xStr;
            String yStr;
            if (((String) xItem).contains("'")) {
                xStr = ((String) xItem).substring(0, ((String) xItem).lastIndexOf('\''));
            } else {
                xStr = "'";
            }
            if (((String) yItem).contains("'")) {
                yStr = ((String) yItem).substring(1);
            } else {
                yStr = "'";
            }
            return xStr + yStr;
        }

        if (xItem instanceof String) {
            xItem = 0;
        }
        if (yItem instanceof String) {
            yItem = 0;
        }
        Double first = Double.parseDouble(xItem.toString());
        Double second = Double.parseDouble(yItem.toString());
        Double result = first + second;
        if (cType.equals(CellType.FLOAT)) {
            return result;
        }
        if (result > Integer.MAX_VALUE) {
            return (int) (Integer.MIN_VALUE + result - Integer.MAX_VALUE - 1);
        }
        if (result < Integer.MIN_VALUE) {
            return (int) (Integer.MAX_VALUE + result - Integer.MIN_VALUE + 1);
        }
        return result.intValue();
    }

    private Object sub(Object xItem, Object yItem) {
        if (xItem instanceof String) {
            xItem = 0;
        }
        if (yItem instanceof String) {
            yItem = 0;
        }
        Double first = Double.parseDouble(xItem.toString());
        Double second = Double.parseDouble(yItem.toString());
        Double result = first - second;
        if (cType.equals(CellType.FLOAT)) {
            return result;
        }
        return result.intValue();
    }

    private Object mul(Object xItem, Object yItem) {
        if (xItem instanceof String) {
            xItem = 0;
        }
        if (yItem instanceof String) {
            yItem = 0;
        }
        Double first = Double.parseDouble(xItem.toString());
        Double second = Double.parseDouble(yItem.toString());
        Double result = first * second;
        if (cType.equals(CellType.FLOAT)) {
            return result;
        }
        return result.intValue();
    }

    private Object div(Object xItem, Object yItem) {
        if (xItem instanceof String) {
            xItem = 0;
        }
        if (yItem instanceof String) {
            yItem = 0;
        }
        Double first = Double.parseDouble(xItem.toString());
        Double second = Double.parseDouble(yItem.toString());
        if (second.equals(0.0)) {
            cType = CellType.NAN;
            return "NaN";
        }
        Double result = first / second;
        if (cType.equals(CellType.FLOAT)) {
            return result;
        }
        return result.intValue();
    }

    @Override
    public Cell getCopyOfCell() {
        return new ItemCell(item, colIndex);
    }

    @Override
    public boolean update(Object other) {
        if (cType.equalsType(other)) {
            this.item = other;
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Cell other) {
        return this.item.equals(((ItemCell) other).item);
    }

    public boolean compare(ItemCell other, ComparisonOperator op) {
        double result;
        if (this.cType.isNOVALUE() || other.cType.isNOVALUE()) {
            return false;
        }
        if (this.cType.isNAN() || other.cType.isNAN()) {
            return true;
        }
        if (this.cType.equals(CellType.STRING)) {
            result = (double) ((String) this.item).compareTo((String) other.item);
        } else {
            Double firstItem = Double.parseDouble(this.item.toString());
            Double secondItem = Double.parseDouble(other.item.toString());
            result = firstItem - secondItem;
        }
        if (op.equals(ComparisonOperator.EQUAL)) {
            return result == 0;
        } else if (op.equals(ComparisonOperator.GREATEROREQUAL)) {
            return result >= 0;
        } else if (op.equals(ComparisonOperator.LESSOREQUAL)) {
            return result <= 0;
        } else if (op.equals(ComparisonOperator.GREATERTHAN)) {
            return result > 0;
        } else if (op.equals(ComparisonOperator.LESSTHAN)) {
            return result < 0;
        } else {
            return result != 0;
        }
    }

    @Override
    public String toString() {
        if (item instanceof Double) {
            return String.format("%.3f", (Double) item);
        }
        return item.toString();
    }
}
