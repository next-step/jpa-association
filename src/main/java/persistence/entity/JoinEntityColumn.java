package persistence.entity;

import jakarta.persistence.JoinColumn;

import java.lang.reflect.Field;

public class JoinEntityColumn {

    private final String name;

    public JoinEntityColumn(Field field) {
        this.name = getJoinField(field);
    }

    private String getJoinField(Field joinField) {
        JoinColumn joinColumn = joinField.getDeclaredAnnotation(JoinColumn.class);
        if (joinColumn != null && !joinColumn.name().isEmpty()) {
            return joinColumn.name();
        }
        return joinField.getName();
    }

    public String getName() {
        return name;
    }
}
