package persistence.entity.persister;

import jdbc.JdbcTemplate;
import persistence.core.EntityColumns;
import persistence.core.EntityIdColumn;
import persistence.core.EntityMetadata;
import persistence.entity.mapper.EntityColumnsMapper;
import persistence.entity.mapper.EntityIdMapper;
import persistence.sql.dml.DmlGenerator;
import persistence.util.ReflectionUtils;

import java.util.List;

public class EntityPersister {
    private final String tableName;
    private final EntityIdColumn idColumn;
    private final EntityColumns columns;
    private final EntityColumns insertableColumns;
    private final DmlGenerator dmlGenerator;
    private final JdbcTemplate jdbcTemplate;
    private final EntityColumnsMapper entityIdMapper;

    private EntityPersister(final EntityMetadata<?> entityMetadata, final DmlGenerator dmlGenerator, final JdbcTemplate jdbcTemplate) {
        this.tableName = entityMetadata.getTableName();
        this.idColumn = entityMetadata.getIdColumn();
        this.columns = entityMetadata.getColumns();
        this.insertableColumns = entityMetadata.toInsertableColumn();
        this.dmlGenerator = dmlGenerator;
        this.jdbcTemplate = jdbcTemplate;
        this.entityIdMapper = EntityIdMapper.of(entityMetadata.getIdColumn());
    }

    public static EntityPersister of(final EntityMetadata<?> entityMetadata, final DmlGenerator dmlGenerator, final JdbcTemplate jdbcTemplate) {
        return new EntityPersister(entityMetadata, dmlGenerator, jdbcTemplate);
    }

    public void insert(final Object entity) {
        final String insertQuery = renderInsert(entity);
        jdbcTemplate.executeInsert(insertQuery, resultSet -> entityIdMapper.mapColumns(resultSet, entity));
    }

    public void update(final Object entity) {
        final String updateQuery = renderUpdate(entity);
        jdbcTemplate.execute(updateQuery);
    }

    public void delete(final Object entity) {
        final String deleteQuery = renderDelete(entity);
        jdbcTemplate.execute(deleteQuery);
    }

    public String renderInsert(final Object entity) {
        final List<String> columnNames = insertableColumns.getNames();
        final List<Object> values = ReflectionUtils.getFieldValues(entity, insertableColumns.getFieldNames());
        return dmlGenerator.insert(tableName, columnNames, values);
    }

    public String renderUpdate(final Object entity) {
        final List<String> columnNames = insertableColumns.getNames();
        final List<Object> values = ReflectionUtils.getFieldValues(entity, insertableColumns.getFieldNames());
        final Object idValue = ReflectionUtils.getFieldValue(entity, idColumn.getFieldName());
        return dmlGenerator.update(tableName, columnNames, values, idColumn.getName(), idValue);
    }

    public String renderDelete(final Object entity) {
        final Object idValue = ReflectionUtils.getFieldValue(entity, idColumn.getFieldName());
        return dmlGenerator.delete(tableName, idColumn.getName(), idValue);
    }

    public List<String> getColumnNames() {
        return columns.getNames();
    }

    public List<String> getColumnFieldNames() {
        return columns.getFieldNames();
    }

    public int getColumnSize() {
        return columns.size();
    }

    public String getTableName() {
        return tableName;
    }

    public String getIdColumnFieldName() {
        return idColumn.getFieldName();
    }

}
