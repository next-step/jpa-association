package pojo;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Transient;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class FieldInfos {

    private final List<Field> fieldList;

    public FieldInfos(Field[] fields) {
        if (Objects.isNull(fields)) {
            throw new IllegalArgumentException("fields 가 null 이어서는 안됩니다.");
        }
        this.fieldList = Arrays.stream(fields).filter(field -> !isTransientField(field)).collect(Collectors.toList());
    }

    public List<Field> getFieldDataList() {
        return fieldList;
    }

    public Field getIdField() {
        return fieldList.stream()
                .filter(this::isIdField)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Id 필드가 존재하지 않습니다."));
    }

    public List<Field> getColumnFields() {
        return fieldList.stream()
                .filter(field -> !isIdField(field) && !isJoinColumnField(field))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public List<Field> getIdAndColumnFields() {
        return fieldList.stream()
                .filter(field -> !isJoinColumnField(field))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public Optional<Field> getJoinColumnField() {
        return fieldList.stream()
                .filter(this::isJoinColumnField)
                .findFirst();
    }

    private boolean isIdField(Field field) {
        return field.isAnnotationPresent(Id.class);
    }

    private boolean isTransientField(Field field) {
        return field.isAnnotationPresent(Transient.class);
    }

    private boolean isJoinColumnField(Field field) {
        return field.isAnnotationPresent(JoinColumn.class);
    }
}
