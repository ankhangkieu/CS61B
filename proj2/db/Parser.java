package db;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    // Various common constructs, simplifies parsing.
    private static final String REST = "\\s*(.*)\\s*",
            COMMA = "\\s*,\\s*",
            AND = "\\s+and\\s+";
    // Stage 1 syntax, contains the command name.
    private static final Pattern CREATE_CMD = Pattern.compile("create table " + REST),
            LOAD_CMD = Pattern.compile("load " + REST),
            STORE_CMD = Pattern.compile("store " + REST),
            DROP_CMD = Pattern.compile("drop table " + REST),
            INSERT_CMD = Pattern.compile("insert into " + REST),
            PRINT_CMD = Pattern.compile("print " + REST),
            SELECT_CMD = Pattern.compile("select " + REST);
    // Stage 2 syntax, contains the clauses of commands.
    private static final Pattern CREATE_NEW = Pattern.compile("(\\S+)\\s+\\((\\S+\\s+\\S+\\s*"
                    + "(?:,\\s*\\S+\\s+\\S+\\s*)*)\\)"),
            SELECT_CLS = Pattern.compile("([^,]+?(?:,[^,]+?)*)\\s+from\\s+"
                    + "(\\S+\\s*(?:,\\s*\\S+\\s*)*)(?:\\s+where\\s+"
                    + "([\\w\\s+\\-*/'<>=!]+?(?:\\s+and\\s+"
                    + "[\\w\\s+\\-*/'<>=!]+?)*))?"),
            CREATE_SEL = Pattern.compile("(\\S+)\\s+as select\\s+"
                    + SELECT_CLS.pattern()),
            INSERT_CLS = Pattern.compile("(\\S+)\\s+values\\s+(.+?"
                    + "\\s*(?:,\\s*.+?\\s*)*)");

    private Utility util;
    private Database db;

    Parser(Database db) {
        this.db = db;
    }

    String eval(String query) {
        Matcher m;
        if ((m = CREATE_CMD.matcher(query)).matches()) {
            return createTable(m.group(1));
        } else if ((m = LOAD_CMD.matcher(query)).matches()) {
            return loadTable(m.group(1));
        } else if ((m = STORE_CMD.matcher(query)).matches()) {
            return storeTable(m.group(1));
        } else if ((m = DROP_CMD.matcher(query)).matches()) {
            return dropTable(m.group(1));
        } else if ((m = INSERT_CMD.matcher(query)).matches()) {
            return insertRow(m.group(1));
        } else if ((m = PRINT_CMD.matcher(query)).matches()) {
            return printTable(m.group(1));
        } else if ((m = SELECT_CMD.matcher(query)).matches()) {
            return select(m.group(1));
        } else {
            throw new RuntimeException("ERROR: Malformed query: " + query);
        }
    }

    private String createTable(String expr) {
        Matcher m;
        if ((m = CREATE_NEW.matcher(expr)).matches()) {
            return createNewTable(m.group(1), m.group(2).split(COMMA));
        } else if ((m = CREATE_SEL.matcher(expr)).matches()) {
            return createSelectedTable(m.group(1), m.group(2), m.group(3), m.group(4));
        } else {
            throw new RuntimeException("ERROR: Malformed create: " + expr + "\n");
        }
    }

    private String createNewTable(String name, String[] cols) {
        StringTokenizer strToken;
        Table tb;
        Row headerRow = new Row();
        HeaderCell headerCell;
        if (db.contains(name)) {
            throw new RuntimeException("ERROR: Table already exists: " + name);
        }
        for (int i = 0; i < cols.length; i++) {
            strToken = new StringTokenizer(cols[i], " \t");
            if (strToken.countTokens() == 1) {
                throw new RuntimeException("ERROR: Malformed column declaration: "
                                            + strToken.nextToken());
            }
            headerCell = new HeaderCell(strToken.nextToken(), strToken.nextToken(), i);
            headerRow.add(headerCell);
        }
        tb = new Table(name, headerRow);
        db.addTable(tb);
        return "";
    }

    private String createSelectedTable(String name, String exprs, String tables, String conds) {
        Table newTable = select(exprs, tables, conds);
        newTable.setName(name);
        db.addTable(newTable);
        return "";
    }

    private String loadTable(String name) {
        util = new LoadTableUtil();
        Table tb = (Table) util.apply(name);
        db.addTable(tb);
        return "";
    }

    private String storeTable(String name) {
        Table tb = db.getTable(name);
        util = new StoreUtil();
        if (tb == null) {
            throw new RuntimeException("ERROR: No such table: " + name);
        }
        return (String) util.apply(tb);
    }

    private String dropTable(String name) {
        if (db.removeTable(name)) {
            return "";
        }
        throw new RuntimeException("ERROR: No such table: " + name);
    }

    private String insertRow(String expr) {
        Row itemRow = new Row();
        Matcher m = INSERT_CLS.matcher(expr);
        StringTokenizer itemRowToken;
        String name;
        if (!m.matches()) {
            throw new RuntimeException("ERROR: Malformed insert: " + expr);
        }
        name = m.group(1);
        if (!db.contains(name)) {
            throw new RuntimeException("ERROR: No such table: " + name);
        }
        itemRowToken = new StringTokenizer(m.group(2), ",");
        for (int i = 0; itemRowToken.hasMoreTokens(); i++) {
            String itemToken = itemRowToken.nextToken().trim();
            ItemCell itemCell;
            try {
                if (itemToken.length() >= 2
                        && itemToken.startsWith("'")
                        && itemToken.endsWith("'")) {
                    itemCell = new ItemCell(itemToken, i);
                } else if (itemToken.contains(".")) {
                    itemCell = new ItemCell(Double.parseDouble(itemToken), i);
                } else if (itemToken.equals("NOVALUE")) {
                    itemCell = new ItemCell(itemToken, i);
                } else {
                    itemCell = new ItemCell(Integer.parseInt(itemToken), i);
                }
            } catch (NumberFormatException nfe) {
                throw new RuntimeException("ERROR: Malformed data entry: " + itemToken);
            }
            itemRow.add(itemCell);
        }
        if (!db.getTable(name).addRow(itemRow)) {
            throw new RuntimeException("ERROR: Row does not match table");
        }
        return "";
    }

    private String printTable(String name) {
        Table tb = db.getTable(name);
        if (tb == null) {
            throw new RuntimeException("ERROR: No such table: " + name);
        }
        return tb.toString();
    }

    private String select(String expr) {
        Matcher m = SELECT_CLS.matcher(expr);
        if (!m.matches()) {
            throw new RuntimeException("ERROR: Malformed select: " + expr);
        }
        return select(m.group(1), m.group(2), m.group(3)).toString();
    }

    private Table select(String exprs, String tables, String conds) {
        ArrayList<Table> tableList = new ArrayList<>();
        Table joinTable;
        util = new JoinUtil();
        String[] tableNames = tables.split(COMMA);
        for (String name : tableNames) {
            name = name.trim();
            if (!db.contains(name)) {
                throw new RuntimeException("ERROR: No such table: " + name);
            }
            tableList.add(db.getTable(name));
        }
        joinTable = (Table) util.apply("", tableList.toArray());

        String[] cols = exprs.split(COMMA);

        String[] conditions = {"null"};
        if (conds != null) {
            conditions = conds.split(AND);
        }

        util = new SelectUtil();

        return (Table) util.apply(cols, joinTable, conditions);
    }
}
