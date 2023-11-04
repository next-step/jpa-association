package persistence.entity.mapper;


import java.sql.ResultSet;
import java.sql.SQLException;

public interface EntityColumnsMapper {

    <T> void mapColumns(ResultSet resultSet, T instance) throws SQLException;

}
