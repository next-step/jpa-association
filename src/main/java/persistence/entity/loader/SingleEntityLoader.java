package persistence.entity.loader;

import jdbc.JdbcTemplate;
import jdbc.RowMapper;
import persistence.ReflectionUtils;
import persistence.entity.SingleEntityRowMapper;
import persistence.model.*;
import persistence.sql.QueryException;
import persistence.sql.dml.*;
import persistence.sql.mapping.*;

import java.util.*;
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

        final Map<Boolean, List<EntityJoinField>> joinFieldsMap = persistentClass.getFields()
                .stream()
                .filter(AbstractEntityField::isJoinField)
                .map(joinField -> (EntityJoinField) joinField)
                .collect(Collectors.partitioningBy(EntityJoinField::isEager));

        final Optional<EntityJoinField> eagerJoinField = joinFieldsMap.getOrDefault(true, Collections.emptyList())
                .stream().findFirst();

        final List<EntityJoinField> lazyJoinFields = joinFieldsMap.getOrDefault(false, Collections.emptyList());

        final List<T> entities = getEntities(persistentClass, eagerJoinField, selectQuery);

        if (!lazyJoinFields.isEmpty()) {
            final EntityId entityId = persistentClass.getEntityId();
            final Object[] idValues = entities.stream().map(entity -> ReflectionUtils.getFieldValue(entityId.getField(), entity)).toArray();

            final Select[] selects = lazyJoinFields.stream().map(field -> generateSelect(field.getEntityClass(), select.getTable().getPrimaryKey().getColumns().get(0), idValues[0])).toArray(Select[]::new);

            Arrays.stream(selects)
                    .map(dmlQueryBuilder::buildSelectQuery)
                    .forEach(System.out::println);
        }

        return entities;
    }

    private <T> List<T> getEntities(final PersistentClass<T> persistentClass, final Optional<EntityJoinField> eagerJoinField, final String query) {
        if (eagerJoinField.isPresent()) {
            final CollectionEntityLoader collectionEntityLoader = new CollectionEntityLoader(jdbcTemplate);
            return collectionEntityLoader.queryWithEagerColumn(persistentClass.getEntityClass(), eagerJoinField.get(), this.collectionPersistentClassBinder, query);
        }

        final RowMapper<T> rowMApper = new SingleEntityRowMapper<>(persistentClass);

        return jdbcTemplate.query(query, rowMApper)
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

    private <T> Select generateSelect(final Class<T> clazz, final Column column, final Object value) {
        final Table table = tableBinder.createTable(clazz, this.collectionPersistentClassBinder);

        final Select select = new Select(table);
        final Value columnValue = new Value(value.getClass(), ColumnTypeMapper.getInstance().toSqlType(value.getClass()), value);

        select.addWhere(new Where(table.getName(), column, columnValue, LogicalOperator.NONE, new ComparisonOperator(ComparisonOperator.Comparisons.EQ)));

        return select;
    }
}
