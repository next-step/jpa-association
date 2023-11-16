package persistence.sql.dml;

import persistence.sql.metadata.Column;
import persistence.sql.metadata.EntityMetadata;

import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static persistence.sql.util.QueryUtil.convertColumnToString;
import static persistence.sql.util.QueryUtil.convertValueToString;

public class ClauseBuilder {
	private static final String JOIN_CLAUSE = " JOIN %s ON %s = %s";

	private static final String WHERE_CLAUSE = " WHERE %s";

	protected String JoinClauses(EntityMetadata entityMetadata) {
		return entityMetadata.getColumns().stream()
				.map(Column::getAssociation)
				.filter(Objects::nonNull)
				.map(x -> format(JOIN_CLAUSE, x.getTableName(), convertColumnToString(x.getTableName(), x.getJoinColumnName()), convertColumnToString(entityMetadata.getTableName(), entityMetadata.getIdName())))
				.collect(Collectors.joining("/n"));
	}

	protected String wherePKClause(EntityMetadata entityMetadata, Object idValue) {
		return format(WHERE_CLAUSE, convertColumnToString(entityMetadata.getTableName(), entityMetadata.getIdName()) + " = " +convertValueToString(idValue));
	}
}
