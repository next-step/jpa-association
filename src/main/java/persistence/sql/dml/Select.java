package persistence.sql.dml;

import persistence.sql.mapping.Table;

import java.util.ArrayList;
import java.util.List;

public class Select {

    private final Table table;

    private final Wheres wheres;

    public Select(final Table table) {
        this(table, new ArrayList<>());
    }

    public Select(final Table table, final List<Where> wheres) {
        this.table = table;
        this.wheres = new Wheres(wheres);
    }

    public void addWhere(final Where where) {
        this.wheres.addWhere(where);
    }

    public Table getTable() {
        return table;
    }

    public List<Where> getWheres() {
        return this.wheres.getWheres();
    }
}
