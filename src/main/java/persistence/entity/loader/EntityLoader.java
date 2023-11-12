package persistence.entity.loader;

import persistence.entity.attribute.EntityAttribute;

public interface EntityLoader {
    <T> T load(EntityAttribute entityAttribute, String queryColumnName, String queryValue);
}
