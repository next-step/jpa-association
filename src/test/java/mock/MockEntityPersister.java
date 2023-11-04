package mock;

import persistence.entity.persister.EntityPersister;

import java.sql.SQLException;

public class MockEntityPersister extends EntityPersister {
    public MockEntityPersister(final Class<?> clazz) throws SQLException {
        super(clazz, new MockDmlGenerator(), new MockJdbcTemplate());
    }

}
