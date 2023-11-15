package persistence.sql.dml;

import persistence.sql.metadata.Column;
import persistence.sql.metadata.EntityMetadata;
import persistence.sql.metadata.Values;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static persistence.sql.util.QueryUtil.convertValueToString;

public class UpdateQueryBuilder {
	private static final String UPDATE_COMMAND = "UPDATE %s SET %s;";

	private final ClauseBuilder clauseBuilder;

	public UpdateQueryBuilder(ClauseBuilder clauseBuilder) {
		this.clauseBuilder = clauseBuilder;
	}

	public String build(EntityMetadata entityMetadata, Values values, Object idValue) {
		return format(UPDATE_COMMAND,
				entityMetadata.getTableName(),
				buildSetClause(entityMetadata.getColumns(), values) + clauseBuilder.wherePKClause(entityMetadata, idValue)
		);
	}

	private String buildSetClause(List<Column> columns, Values values) {
		return columns.stream()
				.filter(Column::checkPossibleToBeValue)
				.map(x -> x.getName() + " = " + convertValueToString(values.getValue(x.getName())))
				.collect(Collectors.joining(", "));
	}
}
