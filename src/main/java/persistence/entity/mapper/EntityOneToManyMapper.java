package persistence.entity.mapper;

import persistence.core.*;
import persistence.exception.PersistenceException;
import persistence.util.ReflectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class EntityOneToManyMapper implements EntityColumnsMapper {

    private final List<EntityOneToManyColumn> oneToManyColumns;

    public EntityOneToManyMapper(final List<EntityOneToManyColumn> oneToManyColumns) {
        this.oneToManyColumns = oneToManyColumns;
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
                    final String columnName = innerEntityColumn.getName();
                    final Object object = resultSet.getObject(innerEntityMetadata.getTableName() + "." + columnName);
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
            oneToManyFieldCollection = createCollectionBy(entityOneToManyColumn.getType());
        }
        return oneToManyFieldCollection;
    }

    private Collection<Object> createCollectionBy(final Class<?> type) {
        if (type.isAssignableFrom(List.class)) {
            return new ArrayList<>();
        }
        if (type.isAssignableFrom(Set.class)) {
            return new LinkedHashSet<>();
        }
        throw new PersistenceException(type.getName() + "은 지원하지 않는 컬렉션 타입입니다.");
    }
}
