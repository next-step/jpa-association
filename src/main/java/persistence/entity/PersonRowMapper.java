package persistence.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import jdbc.RowMapper;

public class PersonRowMapper implements RowMapper<Person> {
    @Override
    public Person mapRow(ResultSet resultSet) throws SQLException {
        if (!resultSet.next()) {
            return null;
        }
        return new Person(
                resultSet.getLong("id"),
                resultSet.getString("nick_name"),
                resultSet.getInt("old"),
                resultSet.getString("email"),
                null
        );
    }
}
