package persistence.sql.ddl;

import persistence.dialect.DbType;
import persistence.sql.metadata.Column;
import persistence.sql.metadata.EntityMetadata;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class H2DdlQueryBuilder extends DdlQueryBuilder{
	private static final String CREATE_TABLE_COMMAND = "CREATE TABLE %s(%s);";

	private static final String DROP_TABLE_COMMAND = "DROP TABLE %s IF EXISTS;";

	private static final String PRIMARY_KEY_CONSTRAINT = " PRIMARY KEY";

	private static final String NOT_NULL_CONSTRAINT = " NOT NULL";


	public H2DdlQueryBuilder() {
		super(DbType.H2);
	}

	public String createQuery(EntityMetadata entityMetadata) {
		return format(CREATE_TABLE_COMMAND, entityMetadata.getTableName(), buildColumnsWithConstraint(entityMetadata.getColumns()));
	}

	public String dropQuery(EntityMetadata entityMetadata) {
		return format(DROP_TABLE_COMMAND, entityMetadata.getTableName());
	}

	private String buildColumnsWithConstraint(List<Column> columns) {
		return columns.stream()
				.filter(Column::checkPossibleToBeCreate)
				.map(x -> new StringBuilder()
						.append(x.getName() + " " + dialect.getColumnType(x.getType()))
						.append(getNotNullConstraint(x.isNullable()))
						.append(dialect.getGeneratedStrategy(x.getGeneratedType()))
						.append(getPrimaryKeyConstraint(x.isPrimaryKey()))
						.toString())
				.collect(Collectors.joining(", "));
	}

	private String getNotNullConstraint(boolean isNullable) {
		if(!isNullable) {
			return NOT_NULL_CONSTRAINT;
		}

		return "";
	}

	private String getPrimaryKeyConstraint(boolean isPrimaryKey) {
		if(isPrimaryKey) {
			return PRIMARY_KEY_CONSTRAINT;
		}

		return "";
	}
}
