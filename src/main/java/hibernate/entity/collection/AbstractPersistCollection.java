package hibernate.entity.collection;

import hibernate.entity.EntityLoader;
import hibernate.entity.meta.EntityClass;

import java.util.Collection;
import java.util.Iterator;

public abstract class AbstractPersistCollection<T> implements Collection<T> {

    protected Collection<T> values = null;
    protected boolean isLoaded = false;
    protected final EntityClass<T> entityClass;
    protected final EntityLoader entityLoader;

    protected AbstractPersistCollection(final EntityClass<T> entityClass, final EntityLoader entityLoader) {
        this.entityClass = entityClass;
        this.entityLoader = entityLoader;
    }

    protected void load() {
        if (isLoaded) {
            return;
        }
        values = entityLoader.findAll(entityClass);
        isLoaded = true;
    }

    @Override
    public int size() {
        load();
        return values.size();
    }

    @Override
    public boolean isEmpty() {
        load();
        return values.isEmpty();
    }

    @Override
    public boolean contains(final Object o) {
        load();
        return values.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        load();
        return values.iterator();
    }

    @Override
    public Object[] toArray() {
        load();
        return values.toArray();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        load();
        return values.toArray(a);
    }

    @Override
    public boolean add(final T t) {
        load();
        return values.add(t);
    }

    @Override
    public boolean remove(final Object o) {
        load();
        return values.remove(o);
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        load();
        return values.containsAll(c);
    }

    @Override
    public boolean addAll(final Collection<? extends T> c) {
        load();
        return values.addAll(c);
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        load();
        return values.removeAll(c);
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        load();
        return values.retainAll(c);
    }

    @Override
    public void clear() {
        load();
        values.clear();
    }
}
