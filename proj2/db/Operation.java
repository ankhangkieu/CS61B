package db;

public enum Operation {
    ADD("addition"),
    SUBTRACT("subtraction"),
    MULTIPLY("multiplication"),
    DIVIDE("division");

    private final String op;

    Operation(String op) {
        this.op = op;
    }

    public boolean equals(Operation other) {
        return this.op.equals(other.op);
    }

    public String toString() {
        return this.op;
    }
}
