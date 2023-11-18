package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DmlQueryBuilder;
import persistence.sql.metadata.EntityMetadata;
import persistence.sql.metadata.Values;

import java.lang.reflect.Field;

public class EntityPersister {
	private final JdbcTemplate jdbcTemplate;

	private final EntityMetadata entityMetadata;

	private final DmlQueryBuilder dmlQueryBuilder;

	public EntityPersister(JdbcTemplate jdbcTemplate, EntityMetadata entityMetadata, DmlQueryBuilder dmlQueryBuilder) {
		this.jdbcTemplate = jdbcTemplate;
		this.entityMetadata = entityMetadata;
		this.dmlQueryBuilder = dmlQueryBuilder;
	}

	public Object insert(Object entity) {
		String query = dmlQueryBuilder.insertQuery(entityMetadata, Values.from(entity));
		return jdbcTemplate.executeUpdate(query);
	}

	public void delete(Object entity) {
		String query = dmlQueryBuilder.deleteQuery(entityMetadata, getIdValue(entity));
        jdbcTemplate.execute(query);
	}

	public void update(Field[] fields, Object entity) {
		String query = dmlQueryBuilder.updateQuery(entityMetadata, Values.of(fields, entity), getIdValue(entity));
		jdbcTemplate.execute(query);
	}

	public Object getIdValue(Object entity) {
		return Values.from(entity).getValue(entityMetadata.getIdName());
	}
}
