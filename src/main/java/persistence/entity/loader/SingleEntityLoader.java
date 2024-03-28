package persistence.entity.loader;

import jdbc.JdbcTemplate;
import jdbc.RowMapper;
import persistence.entity.SingleEntityRowMapper;
import persistence.model.*;
import persistence.sql.QueryException;
import persistence.sql.dml.*;
import persistence.sql.mapping.Column;
import persistence.sql.mapping.PrimaryKey;
import persistence.sql.mapping.Table;
import persistence.sql.mapping.TableBinder;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class SingleEntityLoader implements EntityLoader {

    private final TableBinder tableBinder;
    private final CollectionPersistentClassBinder collectionPersistentClassBinder;
    private final DmlQueryBuilder dmlQueryBuilder;
    private final JdbcTemplate jdbcTemplate;

    public SingleEntityLoader(final TableBinder tableBinder, final CollectionPersistentClassBinder collectionPersistentClassBinder, final DmlQueryBuilder dmlQueryBuilder, final JdbcTemplate jdbcTemplate) {
        this.tableBinder = tableBinder;
        this.collectionPersistentClassBinder = collectionPersistentClassBinder;
        this.dmlQueryBuilder = dmlQueryBuilder;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public <T> List<T> load(final Class<T> clazz, final Object key) {
        final PersistentClass<T> persistentClass = PersistentClassMapping.getPersistentClass(clazz);
        final Select select = generateSelect(clazz, key);

        final String selectQuery = dmlQueryBuilder.buildSelectQuery(select);
        log.debug("\n" + selectQuery);

        final Optional<EntityJoinField> eagerJoinField = persistentClass.getFields()
                .stream()
                .filter(AbstractEntityField::isJoinField)
                .map(joinField -> (EntityJoinField) joinField)
                .filter(EntityJoinField::isEager)
                .findFirst();

        if (eagerJoinField.isPresent()) {
            final CollectionEntityLoader collectionEntityLoader = new CollectionEntityLoader(jdbcTemplate);
            return collectionEntityLoader.queryWithEagerColumn(clazz, eagerJoinField.get(), this.collectionPersistentClassBinder, selectQuery);
        }

        final RowMapper<T> rowMApper = new SingleEntityRowMapper<>(persistentClass);

        return jdbcTemplate.query(selectQuery, rowMApper)
                .stream()
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toUnmodifiableList());
    }

    private <T> Select generateSelect(final Class<T> clazz, final Object key) {
        final Table table = tableBinder.createTable(clazz, this.collectionPersistentClassBinder);

        final Select select = new Select(table);

        if (Objects.nonNull(key)) {
            select.addWhere(generateIdColumnWhere(table, key));
        }

        return select;
    }

    private Where generateIdColumnWhere(final Table table, final Object key) {
        final Column idColumn = findIdColumnInPrimaryKey(table.getPrimaryKey());
        idColumn.setValue(key);

        return new Where(idColumn, idColumn.getValue(), LogicalOperator.NONE, new ComparisonOperator(ComparisonOperator.Comparisons.EQ));
    }

    private Column findIdColumnInPrimaryKey(final PrimaryKey primaryKey) {
        return primaryKey.getColumns()
                .stream()
                .findFirst()
                .orElseThrow(() -> new QueryException("not found id column"));
    }
}
