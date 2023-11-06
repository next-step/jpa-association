package hibernate.entity.collection;

import hibernate.entity.EntityLoader;
import hibernate.entity.meta.EntityClass;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class PersistentCollection<T> implements List<T> {

    private List<T> values = null;
    private boolean isLoaded = false;
    private final EntityClass<T> entityClass;
    private final EntityLoader entityLoader;

    public PersistentCollection(final EntityClass<T> entityClass, final EntityLoader entityLoader) {
        this.entityClass = entityClass;
        this.entityLoader = entityLoader;
    }

    private void load() {
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
    public boolean contains(Object o) {
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
    public <T> T[] toArray(T[] a) {
        load();
        return values.toArray(a);
    }

    @Override
    public boolean add(T t) {
        load();
        return values.add(t);
    }

    @Override
    public boolean remove(Object o) {
        load();
        return values.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        load();
        return values.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        load();
        return values.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        load();
        return values.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        load();
        return values.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        load();
        return values.retainAll(c);
    }

    @Override
    public void clear() {
        load();
        values.clear();
    }

    @Override
    public T get(int index) {
        load();
        return values.get(index);
    }

    @Override
    public T set(int index, T element) {
        load();
        return values.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        load();
        values.add(index, element);
    }

    @Override
    public T remove(int index) {
        load();
        return values.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        load();
        return values.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        load();
        return values.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        load();
        return values.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        load();
        return values.listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        load();
        return values.subList(fromIndex, toIndex);
    }
}
