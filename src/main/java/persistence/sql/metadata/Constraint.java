package persistence.sql.metadata;

import jakarta.persistence.*;
import jakarta.persistence.Column;

import java.lang.reflect.Field;

public class Constraint {
    private final boolean isPrimaryKey;

    private final boolean isNullable;

    private final boolean isTransient;

    private final String generatedType;

    public Constraint(Field field) {
        this.isPrimaryKey = findIsPrimaryKey(field);
        this.isNullable = findIsNullable(field);
        this.isTransient = field.isAnnotationPresent(Transient.class);
        this.generatedType = findGeneratedType(field);
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public boolean isNullable() {
        return isNullable;
    }

    public boolean isTransient() {
        return isTransient;
    }

    public String getGeneratedType() {
        return generatedType;
    }

    private boolean findIsPrimaryKey(Field field) {
        return field.isAnnotationPresent(Id.class);
    }

    private boolean findIsNullable(Field field) {
        if(!field.isAnnotationPresent(Column.class)) {
            return true;
        }

        Column column = field.getDeclaredAnnotation(Column.class);

        return column.nullable();
    }

    private String findGeneratedType(Field field) {
        if(!field.isAnnotationPresent(GeneratedValue.class)) {
            return "";
        }

        GenerationType strategy = field.getAnnotation(GeneratedValue.class).strategy();

        return strategy.name();
    }
}
