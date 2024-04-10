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
    private final String entityName;
    private final FieldInfos fieldInfos;
    private final Field joinField;
    private final boolean lazy;

    public EntityJoinMetaData(Class<?> clazz, Field field) {
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalStateException("Entity 클래스가 아닙니다.");
        }
        this.clazz = clazz;
        this.entityName = getEntityNameInfo();
        this.fieldInfos = new FieldInfos(clazz.getDeclaredFields());
        this.joinField = field;
        this.lazy = isLazy(field);
    }

    public String getEntityName() {
        return entityName;
    }

    public boolean isLazy() {
        return lazy;
    }

    private String getEntityNameInfo() {
        if (clazz.isAnnotationPresent(Table.class) && !isBlankOrEmpty(clazz.getAnnotation(Table.class).name())) {
            return clazz.getAnnotation(Table.class).name();
        }

        return clazz.getSimpleName().toLowerCase();
    }

    private boolean isLazy(Field field) {
        //일단 OneToMany 만 고려
        return !field.getAnnotation(OneToMany.class).fetch().equals(FetchType.EAGER);
    }

    public String getJoinColumnNameInfo(IdField entityMetaDataIdField) {
        if (joinField.isAnnotationPresent(JoinColumn.class) && !isBlankOrEmpty(joinField.getAnnotation(JoinColumn.class).name())) {
            return joinField.getAnnotation(JoinColumn.class).name();
        }

        return getEntityNameInfo() + UNDER_SCORE + entityMetaDataIdField.getFieldNameData();
    }

    public List<FieldName> getFieldNamesInfo() {
        return fieldInfos.getIdAndColumnFields().stream()
                .map(FieldName::new)
                .collect(Collectors.toList());
    }
}
