package persistence.entity;

import jakarta.persistence.FetchType;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import jdbc.CollectionRowMapper;
import jdbc.JdbcRowMapper;
import jdbc.JdbcTemplate;
import jdbc.RowMapper;
import persistence.meta.MetaDataColumn;
import persistence.meta.MetaDataColumns;
import persistence.meta.MetaEntity;
import persistence.meta.Relation;
import persistence.sql.dml.builder.JoinQueryBuilder;
import persistence.sql.dml.builder.SelectQueryBuilder;

public class CollectionElementLoader<T> implements RelationLoader<T>{
  private final JdbcTemplate jdbcTemplate;
  private final SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder();
  private final MetaEntity<T> metaEntity;
  private final RowMapper<T> rowMapper;
  private final MetaEntity<?> elementEntity;
  private final RowMapper<T> elementRowMapper;
  private final Relation relation;

  private CollectionElementLoader(Connection connection, MetaEntity<T> metaEntity,
      MetaEntity<?> elementEntity, Relation relation) {
    this.jdbcTemplate = new JdbcTemplate(connection);
    this.metaEntity = metaEntity;
    this.elementEntity = elementEntity;
    this.rowMapper = new JdbcRowMapper<>(metaEntity);
    this.elementRowMapper = new CollectionRowMapper<>(metaEntity, elementEntity);
    this.relation = relation;
  }

  public static CollectionElementLoader<?> of(Class<?> clazz, Connection connection){

    MetaEntity<?> entity = MetaEntity.of(clazz);
    MetaDataColumns columns = entity.getMetaDataColumns();
    Relation relation = columns.getRelation();

    MetaEntity<?> elementEntity = relation.getMetaEntity();


    return new CollectionElementLoader<>(connection, entity, elementEntity, relation);
  }

  @Override
  public Optional<T> load(Long id) {

    MetaDataColumn keyColumn = metaEntity.getPrimaryKeyColumn();
    String targetColumn = keyColumn.getDBColumnName();

    String query = selectQueryBuilder.createSelectByFieldQuery(metaEntity.getColumnClauseWithId(),
        metaEntity.getTableName(), targetColumn, id);

    T entity = jdbcTemplate.queryForObject(query, rowMapper);

    if(relation.getFetchType() == FetchType.LAZY){
      MetaDataColumn relationColumn = metaEntity.getMetaDataColumns().getColumnByFieldName(relation.getFieldName());
//      relationColumn.setFieldValue(entity, elements); proxy 예정
      return Optional.ofNullable(entity);
    }

    String joinQuery = new JoinQueryBuilder()
        .select(metaEntity.getTableName(),elementEntity.getTableName(), metaEntity.getEntityColumnsWithId(), elementEntity.getEntityColumnsWithId())
        .join(List.of(elementEntity.getTableName()))
        .on(List.of(relation.getDbName()))
        .where(metaEntity.getPrimaryKeyColumn().getDBColumnName(), List.of(String.valueOf(id)))
        .build().createJoinQuery();

    return Optional.ofNullable(jdbcTemplate.queryForObject(joinQuery, elementRowMapper));

  }

  @Override
  public List<T> loadByIds(List<Long> ids) {

    MetaDataColumn keyColumn = metaEntity.getPrimaryKeyColumn();
    String targetColumn = keyColumn.getDBColumnName();

    String query = selectQueryBuilder.createSelectByFieldQuery(metaEntity.getColumnClauseWithId(),
        metaEntity.getTableName(), targetColumn, ids);

    List<T> entities = jdbcTemplate.query(query, rowMapper);

    if(relation.getFetchType() == FetchType.LAZY){
      MetaDataColumn relationColumn = metaEntity.getMetaDataColumns().getColumnByFieldName(relation.getFieldName());
//      relationColumn.setFieldValue(entity, elements); proxy 예정
      return entities;
    }

    String joinQuery = new JoinQueryBuilder()
        .select(metaEntity.getTableName(),elementEntity.getTableName(), metaEntity.getEntityColumnsWithId(), elementEntity.getEntityColumnsWithId())
        .join(List.of(elementEntity.getTableName()))
        .on(List.of(relation.getDbName()))
        .where(metaEntity.getPrimaryKeyColumn().getDBColumnName(), List.of(String.valueOf(ids)))
        .build().createJoinQuery();

    List<T> entitiesWithCollection = jdbcTemplate.query(joinQuery, elementRowMapper);

    return entitiesWithCollection;
  }


}
