package persistence.sql.metadata.association;

public interface Association {
	Class<?> getType();

	String getJoinColumnName();

	String getTableName();
}
