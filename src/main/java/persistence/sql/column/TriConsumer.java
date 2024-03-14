package persistence.sql.column;

import java.sql.ResultSet;

@FunctionalInterface
public interface TriConsumer<T> {

    void accept(ResultSet resultSet, T instance, Column column);

}
