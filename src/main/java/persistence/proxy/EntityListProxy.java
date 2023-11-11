package persistence.proxy;

import persistence.entity.EntityLoader;
import persistence.sql.meta.EntityMeta;

import java.util.*;

public class EntityListProxy<T> implements List<T> {

    private List<T> entityList;
    private EntityLoader entityLoader;
    private EntityMeta entityMeta;
    private String selectQuery;

    protected EntityListProxy() {}

    private EntityListProxy(EntityLoader entityLoader, EntityMeta entityMeta, String selectQuery) {
        this.entityLoader = entityLoader;
        this.entityMeta = entityMeta;
        this.selectQuery = selectQuery;
    }

    public static EntityListProxy of(EntityLoader entityLoader, EntityMeta entityMeta, String selectQuery) {
        return new EntityListProxy(entityLoader, entityMeta, selectQuery);
    }

    private void load() {
        if (entityList != null) {
            return;
        }
        this.entityList = (List<T>) entityLoader.selectChildEntities(entityMeta, selectQuery);
    }

    @Override
    public int size() {
        load();
        return entityList.size();
    }

    @Override
    public boolean isEmpty() {
        load();
        return entityList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        load();
        return entityList.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        load();
        return entityList.iterator();
    }

    @Override
    public Object[] toArray() {
        load();
        return entityList.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        load();
        return entityList.toArray(a);
    }

    @Override
    public boolean add(T t) {
        load();
        return entityList.add(t);
    }

    @Override
    public boolean remove(Object o) {
        load();
        return entityList.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        load();
        return new HashSet<>(entityList).containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        load();
        return entityList.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        load();
        return entityList.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        load();
        return entityList.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        load();
        return entityList.retainAll(c);
    }

    @Override
    public void clear() {
        load();
        entityList.clear();
    }

    @Override
    public T get(int index) {
        load();
        return entityList.get(index);
    }

    @Override
    public T set(int index, T element) {
        load();
        return entityList.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        load();
        entityList.add(index, element);
    }

    @Override
    public T remove(int index) {
        load();
        return entityList.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        load();
        return entityList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        load();
        return entityList.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        load();
        return entityList.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        load();
        return entityList.listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        load();
        return entityList.subList(fromIndex, toIndex);
    }
}
