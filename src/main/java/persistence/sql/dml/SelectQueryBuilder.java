package persistence.sql.dml;

import persistence.sql.metadata.Column;
import persistence.sql.metadata.EntityMetadata;

import java.util.stream.Collectors;

import static java.lang.String.format;
import static persistence.sql.util.QueryUtil.convertColumnToString;

public class SelectQueryBuilder {
	private static final String SELECT_COMMAND = "SELECT %s FROM %s;";

	private final ClauseBuilder clauseBuilder;

	public SelectQueryBuilder(ClauseBuilder clauseBuilder) {
		this.clauseBuilder = clauseBuilder;
	}

	public String findById(EntityMetadata entityMetadata, Object idValue) {
		return format(SELECT_COMMAND,
				buildColumnsClause(entityMetadata),
				entityMetadata.getTableName() + clauseBuilder.JoinClauses(entityMetadata) + clauseBuilder.wherePKClause(entityMetadata, idValue));
	}

	private String buildColumnsClause(EntityMetadata entityMetadata) {
		String result = entityMetadata.getColumns().stream()
				.filter(Column::checkPossibleToBeCreate)
				.map(x -> convertColumnToString(entityMetadata.getTableName(), x.getName()))
				.collect(Collectors.joining(", "));

		if(entityMetadata.hasAssociation()) {
			result += ", " + buildColumnsClause(new EntityMetadata(entityMetadata.getAssociatedColumn().getAssociation().getType()));
		}

		return result;
	}
}
