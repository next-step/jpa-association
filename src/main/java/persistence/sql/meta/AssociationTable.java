package persistence.sql.meta;

import java.util.List;

public interface AssociationTable {
    List<Column> getColumns();

    String getName();

    String getJoinColumn();
}
