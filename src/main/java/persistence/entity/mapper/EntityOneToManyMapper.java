package persistence.entity.mapper;

import persistence.core.*;
import persistence.util.ReflectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class EntityOneToManyMapper implements EntityColumnsMapper {

    private final List<EntityOneToManyColumn> oneToManyColumns;
    private final MapperCollectionStrategies collectionStrategies;

    public EntityOneToManyMapper(final List<EntityOneToManyColumn> oneToManyColumns) {
        this.oneToManyColumns = oneToManyColumns;
        this.collectionStrategies = MapperCollectionStrategies.getInstance();
    }

    public <T> void mapColumns(final ResultSet resultSet, final T instance) throws SQLException {
        for (final EntityOneToManyColumn column : oneToManyColumns) {
            final Collection<Object> oneToManyFieldCollection = getOneToManyFieldCollection(instance, column);
            final Class<?> joinColumnType = column.getJoinColumnType();
            final EntityMetadata<?> innerEntityMetadata = EntityMetadataProvider.getInstance().getEntityMetadata(joinColumnType);

            do {
                final Object innerInstance = ReflectionUtils.createInstance(joinColumnType);

                for (final EntityColumn innerEntityColumn : innerEntityMetadata.getColumns()) {
                    final String fieldName = innerEntityColumn.getFieldName();
                    final String columnName = innerEntityColumn.getNameWithAlias(innerEntityMetadata.getTableName());
                    final Object object = resultSet.getObject(columnName);
                    ReflectionUtils.injectField(innerInstance, fieldName, object);
                }

                oneToManyFieldCollection.add(innerInstance);
                ReflectionUtils.injectField(instance, column.getFieldName(), oneToManyFieldCollection);
            } while (resultSet.next());
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
