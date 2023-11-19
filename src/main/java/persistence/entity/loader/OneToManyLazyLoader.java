package persistence.entity.loader;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;
import persistence.entity.OneToManyAssociation;
import persistence.entity.persister.OneToManyEntityPersister;
import persistence.meta.EntityMeta;

public class OneToManyLazyLoader extends AbstractEntityLoader {
    private final EntityMeta entityMeta;
    private final OneToManyEntityPersister persister;

    public static OneToManyLazyLoader create(EntityMeta entityMeta, OneToManyEntityPersister persister) {
        return new OneToManyLazyLoader(entityMeta, persister);
    }

    private OneToManyLazyLoader(EntityMeta entityMeta, OneToManyEntityPersister persister) {
        this.entityMeta = entityMeta;
        this.persister = persister;
    }

    @Override
    public <T> T load(Class<T> tClass, ResultSet resultSet) {
        final T instance = resultSetToEntity(tClass, resultSet);

        final OneToManyAssociation oneToManyAssociation = entityMeta.getOneToManyAssociation();
        final Field proxyField = oneToManyAssociation.getMappingField();

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(proxyField.getType());

        enhancer.setCallback((LazyLoader) () -> persister.findMany(instance, oneToManyAssociation));

        return instanceProxyFieldMapping(instance, proxyField, enhancer);
    }

    private <T> T instanceProxyFieldMapping(T instance, Field oneField, Enhancer e) {
        try {
            final Field declaredField = instance.getClass().getDeclaredField(oneField.getName());
            declaredField.setAccessible(true);
            declaredField.set(instance, e.create());
            return instance;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
