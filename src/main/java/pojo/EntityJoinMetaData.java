package pojo;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

import static constants.CommonConstants.UNDER_SCORE;
import static utils.StringUtils.isBlankOrEmpty;

public class EntityJoinMetaData {

    private final Class<?> clazz;
    private final Object entity;
    private final String entityName;
    private final String joinColumnName;
    private final List<FieldName> fieldNames;
    private final boolean lazy;

    public EntityJoinMetaData(Class<?> clazz, Object entity, Field field, IdField entityMetaDataIdField) {
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalStateException("Entity 클래스가 아닙니다.");
        }
        this.clazz = clazz;
        this.entity = entity;
        this.entityName = getEntityNameInfo();
        this.joinColumnName = getJoinColumnNameInfo(field, entityMetaDataIdField);
        this.fieldNames = getFieldNamesInfo();
        this.lazy = isLazy(field);
    }

    public String getEntityName() {
        return entityName;
    }

    public String getJoinColumnName() {
        return joinColumnName;
    }

    public List<FieldName> getFieldNames() {
        return fieldNames;
    }

    public boolean isLazy() {
        return lazy;
    }

    private String getEntityNameInfo() {
        return clazz.isAnnotationPresent(Table.class) && !isBlankOrEmpty(clazz.getAnnotation(Table.class).name())
                ? clazz.getAnnotation(Table.class).name() : clazz.getSimpleName().toLowerCase();
    }

    public String getJoinColumnNameInfo(Field field, IdField entityMetaDataIdField) {
        return field.isAnnotationPresent(JoinColumn.class) && !isBlankOrEmpty(field.getAnnotation(JoinColumn.class).name())
                ? field.getAnnotation(JoinColumn.class).name() : getEntityNameInfo() + UNDER_SCORE + entityMetaDataIdField.getFieldNameData();
    }

    private List<FieldName> getFieldNamesInfo() {
        return new FieldInfos(clazz.getDeclaredFields()).getIdAndColumnFields().stream()
                .map(FieldName::new)
                .collect(Collectors.toList());
    }

    private boolean isLazy(Field field) {
        //일단 OneToMany 만 고려
        return !field.getAnnotation(OneToMany.class).fetch().equals(FetchType.EAGER);
    }
}
