package persistence.entity.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public interface EntityLoader {

    Logger log = LoggerFactory.getLogger(EntityLoader.class);

    <T> List<T> load(final Class<T> clazz, final Object key);

}
