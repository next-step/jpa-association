package persistence.sql.metadata;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Values {
	private final Map<String, Object> values;

	private Values(Map<String, Object> values) {
		this.values = values;
	}

	public static Values of(Field[] fields, Object entity) {
		return new Values(
				Arrays.stream(fields)
				.peek(x -> x.setAccessible(true))
				.collect(HashMap::new, (map, field) -> {
					try {
						map.put(findName(field), field.get(entity));
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				}, HashMap::putAll)
		);
	}

	public static Values from(Object entity) {
		return of(entity.getClass().getDeclaredFields(), entity);
	}

	public Object getValue(String name) {
		return values.get(name);
	}

	private static String findName(Field field) {
		if(!field.isAnnotationPresent(jakarta.persistence.Column.class)) {
			return field.getName();
		}

		jakarta.persistence.Column column = field.getDeclaredAnnotation(jakarta.persistence.Column.class);

		if("".equals(column.name())) {
			return field.getName();
		}

		return column.name();
	}
}
