package persistence.sql.dml;

import persistence.sql.metadata.EntityMetadata;
import persistence.sql.metadata.Values;

public class DmlQueryBuilder {
	private final InsertQueryBuilder insertQueryBuilder;

	private final DeleteQueryBuilder deleteQueryBuilder;

	private final UpdateQueryBuilder updateQueryBuilder;

	private final SelectQueryBuilder selectQueryBuilder;

	public DmlQueryBuilder() {
		ClauseBuilder clauseBuilder = new ClauseBuilder();
		this.insertQueryBuilder = new InsertQueryBuilder();
		this.deleteQueryBuilder = new DeleteQueryBuilder(clauseBuilder);
		this.updateQueryBuilder = new UpdateQueryBuilder(clauseBuilder);
		this.selectQueryBuilder = new SelectQueryBuilder(clauseBuilder);
	}

	public String insertQuery(EntityMetadata entityMetadata, Values values){
		return insertQueryBuilder.build(entityMetadata, values);
	}

	public String deleteQuery(EntityMetadata entityMetadata, Object idValue) {
		return deleteQueryBuilder.build(entityMetadata, idValue);
	}

	public String updateQuery(EntityMetadata entityMetadata, Values values, Object idValue) {
		return updateQueryBuilder.build(entityMetadata, values, idValue);
	}

	public String findById(EntityMetadata entityMetadata, Object idValue) {
		return selectQueryBuilder.findById(entityMetadata, idValue);
	}
}
