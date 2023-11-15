package persistence.sql.metadata.association;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import persistence.sql.metadata.Table;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

public class OneToManyAssociation implements Association{
	private final FetchType fetchType;

	private final String joinColumnName;

	private final Class<?> type;

	private final Table table;

	public OneToManyAssociation(Field field) {
		this.fetchType = field.getDeclaredAnnotation(jakarta.persistence.OneToMany.class).fetch();
		this.joinColumnName = findJoinColumnName(field);
		this.type = findType(field);
		this.table = new Table(type);
	}

	@Override
	public Class<?> getType() {
		return type;
	}

	@Override
	public String getJoinColumnName() {
		return joinColumnName;
	}

	@Override
	public String getTableName() {
		return table.getName();
	}

	private String findJoinColumnName(Field field) {
		return field.getDeclaredAnnotation(JoinColumn.class).name();
	}

	private Class<?> findType(Field field) {
		ParameterizedType listType = (ParameterizedType) field.getGenericType();

		return (Class<?>) listType.getActualTypeArguments()[0];
	}
}
