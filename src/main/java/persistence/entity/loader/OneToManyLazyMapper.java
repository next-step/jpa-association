package persistence.entity.loader;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.List;
import jdbc.JdbcTemplate;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.OneToManyAssociation;
import persistence.meta.EntityMeta;
import persistence.sql.QueryGenerator;

public class OneToManyLazyMapper extends EntityMapper {
    private static final Logger log = LoggerFactory.getLogger(OneToManyLazyMapper.class);
    private final JdbcTemplate jdbcTemplate;
    private final QueryGenerator queryGenerator;

    public OneToManyLazyMapper(EntityMeta entityMeta, JdbcTemplate jdbcTemplate, QueryGenerator queryGenerator) {
        super(entityMeta);
        this.jdbcTemplate = jdbcTemplate;
        this.queryGenerator = queryGenerator;
    }

    public <T> T findLazyMapper(Class<T> tClass, ResultSet resultSet) {
        T instance = resultSetToEntity(tClass, resultSet);

        final OneToManyAssociation oneToManyAssociation = entityMeta.getOneToManyAssociation();
        final Field proxyField = oneToManyAssociation.getMappingField();

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(proxyField.getType());
        enhancer.setCallback((LazyLoader) () -> manyLazyLoader(instance, oneToManyAssociation));

        return instanceProxyFieldMapping(instance, proxyField, enhancer);
    }

    private <T> List<?> manyLazyLoader(T instance, OneToManyAssociation oneToManyAssociation) {
        final String query = queryGenerator
                .select()
                .findByForeignerId(entityMeta.getPkValue(instance));

        log.debug(query);

        return jdbcTemplate.query(query, (resultSet) ->
                this.findMapper(oneToManyAssociation.getManyAssociationClassType(), resultSet));
    }

    private <T> T instanceProxyFieldMapping(T instance, Field oneField, Enhancer e) {
        try {
            final Field declaredField = instance.getClass().getDeclaredField(oneField.getName());
            declaredField.setAccessible(true);
            declaredField.set(instance, e.create());
            return instance;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}

