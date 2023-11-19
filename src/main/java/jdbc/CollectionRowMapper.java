package jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface CollectionRowMapper<T> {

  List<T> mapRow(final ResultSet resultSet) throws SQLException;
}
