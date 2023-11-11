package persistence.entity.loader;

import persistence.entity.attribute.EntityAttribute;

import java.util.List;

public interface CollectionLoader {
    <T> List<T> loadCollection(EntityAttribute entityAttribute, String columnName, String id);
}
