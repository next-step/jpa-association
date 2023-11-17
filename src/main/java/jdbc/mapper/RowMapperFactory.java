package jdbc.mapper;

import persistence.sql.metadata.EntityMetadata;

public class RowMapperFactory<T> {
	public RowMapperFactory() {
	}

	public RowMapper<T> create(EntityMetadata entityMetadata, Class<T> clazz) {
		if(entityMetadata.hasAssociation()) {
			return new OneToManyEntityMapper<>(clazz);
		}

		return new SingleEntityMapper<>(clazz);
	}
}
