package persistence.sql.column;

import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.AssociationEntity;
import persistence.entity.JoinEntityColumn;
import persistence.entity.OneToManyAssociationEntity;
import persistence.sql.type.TableName;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;

public class JoinTableColumn implements TableEntity {
    private static Logger log = LoggerFactory.getLogger(JoinTableColumn.class);

    private final TableName name;

    private final AssociationEntity associationEntity;

    private final IdColumn idColumn;
    private final Columns columns;

    public static <T> JoinTableColumn fromOneToMany(Class<T> rootClass) {
        Optional<Field> associationField = getAssociationField(rootClass);
        return associationField.map(JoinTableColumn::new)
                .orElse(null);
    }

    private JoinTableColumn(Field oneToManyField) {
        OneToMany oneToMany = oneToManyField.getDeclaredAnnotation(OneToMany.class);

        OneToManyAssociationEntity associationEntity = new OneToManyAssociationEntity(
                new JoinEntityColumn(oneToManyField),
                oneToMany.mappedBy(),
                oneToMany.fetch()
        );

        Class<?> associatedClass = getAssociatedClass(oneToManyField);

        this.idColumn = new IdColumn(associatedClass.getDeclaredFields());
        this.name = new TableName(associatedClass);
        this.associationEntity = associationEntity;
        this.columns = new Columns(associatedClass.getDeclaredFields());
    }

    private static Class<?> getAssociatedClass(Field oneToManyField) {
        ParameterizedType genericType = (ParameterizedType) oneToManyField.getGenericType();
        Type[] typeArguments = genericType.getActualTypeArguments();
        if (hasType(typeArguments)) {
            Type typeArgument = typeArguments[0];
            try {
                return Class.forName(typeArgument.getTypeName());
            } catch (ClassNotFoundException e) {
                log.info("Class not found: " + typeArgument.getTypeName(), e);
            }
        }
        return null;
    }

    private static boolean hasType(Type[] typeArguments) {
        return typeArguments != null && typeArguments.length > 0;
    }

    private static <T> Optional<Field> getAssociationField(Class<T> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(it -> it.isAnnotationPresent(OneToMany.class))
                .findFirst();
    }

    @Override
    public String getName() {
        return changeSnakeCase(name.getValue());
    }

    private String changeSnakeCase(String name) {
        StringBuilder tableName = new StringBuilder();
        for (int index = 0; index < name.length(); index++) {
            char ch = name.charAt(index);
            addUnderScore(index, ch, tableName);
            tableName.append(Character.toLowerCase(ch));
        }
        return tableName.toString();
    }

    private void addUnderScore(int index, char ch, StringBuilder tableName) {
        if (index > 0 && Character.isUpperCase(ch)) {
            tableName.append("_");
        }
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
}
