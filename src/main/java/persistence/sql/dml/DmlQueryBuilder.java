package persistence.sql.dml;

import persistence.sql.metadata.Column;
import persistence.sql.metadata.EntityMetadata;
import persistence.sql.metadata.Values;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class DmlQueryBuilder {
	private static final String INSERT_COMMAND = "INSERT INTO %s (%s) VALUES (%s);";

	private static final String DELETE_COMMAND = "DELETE FROM %s;";

	private static final String UPDATE_COMMAND = "UPDATE %s SET %s;";

	private static final String SELECT_COMMAND = "SELECT %s FROM %s;";

	private static final String WHERE_CLAUSE = " WHERE %s";

	private static final String JOIN_CLAUSE = " JOIN %s = %s.%s";

	private DmlQueryBuilder() {

	}

	public static DmlQueryBuilder build() {
		return new DmlQueryBuilder();
	}

	public String insertQuery(EntityMetadata entityMetadata, Values values){
		if(entityMetadata == null) {
			throw new IllegalArgumentException("등록하려는 객체가 NULL 값이 될 수 없습니다.");
		}

		return format(INSERT_COMMAND,
				entityMetadata.getTableName(),
				buildColumnsClause(entityMetadata.getColumns()),
				buildValueClause(entityMetadata.getColumns(), values)
		);
	}

	public String deleteQuery(EntityMetadata entityMetadata, Object idValue) {
		return format(DELETE_COMMAND,
				entityMetadata.getTableName() + wherePKClause(entityMetadata, idValue)
		);
	}

	public String updateQuery(EntityMetadata entityMetadata, Values values, Object idValue) {
		return format(UPDATE_COMMAND,
				entityMetadata.getTableName(),
				buildSetClause(entityMetadata.getColumns(), values) + wherePKClause(entityMetadata, idValue)
		);
	}

	public String selectQuery(Class<?> clazz, Object idValue) {
		EntityMetadata entityMetadata = new EntityMetadata(clazz);

		return format(SELECT_COMMAND, "*", entityMetadata.getTableName() + joinClause(entityMetadata) + wherePKClause(entityMetadata, idValue));
	}

	public String wherePKClause(EntityMetadata entityMetadata, Object idValue) {
		return format(WHERE_CLAUSE, entityMetadata.getTableName() + "." + entityMetadata.getIdName() + " = " +convertValueToString(idValue));
	}

	public String joinClause(EntityMetadata entityMetadata) {
		return Arrays.stream(buildJoinClauses(entityMetadata))
				.map(x -> format(JOIN_CLAUSE, x, entityMetadata.getTableName(), entityMetadata.getIdName()))
				.collect(Collectors.joining("/n"));
	}

	public String buildColumnsClause(List<Column> columns) {
		return columns.stream()
				.filter(Column::checkPossibleToBeValue)
				.map(Column::getName)
				.collect(Collectors.joining(", "));
	}

	public String buildValueClause(List<Column> columns, Values values) {
		return columns.stream()
				.filter(Column::checkPossibleToBeValue)
				.map(x -> convertValueToString(values.getValue(x.getName())))
				.collect(Collectors.joining(","));
	}

	public String buildSetClause(List<Column> columns, Values values) {
		return columns.stream()
				.filter(Column::checkPossibleToBeValue)
				.map(x -> x.getName() + " = " + convertValueToString(values.getValue(x.getName())))
				.collect(Collectors.joining(", "));
	}

	public String[] buildJoinClauses(EntityMetadata entityMetadata) {
		return entityMetadata.getColumns().stream()
				.map(Column::getAssociation)
				.filter(Objects::nonNull)
				.map(x -> x.getTableName() + " ON " + x.getTableName() + "." + x.getJoinColumnName())
				.toArray(String[]::new);
	}

	private String convertValueToString(Object value) {
		if(value.getClass().equals(String.class)) {
			return "'" + value + "'";
		}

		return String.valueOf(value);
	}
}
