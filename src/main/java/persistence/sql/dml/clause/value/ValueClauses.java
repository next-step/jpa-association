package persistence.sql.dml.clause.value;

import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ValueClauses {
    private final List<ValueClause> values;
    public ValueClauses(Object entity) {
        List<Field> fields = Arrays.stream(entity.getClass().getDeclaredFields()).collect(Collectors.toList());
        this.values = fields.stream()
                .filter(ValueClauses::isColumn)
                .map(field -> new ValueClause(field, entity)).collect(Collectors.toList());
    }

    private static boolean isColumn(Field field) {
        List<Class<? extends Annotation>> invalidAnnotations = List.of(Transient.class, Id.class, OneToMany.class);

        return invalidAnnotations.stream().noneMatch(field::isAnnotationPresent);
    }

    public List<String> getQueries() {
        return this.values.stream().map(ValueClause::value).collect(Collectors.toList());
    }

    public List<String> values() {
        return this.values.stream().map(ValueClause::value).collect(Collectors.toList());
    }
}
