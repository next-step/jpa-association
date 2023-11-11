package persistence.sql.metadata.association;

public interface Association {
	String buildJoinClause();

	Class<?> getType();
}
