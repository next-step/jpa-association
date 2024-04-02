package persistence.entity.collection;

import java.util.Collection;
import java.util.Iterator;

public abstract class AbstractPersistentCollection<T> implements Collection<T> {

    protected Collection<T> values;

    protected AbstractPersistentCollection(final Collection<T> values) {
        this.values = values;
    }

    @Override
    public int size() {
        return this.values.size();
    }

    @Override
    public boolean isEmpty() {
        return this.values.isEmpty();
    }

    @Override
    public boolean contains(final Object o) {
        return this.values.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return this.values.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.values.toArray();
    }

    @Override
    public <T1> T1[] toArray(final T1[] a) {
        return this.values.toArray(a);
    }

    @Override
    public boolean add(final T t) {
        return this.values.add(t);
    }

    @Override
    public boolean remove(final Object o) {
        return this.values.remove(o);
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return this.values.containsAll(c);
    }

    @Override
    public boolean addAll(final Collection<? extends T> c) {
        return this.values.addAll(c);
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        return this.retainAll(c);
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        return this.values.retainAll(c);
    }

    @Override
    public void clear() {
        this.values.clear();
    }
}
