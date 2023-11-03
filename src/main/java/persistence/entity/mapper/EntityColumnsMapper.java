package persistence.entity.mapper;


import java.sql.ResultSet;
import java.sql.SQLException;

public interface EntityColumnsMapper {

    void mapColumns(ResultSet resultSet, Object instance) throws SQLException;

}
