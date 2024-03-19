package persistence.sql.mapping;

import jakarta.persistence.OneToMany;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Associations implements Iterable<OneToManyData> {
    private final List<OneToManyData> associations;

    @Override
    public Iterator<OneToManyData> iterator() {
        return associations.iterator();
    }

    private Associations(List<OneToManyData> associations) {
        this.associations = associations;
    }

    public static Associations fromEntityClass(Class<?> clazz) {
        return new Associations(extractAssociations(clazz));
    }

    public List<OneToManyData> getEagerAssociations() {
        return associations.stream()
                .filter(OneToManyData::isEagerLoad)
                .collect(Collectors.toList());
    }

    public boolean hasEagerLoad() {
        return associations.stream().anyMatch(OneToManyData::isEagerLoad);
    }

    public boolean hasNotLazyLoad() {
        return associations.stream().noneMatch(OneToManyData::isLazyLoad);
    }

    public boolean isNotEmpty() {
        return !associations.isEmpty();
    }

    private static List<OneToManyData> extractAssociations(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(OneToMany.class))
                .map(OneToManyData::from)
                .collect(Collectors.toList());
    }
}
