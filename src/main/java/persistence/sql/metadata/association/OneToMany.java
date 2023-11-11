package persistence.sql.metadata.association;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import persistence.sql.metadata.Table;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

public class OneToMany implements Association{
	private final FetchType fetchType;

	private final String joinColumnName;

	private final Class<?> type;

	private final Table table;

	public OneToMany(Field field) {
		this.fetchType = field.getDeclaredAnnotation(jakarta.persistence.OneToMany.class).fetch();
		this.joinColumnName = findJoinColumnName(field);
		this.type = findType(field);
		this.table = new Table(type);
	}

	@Override
	public String buildJoinClause() {
		return table.getName() + " ON " + table.getName() + "." + joinColumnName;
	}

	@Override
	public Class<?> getType() {
		return type;
	}

	private String findJoinColumnName(Field field) {
		return field.getDeclaredAnnotation(JoinColumn.class).name();
	}

	private Class<?> findType(Field field) {
		ParameterizedType listType = (ParameterizedType) field.getGenericType();

		return (Class<?>) listType.getActualTypeArguments()[0];
	}
}
