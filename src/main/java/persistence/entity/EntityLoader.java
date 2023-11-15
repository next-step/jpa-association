package persistence.entity;

import jdbc.EntityMapper;
import jdbc.JdbcTemplate;
import persistence.sql.dml.DmlQueryBuilder;
import persistence.sql.metadata.EntityMetadata;

public class EntityLoader {
	private final JdbcTemplate jdbcTemplate;

	private final EntityMetadata entityMetadata;

	public EntityLoader(JdbcTemplate jdbcTemplate, Class<?> clazz) {
		this.jdbcTemplate = jdbcTemplate;
		this.entityMetadata = new EntityMetadata(clazz);
	}

	public <T> T find(Class<T> clazz, Long id) {
		String query = DmlQueryBuilder.build().selectQuery(entityMetadata, id);
		return jdbcTemplate.queryForObject(query, new EntityMapper<>(clazz));
	}
}
