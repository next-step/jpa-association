package persistence.sql.column;

import jakarta.persistence.*;
import persistence.entity.AssociationEntity;
import persistence.entity.OneToManyAssociationEntity;
import utils.CamelToSnakeCaseConverter;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JoinTableColumn implements TableEntity {

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
        OneToMany oneToMany = oneToManyField.getDeclaredAnnotation(OneToMany.class);

        OneToManyAssociationEntity associationEntity = new OneToManyAssociationEntity(
                new JoinEntityColumn(oneToManyField),
                oneToMany.fetch()
        );

        Class<?> associatedClass = getAssociatedClass(oneToManyField);

        this.idColumn = new IdColumn(associatedClass.getDeclaredFields());
        this.name = new TableName(associatedClass);
        this.associationEntity = associationEntity;
        this.columns = new Columns(associatedClass.getDeclaredFields());
        this.clazz = associatedClass;

    }

    private static Class<?> getAssociatedClass(Field oneToManyField) {
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

}
