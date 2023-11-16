package persistence.sql.metadata.association;

import java.lang.reflect.Field;

public class ManyToOneAssociation implements Association{
	public ManyToOneAssociation(Field field) {
	}

	@Override
	public Class<?> getType() {
		return null;
	}

	@Override
	public String getJoinColumnName() {
		return null;
	}

	@Override
	public String getTableName() {
		return null;
	}
}
