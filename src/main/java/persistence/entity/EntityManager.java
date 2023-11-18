package persistence.entity;

import jakarta.persistence.criteria.Join;
import java.util.List;
import persistence.sql.common.meta.JoinColumn;

public interface EntityManager {

    <T> List<T> findAll(Class<T> tClass);

    <T, R> T find(Class<T> tClass, R r);

    <S, R> List<S> findJoin(Class<S> sClass, R r, JoinColumn joinColumn);

    <T> T persist(T t);

    <T> void remove(T t, Object arg);

    void flush();
}
