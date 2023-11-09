package persistence.entity.mapper;

import persistence.core.EntityColumn;
import persistence.core.EntityOneToManyColumn;
import persistence.util.ReflectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class EntityOneToManyMapper extends EntityColumnsMapper {

    private final List<EntityOneToManyColumn> oneToManyColumns;
    private final MapperCollectionStrategies collectionStrategies;

    private EntityOneToManyMapper(final List<EntityOneToManyColumn> oneToManyColumns) {
        this.oneToManyColumns = oneToManyColumns;
        this.collectionStrategies = MapperCollectionStrategies.getInstance();
    }

    public static EntityColumnsMapper of(final List<EntityOneToManyColumn> oneToManyColumns) {
        return new EntityOneToManyMapper(oneToManyColumns);
    }

    @Override
    public <T> void mapColumnsInternal(final ResultSet resultSet, final T instance) throws SQLException {
        for (final EntityOneToManyColumn oneToManyColumn : oneToManyColumns) {
            final Collection<Object> oneToManyFieldCollection = getOneToManyFieldCollection(instance, oneToManyColumn);
            final Class<?> joinColumnType = oneToManyColumn.getJoinColumnType();

            do {
                final Object innerInstance = ReflectionUtils.createInstance(joinColumnType);

                for (final EntityColumn associatedEntityColumn : oneToManyColumn.getAssociatedEntityColumns()) {
                    final String fieldName = associatedEntityColumn.getFieldName();
                    final String columnName = associatedEntityColumn.getNameWithAlias();
                    final Object object = resultSet.getObject(columnName);
                    ReflectionUtils.injectField(innerInstance, fieldName, object);
                }

                oneToManyFieldCollection.add(innerInstance);
            } while (resultSet.next());

            ReflectionUtils.injectField(instance, oneToManyColumn.getFieldName(), oneToManyFieldCollection);
        }

    }

    @SuppressWarnings("unchecked")
    private Collection<Object> getOneToManyFieldCollection(final Object instance, final EntityOneToManyColumn entityOneToManyColumn) {
        Collection<Object> oneToManyFieldCollection = (Collection<Object>) ReflectionUtils.getFieldValue(instance, entityOneToManyColumn.getFieldName());
        if (Objects.isNull(oneToManyFieldCollection)) {
            final Class<?> type = entityOneToManyColumn.getType();
            oneToManyFieldCollection = collectionStrategies.createCollectionBy(type);
        }
        return oneToManyFieldCollection;
    }

}
