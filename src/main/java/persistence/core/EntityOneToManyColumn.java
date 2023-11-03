package persistence.core;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import persistence.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;

public class EntityOneToManyColumn implements EntityAssociatedColumn {
    private static final String DELIMITER = "_";
    private final EntityColumn column;
    private final boolean isNotNull;
    private final boolean isInsertable;
    private final FetchType fetchType;
    private final Class<?> joinColumnType;
    private final String joinColumnName;

    public EntityOneToManyColumn(final Field field, final String tableName) {
        field.setAccessible(true);
        this.column = new EntityFieldColumn(field, tableName);
        this.isNotNull = initIsNotNull(field);
        this.isInsertable = initIsInsertable(field);
        this.fetchType = initFetchType(field);
        this.joinColumnType = initJoinColumnType(field);
        this.joinColumnName = initJoinColumnName(field);
    }

    private boolean initIsNotNull(final Field field) {
        final JoinColumn columnMetadata = field.getDeclaredAnnotation(JoinColumn.class);
        return Optional.ofNullable(columnMetadata)
                .map(column -> !column.nullable())
                .orElse(false);
    }

    private boolean initIsInsertable(final Field field) {
        final JoinColumn columnMetadata = field.getDeclaredAnnotation(JoinColumn.class);
        return Optional.ofNullable(columnMetadata)
                .map(JoinColumn::insertable)
                .orElse(true);
    }

    private FetchType initFetchType(final Field field) {
        final OneToMany columnMetadata = field.getDeclaredAnnotation(OneToMany.class);
        return Optional.ofNullable(columnMetadata)
                .map(OneToMany::fetch)
                .orElse(FetchType.LAZY);
    }

    private Class<?> initJoinColumnType(final Field field) {
        return ReflectionUtils.extractGenericClass(field);
    }

    private String initJoinColumnName(final Field field) {
        final JoinColumn columnMetadata = field.getDeclaredAnnotation(JoinColumn.class);
        return Optional.ofNullable(columnMetadata)
                .map(JoinColumn::name)
                .orElseGet(()->guessJoinColumnName(field));
    }

    private String guessJoinColumnName(final Field field) {
        final EntityMetadata<?> entityMetadata = EntityMetadataProvider.getInstance().getEntityMetadata(joinColumnType);
        return field.getName() + DELIMITER + entityMetadata.getIdColumnName();
    }

    @Override
    public FetchType getFetchType() {
        return this.fetchType;
    }

    @Override
    public Class<?> getJoinColumnType() {
        return this.joinColumnType;
    }

    @Override
    public String getTableName() {
        return this.column.getTableName();
    }

    @Override
    public String getName() {
        return this.joinColumnName;
    }

    @Override
    public boolean isNotNull() {
        return this.isNotNull;
    }

    @Override
    public Class<?> getType() {
        return this.column.getType();
    }

    @Override
    public boolean isStringValued() {
        return false;
    }

    @Override
    public int getStringLength() {
        return this.column.getStringLength();
    }

    @Override
    public String getFieldName() {
        return this.column.getFieldName();
    }

    @Override
    public boolean isInsertable() {
        return this.isInsertable;
    }

    @Override
    public boolean isAutoIncrement() {
        return false;
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        final EntityOneToManyColumn that = (EntityOneToManyColumn) object;
        return isNotNull == that.isNotNull && isInsertable == that.isInsertable && Objects.equals(column, that.column) && fetchType == that.fetchType && Objects.equals(joinColumnType, that.joinColumnType) && Objects.equals(joinColumnName, that.joinColumnName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(column, isNotNull, isInsertable, fetchType, joinColumnType, joinColumnName);
    }
}
