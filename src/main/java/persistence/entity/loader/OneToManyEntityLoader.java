package persistence.entity.loader;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.EntityKey;
import persistence.entity.OneToManyAssociation;
import persistence.meta.EntityMeta;

public class OneToManyEntityLoader extends AbstractEntityLoader {
    private final Logger log = LoggerFactory.getLogger(OneToManyEntityLoader.class);
    private final EntityMeta entityMeta;
    private static final int ASSOCIATION_START_LEVEL = 1;

    private OneToManyEntityLoader(EntityMeta entityMeta) {
        this.entityMeta = entityMeta;
    }

    public static OneToManyEntityLoader create(EntityMeta entityMeta) {
        return new OneToManyEntityLoader(entityMeta);
    }

    @Override
    public <T> T load(Class<T> tClass, ResultSet resultSet) {
        final T instance = resultSetToEntity(tClass, resultSet);
        final EntityOneToManyPath path = new EntityOneToManyPath(entityMeta, instance);

        do {
            loadManyEntity(path, resultSet);
        } while (isNextRow(resultSet));

        return entityLoad(path, instance);
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

    public <T> List<T> loadAll(Class<T> tClass, ResultSet resultSet) {
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

    private <T> T entityLoad(EntityOneToManyPath path, T instance) {
        OneToManyAssociation oneToManyAssociation = entityMeta.getOneToManyAssociation();
        for (int level = ASSOCIATION_START_LEVEL; level < path.totalLevel(); level++) {
            setFieldValue(instance, oneToManyAssociation.getMappingField().getName(), path.get(level));
            oneToManyAssociation = oneToManyAssociation.getManyEntityMeta().getOneToManyAssociation();
        }
        return instance;
    }


}
