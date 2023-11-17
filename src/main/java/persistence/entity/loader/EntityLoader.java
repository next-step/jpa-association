package persistence.entity.loader;

public interface EntityLoader<T> {
    T find(Long id);
}
