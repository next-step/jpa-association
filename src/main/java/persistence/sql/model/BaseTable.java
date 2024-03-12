package persistence.sql.model;

import java.util.List;

public interface BaseTable {

    Class<?> getEntity();
    
    String getName();

    PKColumn getPKColumn();

    Columns getColumns();

    String getPKColumnName();

    List<String> getAllColumnNames();
}
