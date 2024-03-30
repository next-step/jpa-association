package persistence.entity.collection;

import persistence.entity.loader.CollectionEntityLoader;
import persistence.model.PersistentClass;

import java.util.Collection;
import java.util.Iterator;

public abstract class AbstractPersistentCollection<T> implements Collection<T> {

    private boolean initialized = false;
    private final CollectionEntityLoader collectionEntityLoader;
    private final PersistentClass<T> persistentClass;
    private final String query;
    protected Collection<T> values;

    protected AbstractPersistentCollection(final CollectionEntityLoader collectionEntityLoader, final PersistentClass<T> persistentClass, final String query) {
        this.collectionEntityLoader = collectionEntityLoader;
        this.persistentClass = persistentClass;
        this.query = query;
    }

    protected void initialize() {
        if (initialized) return;

        values = collectionEntityLoader.queryWithLazyColumn(persistentClass, query);
        initialized = true;
    }

    @Override
    public int size() {
        initialize();
        return this.values.size();
    }

    @Override
    public boolean isEmpty() {
        initialize();
        return this.values.isEmpty();
    }

    @Override
    public boolean contains(final Object o) {
        initialize();
        return this.values.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        initialize();
        return this.values.iterator();
    }

    @Override
    public Object[] toArray() {
        initialize();
        return this.values.toArray();
    }

    @Override
    public <T1> T1[] toArray(final T1[] a) {
        initialize();
        return this.values.toArray(a);
    }

    @Override
    public boolean add(final T t) {
        initialize();
        return this.values.add(t);
    }

    @Override
    public boolean remove(final Object o) {
        initialize();
        return this.values.remove(o);
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        initialize();
        return this.values.containsAll(c);
    }

    @Override
    public boolean addAll(final Collection<? extends T> c) {
        initialize();
        return this.values.addAll(c);
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        initialize();
        return this.retainAll(c);
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        initialize();
        return this.values.retainAll(c);
    }

    @Override
    public void clear() {
        initialize();
        this.values.clear();
    }
}
