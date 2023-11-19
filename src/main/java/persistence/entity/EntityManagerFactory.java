package persistence.entity;


import java.util.Set;
import jdbc.JdbcTemplate;
import persistence.dialect.Dialect;


public class EntityManagerFactory {
    private final EntityPersisteContext entityPersisteContext;

    private EntityManagerFactory(Set<Class<?>> entityClasses, JdbcTemplate jdbcTemplate, Dialect dialect) {
        this.entityPersisteContext = EntityPersisteContext.create(entityClasses, jdbcTemplate, dialect);
    }

    public static EntityManagerFactory of(String packageName, JdbcTemplate jdbcTemplate, Dialect dialect) {
        return new EntityManagerFactory(getEntityClassesFromPackage(packageName), jdbcTemplate, dialect);
    }


    private static Set<Class<?>> getEntityClassesFromPackage(String packageName) {
        return new EntityClassLoader(packageName).getEntityClasses();
    }

    public EntityManager createEntityManager() {
        return SimpleEntityManager.create(entityPersisteContext);
    }
}
