package persistence.entity.proxy;

import java.util.List;
import java.util.function.Supplier;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CollectionProxyWrapper {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public <T> Object wrap(Class<T> collectionType, Supplier<List<?>> lazyLoadSupplier) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(collectionType);
        enhancer.setCallback((LazyLoader) lazyLoadSupplier::get);

        logger.info("proxy create");
        return enhancer.create();
    }
}
