package persistence.sql.mapping;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import persistence.entity.PersistentCollection;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

public class OneToManyData {
    private final TableData referenceTable;
    private final Class<?> referenceEntityClazz;
    private final String joinColumnName;
    private final FetchType fetchType;
    private final Field field;

    private OneToManyData(String joinColumnName, FetchType fetchType, Class<?> referenceEntityClazz, Field field) {
        this.joinColumnName = joinColumnName;
        this.fetchType = fetchType;
        this.referenceEntityClazz = referenceEntityClazz;
        this.referenceTable = TableData.from(referenceEntityClazz);
        this.field = field;
    }

    public static OneToManyData from(Field field) {
        ParameterizedType genericType = (ParameterizedType) field.getGenericType();
        Class<?> referenceClazz = (Class<?>) genericType.getActualTypeArguments()[0];

        OneToMany oneToMany = field.getAnnotation(OneToMany.class);
        JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);

        return new OneToManyData(
                joinColumn.name(),
                oneToMany.fetch(),
                referenceClazz,
                field
        );
    }

    public String getJoinColumnName() {
        return String.format("%s.%s", referenceTable.getName(), joinColumnName);
    }

    public String getJoinTableName() {
        return referenceTable.getName();
    }

    public boolean isLazyLoad() {
        return fetchType == FetchType.LAZY;
    }

    public Class<?> getReferenceEntityClazz() {
        return referenceEntityClazz;
    }

    public Field getField() {
        return field;
    }

    public <T> void setCollectionToField(Object entity, PersistentCollection<T> collection) {
        field.setAccessible(true);
        try {
            field.set(entity, collection);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isEagerLoad() {
        return fetchType == FetchType.EAGER;
    }
}
