package db;

public enum ComparisonOperator {
    EQUAL("=="),
    GREATEROREQUAL(">="),
    LESSOREQUAL("<="),
    GREATERTHAN(">"),
    LESSTHAN("<"),
    NOTEQUAL("!=");

    private final String operator;

    ComparisonOperator(String operator) {
        this.operator = operator;
    }

    public boolean equals(ComparisonOperator op) {
        return this.operator.equals(op.operator);
    }
}
