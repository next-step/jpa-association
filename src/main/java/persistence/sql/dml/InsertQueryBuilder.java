package persistence.sql.dml;

import persistence.sql.metadata.Column;
import persistence.sql.metadata.EntityMetadata;
import persistence.sql.metadata.Values;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static persistence.sql.util.QueryUtil.convertValueToString;

public class InsertQueryBuilder {
	private static final String INSERT_COMMAND = "INSERT INTO %s (%s) VALUES (%s);";

	public String build(EntityMetadata entityMetadata, Values values){
		if(entityMetadata == null) {
			throw new IllegalArgumentException("등록하려는 객체가 NULL 값이 될 수 없습니다.");
		}

		return format(INSERT_COMMAND,
				entityMetadata.getTableName(),
				buildColumnsClause(entityMetadata.getColumns()),
				buildValueClause(entityMetadata.getColumns(), values)
		);
	}

	private String buildColumnsClause(List<Column> columns) {
		return columns.stream()
				.filter(Column::checkPossibleToBeValue)
				.map(Column::getName)
				.collect(Collectors.joining(", "));
	}

	private String buildValueClause(List<Column> columns, Values values) {
		return columns.stream()
				.filter(Column::checkPossibleToBeValue)
				.map(x -> convertValueToString(values.getValue(x.getName())))
				.collect(Collectors.joining(", "));
	}
}
