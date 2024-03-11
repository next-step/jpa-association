package persistence.entity;

public interface EntityManager {

    <T> T find(Class<T> clazz, Long Id);

    <T> T persist(Object entity);

    void remove(Object entity);
}
