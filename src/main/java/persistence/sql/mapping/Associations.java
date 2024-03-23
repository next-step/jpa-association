package persistence.sql.mapping;

import jakarta.persistence.OneToMany;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Associations {
    private final List<OneToManyData> associations;

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

    public boolean isNotEmpty() {
        return !associations.isEmpty();
    }

    private static List<OneToManyData> extractAssociations(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(OneToMany.class))
                .map(OneToManyData::from)
                .collect(Collectors.toList());
    }

    public boolean hasLazyLoad() {
        return associations.stream().anyMatch(OneToManyData::isLazyLoad);
    }

    public List<OneToManyData> getLazyAssociations() {
        return associations.stream().filter(OneToManyData::isLazyLoad).collect(Collectors.toList());
    }
}
