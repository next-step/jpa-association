package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DmlQueryBuilder;
import persistence.sql.metadata.EntityMetadata;
import persistence.sql.metadata.Values;

import java.lang.reflect.Field;

public class EntityPersister {
	private final JdbcTemplate jdbcTemplate;

	private final EntityMetadata entityMetadata;

	public EntityPersister(JdbcTemplate jdbcTemplate, Class<?> clazz) {
		this.jdbcTemplate = jdbcTemplate;
		this.entityMetadata = new EntityMetadata(clazz);
	}

	public Object insert(Object entity) {
		String query = DmlQueryBuilder.build().insertQuery(entityMetadata, Values.from(entity));
		return jdbcTemplate.executeUpdate(query);
	}

	public void delete(Object entity) {
		String query = DmlQueryBuilder.build().deleteQuery(entityMetadata, getIdValue(entity));
        jdbcTemplate.execute(query);
	}

	public void update(Field[] fields, Object entity) {
		String query = DmlQueryBuilder.build().updateQuery(entityMetadata, Values.of(fields, entity), getIdValue(entity));
		jdbcTemplate.execute(query);
	}

	public Object getIdValue(Object entity) {
		return Values.from(entity).getValue(entityMetadata.getIdName());
	}
}
