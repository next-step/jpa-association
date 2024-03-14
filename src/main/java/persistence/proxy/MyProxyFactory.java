package persistence.proxy;

import jdbc.JdbcTemplate;
import jdbc.RowMapper;
import jdbc.RowMapperFactory;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;
import persistence.sql.dml.SelectAllQueryBuilder;

import java.util.List;

public class MyProxyFactory implements ProxyFactory {

    private final JdbcTemplate jdbcTemplate;

    public MyProxyFactory(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Object createProxy(Class<?> clazz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(List.class);
        enhancer.setCallback((LazyLoader) () -> findAll(clazz));
        Object proxy = enhancer.create();
        return proxy;
    }

    private List<?> findAll(Class<?> clazz) {
        SelectAllQueryBuilder selectAllQueryBuilder = new SelectAllQueryBuilder();
        RowMapper<?> rowMapper = RowMapperFactory.create(clazz);
        return jdbcTemplate.query(selectAllQueryBuilder.build(clazz), rowMapper);
    }
}
