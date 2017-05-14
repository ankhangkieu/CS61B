package db;

import java.util.HashMap;

public class Database {
    private HashMap<String, Table> tables;
    private Parser parser;

    public Database() {
        tables = new HashMap<String, Table>();
        parser = new Parser(this);
    }

    public Table getTable(String name) {
        if (contains(name)) {
            return tables.get(name);
        }
        return null;
    }

    public boolean contains(String name) {
        return tables.containsKey(name);
    }

    public boolean addTable(Table t) {
        tables.put(t.getName(), t);
        return true;
    }

    public boolean removeTable(String name) {
        if (tables.containsKey(name)) {
            tables.remove(name);
            return true;
        }
        return false;
    }

    public String transact(String query) {
        try {
            return parser.eval(query);
        } catch (RuntimeException re) {
            return re.getMessage();
        }
    }
}
