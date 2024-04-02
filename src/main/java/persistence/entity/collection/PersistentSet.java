package persistence.entity.collection;

import java.util.*;

public class PersistentSet<T> extends AbstractPersistentCollection<T> implements Set<T> {

    public PersistentSet() {
        super(new HashSet<>());
    }
}
