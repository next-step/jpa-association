package mock;

import jdbc.JdbcTemplate;
import jdbc.RowMapper;
import org.h2.tools.SimpleResultSet;

import java.util.ArrayList;
import java.util.List;

public class MockJdbcTemplate extends JdbcTemplate {
    private final SimpleResultSet rs;

    public MockJdbcTemplate(){
        this(null);
    }

    public MockJdbcTemplate(final SimpleResultSet rs) {
        super(new MockDatabaseServer().getConnection());
        this.rs = rs;
    }

    @Override
    public <T> List<T> query(final String sql, final RowMapper<T> rowMapper) {
        try (rs) {
            final List<T> result = new ArrayList<>();
            while (rs.next()) {
                result.add(rowMapper.mapRow(rs));
            }
            return result;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
