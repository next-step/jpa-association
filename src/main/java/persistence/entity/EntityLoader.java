package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.mapper.RowMapperFactory;
import persistence.sql.dml.DmlQueryBuilder;
import persistence.sql.metadata.EntityMetadata;

public class EntityLoader {
	private final JdbcTemplate jdbcTemplate;

	private final EntityMetadata entityMetadata;

	private final DmlQueryBuilder dmlQueryBuilder;

	public EntityLoader(JdbcTemplate jdbcTemplate, Class<?> clazz, DmlQueryBuilder dmlQueryBuilder) {
		this.jdbcTemplate = jdbcTemplate;
		this.entityMetadata = new EntityMetadata(clazz);
		this.dmlQueryBuilder = dmlQueryBuilder;
	}

	public <T> T find(Class<T> clazz, Long id) {
		String query = dmlQueryBuilder.findById(entityMetadata, id);
		return jdbcTemplate.queryForObject(query, new RowMapperFactory<T>().create(entityMetadata, clazz));
	}
}
