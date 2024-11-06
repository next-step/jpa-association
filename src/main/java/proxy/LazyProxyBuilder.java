package proxy;

import builder.dml.JoinEntityData;
import database.H2DBConnection;
import jdbc.JdbcTemplate;
import persistence.EntityLoader;

import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.List;

public class LazyProxyBuilder<T> {

    private final JdbcTemplate jdbcTemplate;

    public LazyProxyBuilder(){
        try {
            this.jdbcTemplate = new H2DBConnection().start();
        } catch (SQLException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<T> createProxy(JoinEntityData joinEntityData) {
        return (List<T>) Proxy.newProxyInstance(joinEntityData.getClazz().getClassLoader(),
                new Class[]{List.class},
                new ProxyInvocationHandler(joinEntityData, new EntityLoader(jdbcTemplate)));
    }
}
