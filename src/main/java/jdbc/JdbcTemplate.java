package jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JdbcTemplate {
    private final Connection connection;

    public JdbcTemplate(final Connection connection) {
        this.connection = connection;
    }

    public void execute(final String sql) {
        try (final Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T queryForObject(final String sql, final RowMapper<T> rowMapper) {
        try (final ResultSet resultSet = connection.prepareStatement(sql).executeQuery()) {
            return rowMapper.mapRow(resultSet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> List<T> query(final String sql, final RowMapper<T> rowMapper) {
        try (final ResultSet resultSet = connection.prepareStatement(sql).executeQuery()) {
            final List<T> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(rowMapper.mapRow(resultSet));
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> List<T> queryList(final String sql, final RowsMapper<T> rowsMapper) {
        try (final ResultSet resultSet = connection.prepareStatement(sql).executeQuery()) {
            return rowsMapper.mapRow(resultSet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public long executeAndReturnGeneratedId(final String sql) {
        try (final PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.execute();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new RuntimeException("생성된 키를 가져오는 데 실패했습니다.");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
