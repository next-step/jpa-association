package persistence.sql.model;

import java.util.List;

public interface BaseTable {
    
    String getName();

    PKColumn getPKColumn();

    Columns getColumns();

    List<JoinTable> getJoinTables();

    String getPKColumnName();

    List<String> getAllColumnNames();
}
