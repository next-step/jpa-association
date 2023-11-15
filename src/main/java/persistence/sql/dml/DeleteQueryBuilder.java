package persistence.sql.dml;

import persistence.sql.metadata.EntityMetadata;

import static java.lang.String.format;

public class DeleteQueryBuilder {
	private static final String DELETE_COMMAND = "DELETE FROM %s;";

	private final ClauseBuilder clauseBuilder;

	public DeleteQueryBuilder(ClauseBuilder clauseBuilder) {
		this.clauseBuilder = clauseBuilder;
	}

	public String build(EntityMetadata entityMetadata, Object idValue) {
		return format(DELETE_COMMAND,
				entityMetadata.getTableName() + clauseBuilder.wherePKClause(entityMetadata, idValue)
		);
	}
}
