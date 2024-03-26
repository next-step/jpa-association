package persistence.entity.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class PersistentCollection<T> implements Collection<T> {
    protected Collection<T> collection = null;
    private final CollectionLoader collectionLoader;
    private final Object joinColumnValue;

    public PersistentCollection(
            CollectionLoader collectionLoader,
            Object joinColumnValue
    ) {
        this.collectionLoader = collectionLoader;
        this.joinColumnValue = joinColumnValue;
    }

    final protected void load() {
        if (collection != null) {
            return;
        }
        collection = collectionLoader.loadCollection(joinColumnValue);
    }

    @Override
    public int size() {
        load();
        return collection.size();
    }

    @Override
    public boolean isEmpty() {
        load();
        return collection.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        load();
        return collection.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        load();
        return collection.iterator();
    }

    @Override
    public Object[] toArray() {
        load();
        return new Object[0];
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        load();
        return null;
    }

    @Override
    public boolean add(T t) {
        load();
        return true;
    }

    @Override
    public boolean remove(Object o) {
        load();
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        load();
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        load();
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        load();
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        load();
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public <T1> T1[] toArray(IntFunction<T1[]> generator) {
        load();
        return Collection.super.toArray(generator);
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        load();
        return Collection.super.removeIf(filter);
    }

    @Override
    public Spliterator<T> spliterator() {
        load();
        return Collection.super.spliterator();
    }

    @Override
    public Stream<T> stream() {
        load();
        return Collection.super.stream();
    }

    @Override
    public Stream<T> parallelStream() {
        load();
        return Collection.super.parallelStream();
    }
}
