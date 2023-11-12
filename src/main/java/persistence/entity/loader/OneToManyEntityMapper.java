package persistence.entity.loader;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import persistence.entity.EntityKey;
import persistence.entity.OneToManyAssociation;
import persistence.meta.EntityMeta;

public class OneToManyEntityMapper extends EntityMapper {

    private static final int ASSOCIATION_START_LEVEL = 1;

    public OneToManyEntityMapper(EntityMeta entityMeta) {
        super(entityMeta);
    }

    @Override
    public <T> T findMapper(Class<T> tClass, ResultSet resultSet) {
        final T instance = resultSetToEntity(tClass, resultSet);
        final EntityOneToManyPath path = new EntityOneToManyPath(entityMeta, instance);

        do {
            loadManyEntity(path, resultSet);
        } while (isNextRow(resultSet));

        return entityLoad(path, instance);
    }

    public <T> List<T> findAllMapper(Class<T> tClass, ResultSet resultSet) {
        final Map<EntityKey, EntityOneToManyPath> context = new ConcurrentHashMap<>();
        List<T> list = new ArrayList<>();
        while (isNextRow(resultSet)) {
            final T instance = resultSetToEntity(tClass, resultSet);
            final Object pkValue = entityMeta.getPkValue(instance);
            final EntityKey entityKey = EntityKey.of(tClass, pkValue);

            EntityOneToManyPath path = context.getOrDefault(entityKey, new EntityOneToManyPath(entityMeta, instance));
            context.put(entityKey, path);
            loadManyEntity(path, resultSet);
        }

        for (Entry<EntityKey, EntityOneToManyPath> entries : context.entrySet()) {
            EntityOneToManyPath path = entries.getValue();
            final Object entity = path.getRootInstance();
            list.add((T) entityLoad(path, entity));
        }

        return list;
    }

    private void loadManyEntity(EntityOneToManyPath path, ResultSet resultSet) {
        OneToManyAssociation oneToManyAssociation = entityMeta.getOneToManyAssociation();

        for (int level = ASSOCIATION_START_LEVEL; level < path.totalLevel(); level++) {
            final List<Object> list = path.get(level);
            list.add(resultSetSingleToEntity(oneToManyAssociation.getManyEntityMeta().getEntityClass(), resultSet,
                    level));
            oneToManyAssociation = oneToManyAssociation.getManyEntityMeta().getOneToManyAssociation();
        }
    }

    private <T> T entityLoad(EntityOneToManyPath path, T instance) {
        OneToManyAssociation oneToManyAssociation = entityMeta.getOneToManyAssociation();
        for (int level = ASSOCIATION_START_LEVEL; level < path.totalLevel(); level++) {
            setFieldValue(instance, oneToManyAssociation.getOneField().getName(), path.get(level));
            oneToManyAssociation = oneToManyAssociation.getManyEntityMeta().getOneToManyAssociation();
        }
        return instance;
    }
}
