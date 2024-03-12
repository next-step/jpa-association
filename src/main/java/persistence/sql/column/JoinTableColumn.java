package persistence.sql.column;

import jakarta.persistence.*;
import persistence.entity.AssociationEntity;
import persistence.entity.OneToManyAssociationEntity;
import utils.CamelToSnakeCaseConverter;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class JoinTableColumn implements TableEntity {

    private static final String COMMA = ", ";
    private static final int MINIMUM_SIZE = 0;

    private final TableName name;

    private final AssociationEntity associationEntity;

    private final IdColumn idColumn;
    private final Columns columns;
    private final Class<?> clazz;

    public static <T> List<JoinTableColumn> fromOneToMany(Class<T> rootClass) {
        List<Field> associationField = getAssociationField(rootClass);
        return associationField.stream()
                .map(JoinTableColumn::new)
                .collect(Collectors.toList());
    }

    private JoinTableColumn(Field oneToManyField) {
        OneToManyAssociationEntity associationEntity = new OneToManyAssociationEntity(oneToManyField);
        Class<?> associatedClass = getAssociatedClass(oneToManyField);

        this.idColumn = new IdColumn(associatedClass.getDeclaredFields());
        this.name = new TableName(associatedClass);
        this.associationEntity = associationEntity;
        this.columns = new Columns(associatedClass.getDeclaredFields());
        this.clazz = associatedClass;

    }

    private Class<?> getAssociatedClass(Field oneToManyField) {
        ParameterizedType genericType = (ParameterizedType) oneToManyField.getGenericType();
        Type[] typeArguments = genericType.getActualTypeArguments();
        if (hasType(typeArguments)) {
            Type typeArgument = typeArguments[0];
            try {
                return Class.forName(typeArgument.getTypeName());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Class not found: " + typeArgument.getTypeName());
            }
        }
        return null;
    }

    private static boolean hasType(Type[] typeArguments) {
        return typeArguments != null && typeArguments.length > MINIMUM_SIZE;
    }

    private static <T> List<Field> getAssociationField(Class<T> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(it -> it.isAnnotationPresent(OneToMany.class))
                .collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return CamelToSnakeCaseConverter.convert(name.getValue());
    }

    public String getColumnDefinition() {
        String columnsDefinition = columns.getTableAndColumnDefinition(getName());
        String idColumnDefinition = idColumn.getTableAndColumnDefinition(getName());
        return idColumnDefinition + COMMA + columnsDefinition;
    }

    public AssociationEntity getAssociationEntity() {
        return associationEntity;
    }

    public Columns getColumns() {
        return columns;
    }

    public IdColumn getIdColumn() {
        return idColumn;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public <T> void setAssociationColumn(T rootEntity, T associatedEntity) {
        String joinFieldName = associationEntity.getJoinFieldName();
        Field associationField = null;
        try {
            associationField = rootEntity.getClass().getDeclaredField(joinFieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        associationField.setAccessible(true);
        Collection<T> associationCollection = getAssociationCollection(associationField, rootEntity);
        associationCollection.add(associatedEntity);
    }

    private <T> Collection<T> getAssociationCollection(Field associationField, T rootEntity) {
        try {
            Collection<T> associationCollection = (Collection<T>) associationField.get(rootEntity);
            if (associationCollection == null) {
                associationCollection = new ArrayList<>();
                associationField.set(rootEntity, associationCollection);
            }
            return associationCollection;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
