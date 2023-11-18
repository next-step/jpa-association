package persistence.entity.loader;

import jdbc.JdbcTemplate;
import jdbc.mapper.RowMapper;
import persistence.sql.dml.DmlQueryBuilder;
import persistence.sql.metadata.EntityMetadata;

public class SingleEntityLoader<T> implements EntityLoader<T>{
	private final JdbcTemplate jdbcTemplate;

	private final EntityMetadata entityMetadata;

	private final DmlQueryBuilder dmlQueryBuilder;

	private final RowMapper<T> rowMapper;

	public SingleEntityLoader(JdbcTemplate jdbcTemplate, EntityMetadata entityMetadata, DmlQueryBuilder dmlQueryBuilder, RowMapper<T> rowMapper) {
		this.jdbcTemplate = jdbcTemplate;
		this.entityMetadata = entityMetadata;
		this.dmlQueryBuilder = dmlQueryBuilder;
		this.rowMapper = rowMapper;
	}

	@Override
	public T find(Long id) {
		String query = dmlQueryBuilder.findById(entityMetadata, id);
		return jdbcTemplate.queryForObject(query, rowMapper);
	}
}
