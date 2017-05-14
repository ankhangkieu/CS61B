package db;

public enum CellType {
    STRING("string"),
    INTEGER("int"),
    FLOAT("float"),
    NOVALUE("NOVALUE"),
    NAN("NaN");

    private final String type;

    CellType(String type) {
        this.type = type;
    }

    public static CellType typeOf(String str) {
        if (str.equals("int")) {
            return INTEGER;
        } else if (str.equals("float")) {
            return FLOAT;
        } else if (str.equals("string")) {
            return STRING;
        } else {
            throw new RuntimeException("ERROR: Invalid type: " + str);
        }
    }

    public static CellType typeOf(Object obj) {
        if (obj instanceof Integer) {
            return INTEGER;
        } else if (obj instanceof Double) {
            return FLOAT;
        } else if (obj instanceof String) {
            if (obj.equals("NOVALUE")) {
                return NOVALUE;
            } else if (obj.equals("NaN")) {
                return NAN;
            }
            return STRING;
        } else {
            throw new RuntimeException("Type of OBJECT does not exist in CellType Enum");
        }
    }

    public static CellType typeOf(HeaderCell x, HeaderCell y, Operation op) {
        if (x.getcType().equals(STRING) && y.getcType().equals(STRING)) {
            if (!op.equals(Operation.ADD)) {
                throw new RuntimeException("ERROR: Strings do not support " + op.toString());
            }
            return STRING;
        }
        if (x.getcType().equals(STRING) || y.getcType().equals(STRING)) {
            throw new RuntimeException("ERROR: Incompatible types :"
                                    + x.getcType().toString() + " and " + y.getcType().toString());
        }
        if (x.getcType().equals(FLOAT) || y.getcType().equals(FLOAT)) {
            return FLOAT;
        }
        return INTEGER;
    }

    public static CellType typeOf(ItemCell x, ItemCell y) {
        if (x.getcType().isNAN() || y.getcType().isNAN()) {
            return NAN;
        }
        if (x.getcType().isNOVALUE()) {
            return y.getcType();
        }
        if (y.getcType().isNOVALUE()) {
            return x.getcType();
        }
        if (x.getcType().equals(STRING)) {
            return STRING;
        }
        if (x.getcType().equals(FLOAT) || y.getcType().equals(FLOAT)) {
            return FLOAT;
        }
        return INTEGER;
    }

    public boolean isNAN() {
        return type.equals(NAN.type);
    }

    public boolean isNOVALUE() {
        return type.equals(NOVALUE.type);
    }

    public boolean equals(CellType cellType) {
        return this.type.equals(NOVALUE.type) || cellType.type.equals(NOVALUE.type)
                || (this.type.equals(NAN.type) && !cellType.type.equals(STRING.type))
                || (cellType.type.equals(NAN.type) && !this.type.equals(STRING.type))
                || this.type.equals(cellType.type);
    }

    public boolean equalsType(Object obj) {
        return this.equals(typeOf(obj));
    }

    public boolean comparable(CellType cellType) {
        return (this.type.equals("string") && cellType.type.equals("string"))
                || (!this.type.equals("string") && !cellType.type.equals("string"));
    }

    public boolean comparable(Object obj) {
        return comparable(typeOf(obj));
    }

    public String toString() {
        return type;
    }
}
