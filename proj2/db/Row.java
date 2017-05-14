package db;

import java.util.ArrayList;
import java.util.Collection;

public class Row extends ArrayList<Cell> {
    public Row() {
        super();
    }

    public Row(Collection<Cell> c) {
        super(c);
    }

    public boolean update(Row row) {
        clear();
        return addAll(row);
    }

    public int getColIndex(String name) {
        for (int i = 0; i < size(); i++) {
            if (((HeaderCell) get(i)).getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    public boolean updateAt(int index, Object item) {
        return get(index).update(item);
    }

    public boolean addHeader(String type, String name) throws Exception {
        return add(new HeaderCell(name, type, size()));
    }

    public boolean addItem(Object object) {
        return add(new ItemCell(object, size()));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size(); i++) {
            sb.append(get(i));
            if (i != size() - 1) {
                sb.append(',');
            }
        }
        return sb.toString();
    }
}
