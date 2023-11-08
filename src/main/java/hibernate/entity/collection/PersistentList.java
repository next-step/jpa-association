package hibernate.entity.collection;

import hibernate.entity.EntityLoader;
import hibernate.entity.meta.EntityClass;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

public class PersistentList<T> extends AbstractPersistCollection<T> implements List<T> {

    public PersistentList(final EntityClass<T> entityClass, final EntityLoader entityLoader) {
        super(entityClass, entityLoader);
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends T> c) {
        load();
        return ((List<T>) values).addAll(c);
    }

    @Override
    public T get(int index) {
        load();
        return ((List<T>) values).get(index);
    }

    @Override
    public T set(final int index, final T element) {
        load();
        return ((List<T>) values).set(index, element);
    }

    @Override
    public void add(final int index, final T element) {
        load();
        ((List<T>) values).add(index, element);
    }

    @Override
    public T remove(final int index) {
        load();
        return ((List<T>) values).remove(index);
    }

    @Override
    public int indexOf(final Object o) {
        load();
        return ((List<T>) values).indexOf(o);
    }

    @Override
    public int lastIndexOf(final Object o) {
        load();
        return ((List<T>) values).lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        load();
        return ((List<T>) values).listIterator();
    }

    @Override
    public ListIterator<T> listIterator(final int index) {
        load();
        return ((List<T>) values).listIterator(index);
    }

    @Override
    public List<T> subList(final int fromIndex, final int toIndex) {
        load();
        return ((List<T>) values).subList(fromIndex, toIndex);
    }
}
