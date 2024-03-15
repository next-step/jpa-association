package persistence.entity.proxy;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;
import persistence.entity.loader.EntityLoader;
import persistence.sql.meta.Column;
import persistence.sql.meta.Table;

public class LazyLoadingProxyFactory {

    private LazyLoadingProxyFactory() {
    }

    public static Object create(Table root, Table relationTable, Object instance, EntityLoader entityLoader,
                                Consumer<List<?>> externalFunction) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(root.getRelationValue(instance, relationTable).getClass());
        enhancer.setCallback((LazyLoader) () -> Table.getRelationColumns(relationTable)
            .stream().filter(entry -> entry.getKey().equals(root))
            .map(entry -> callback(root, relationTable, instance, entityLoader, entry.getValue(), externalFunction))
            .findFirst()
            .orElse(Collections.emptyList()));

        return enhancer.create();
    }

    private static List<?> callback(Table root, Table relationTable, Object instance, EntityLoader entityLoader,
                                    Column column, Consumer<List<?>> externalFunction) {
        List<?> result = entityLoader.find(relationTable.getClazz(), Map.of(column, root.getIdValue(instance)));
        externalFunction.accept(result);
        return result;
    }
}
