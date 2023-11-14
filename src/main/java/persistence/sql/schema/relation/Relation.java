package persistence.sql.schema.relation;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import persistence.sql.dialect.ColumnType;
import persistence.sql.schema.meta.EntityClassMappingMeta;
import persistence.sql.schema.meta.TableMeta;

public class Relation {

    private final TableMeta joinTable;
    private final String joinColumnName;
    private final FetchType fetchType;
    private final boolean hasRelation;

    private Relation(TableMeta joinTable, String joinColumnName, FetchType fetchType) {
        this.joinTable = joinTable;
        this.joinColumnName = joinColumnName;
        this.fetchType = fetchType;
        this.hasRelation = true;
    }

    private Relation() {
        this.joinTable = null;
        this.joinColumnName = null;
        this.fetchType = null;
        this.hasRelation = false;
    }

    public static Relation of(Field field, ColumnType columnType) {
        if (field.isAnnotationPresent(OneToMany.class)) {
            return createOneToManyRelation(field, columnType);
        }

        if (field.isAnnotationPresent(ManyToOne.class)) {
            return createManyToOneRelation(field, columnType);
        }

        return new Relation();
    }

    public Class<?> getJoinTableType() {
        return this.joinTable.getType();
    }

    public boolean hasRelation() {
        return hasRelation;
    }

    public boolean isLazyLoading() {
        return fetchType == FetchType.LAZY;
    }

    public String getJoinColumnName() {
        return joinColumnName;
    }

    private static Relation createManyToOneRelation(Field field, ColumnType columnType) {
        final Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            final Type actualTypeArgument = getParameterizedType((ParameterizedType) genericType);
            EntityClassMappingMeta entityClassMappingMeta = EntityClassMappingMeta.of(actualTypeArgument.getClass(), columnType);
            final ManyToOne annotation = field.getAnnotation(ManyToOne.class);

            return new Relation(
                entityClassMappingMeta.getTableMeta(),
                extractJoinColumn(field),
                annotation.fetch()
            );
        }

        return new Relation();
    }

    private static Relation createOneToManyRelation(Field field, ColumnType columnType) {
        final Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            final Type actualTypeArgument = getParameterizedType((ParameterizedType) genericType);
            EntityClassMappingMeta entityClassMappingMeta = EntityClassMappingMeta.of((Class<?>) actualTypeArgument, columnType);
            final OneToMany annotation = field.getAnnotation(OneToMany.class);

            return new Relation(
                entityClassMappingMeta.getTableMeta(),
                extractJoinColumn(field),
                annotation.fetch()
            );
        }

        return new Relation();
    }

    private static Type getParameterizedType(ParameterizedType genericType) {
        final Type[] actualTypeArguments = genericType.getActualTypeArguments();
        return actualTypeArguments[0];
    }

    private static String extractJoinColumn(Field field) {
        if (field.isAnnotationPresent(JoinColumn.class)) {
            final JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
            return joinColumn.name();
        }

        return null;
    }
}
