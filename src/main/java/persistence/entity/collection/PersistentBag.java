package persistence.entity.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

public class PersistentBag<T> extends AbstractPersistentCollection<T> implements List<T> {

    public PersistentBag() {
        super(new ArrayList<>());
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends T> c) {
        return getValuesInternal().addAll(index, c);
    }

    @Override
    public T get(final int index) {
        return getValuesInternal().get(index);
    }

    @Override
    public T set(final int index, final T element) {
        return getValuesInternal().set(index, element);
    }

    @Override
    public void add(final int index, final T element) {
        getValuesInternal().add(index, element);
    }

    @Override
    public T remove(final int index) {
        return getValuesInternal().remove(index);
    }

    @Override
    public int indexOf(final Object o) {
        return getValuesInternal().indexOf(o);
    }

    @Override
    public int lastIndexOf(final Object o) {
        return getValuesInternal().lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return getValuesInternal().listIterator();
    }

    @Override
    public ListIterator<T> listIterator(final int index) {
        return getValuesInternal().listIterator(index);
    }

    @Override
    public List<T> subList(final int fromIndex, final int toIndex) {
        return getValuesInternal().subList(fromIndex, toIndex);
    }

    private List<T> getValuesInternal() {
        return (List<T>) this.values;
    }
}
