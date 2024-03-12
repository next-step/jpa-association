package persistence.entity.proxy;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;
import persistence.entity.loader.EntityLoader;
import persistence.sql.meta.Column;
import persistence.sql.meta.Table;

public class LazyLoadingProxyFactory {

    private LazyLoadingProxyFactory() {
    }

    public static Object create(Table root, Table relationTable, Object instance, EntityLoader entityLoader) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(root.getRelationValue(instance,
            relationTable).getClass());
        enhancer.setCallback((LazyLoader) () -> Table.getRelationColumns(relationTable)
            .stream().filter(entry -> entry.getKey().equals(root))
            .map(entry -> callback(root, relationTable, instance, entityLoader, entry))
            .findFirst().orElse(Collections.emptyList()));

        return enhancer.create();
    }

    private static List<?> callback(Table root, Table relationTable, Object instance, EntityLoader entityLoader, Map.Entry<Table, Column> entry) {
        return entityLoader.find(relationTable.getClazz(), Map.of(entry.getValue(), root.getIdValue(instance)));
    }
}
