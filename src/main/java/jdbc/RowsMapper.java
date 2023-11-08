package jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@FunctionalInterface
public interface RowsMapper<T> {
    List<T> mapRow(final ResultSet resultSet) throws SQLException;
}
