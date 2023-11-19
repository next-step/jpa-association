package persistence.entity;

import jakarta.persistence.FetchType;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import jdbc.CollectionRowMapper;
import jdbc.JdbcRowMapper;
import jdbc.JdbcTemplate;
import jdbc.RowMapper;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;
import persistence.meta.MetaDataColumn;
import persistence.meta.MetaDataColumns;
import persistence.meta.MetaEntity;
import persistence.meta.Relation;
import persistence.sql.dml.builder.JoinQueryBuilder;
import persistence.sql.dml.builder.SelectQueryBuilder;

public class CollectionElementLoader<T> implements RelationLoader<T> {

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
    this.elementRowMapper = new CollectionRowMapper<>(metaEntity);
    this.relation = relation;
  }

  public static CollectionElementLoader<?> of(Class<?> clazz, Connection connection) {

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

    String joinQuery = new JoinQueryBuilder()
        .select(metaEntity.getEntityTableColumnsWithId(), elementEntity.getEntityTableColumnsWithId())
        .from(metaEntity.getTableName())
        .join(List.of(elementEntity.getTableName()))
        .on(List.of(relation.getDbName()))
        .where(metaEntity.getPrimaryKeyColumn().getDBColumnName(metaEntity.getTableName()), List.of(String.valueOf(id)))
        .build().createJoinQuery();

    if (relation.getFetchType() == FetchType.LAZY) {
      MetaDataColumn relationColumn = metaEntity.getMetaDataColumns()
          .getColumnByFieldName(relation.getFieldName());

      Enhancer enhancer = new Enhancer();
      enhancer.setSuperclass(List.class); // 여기 relation 에서 타입 가져와야할듯.
      enhancer.setCallback(new MethodLazyLoader(joinQuery, elementRowMapper));
//      List<Object> objects = (List<>) enhancer.create(); // 이부분에 타입만 잘정의하면 lazy loader 잘들어갈듯
//      relationColumn.setFieldValue(entity, objects);
      return Optional.ofNullable(entity);
    }

    return Optional.ofNullable(jdbcTemplate.queryForObject(joinQuery, elementRowMapper));

  }

  @Override
  public List<T> loadByIds(List<Long> ids) {

    MetaDataColumn keyColumn = metaEntity.getPrimaryKeyColumn();
    String targetColumn = keyColumn.getDBColumnName();

    List<String> idValues = ids.stream().map(Object::toString).collect(Collectors.toList());

    String query = selectQueryBuilder.createSelectByFieldsQuery(metaEntity.getColumnClauseWithId(),
        metaEntity.getTableName(), targetColumn, idValues);

    List<T> entities = jdbcTemplate.query(query, rowMapper);

    String joinQuery = new JoinQueryBuilder()
        .select(metaEntity.getEntityTableColumnsWithId(), elementEntity.getEntityTableColumnsWithId())
        .from(metaEntity.getTableName())
        .join(List.of(elementEntity.getTableName()))
        .on(List.of(relation.getDbName()))
        .where(metaEntity.getPrimaryKeyColumn().getDBColumnName(metaEntity.getTableName()), idValues)
        .build().createJoinQuery();

    if (relation.getFetchType() == FetchType.LAZY) {

      MetaDataColumn relationColumn = metaEntity.getMetaDataColumns()
          .getColumnByFieldName(relation.getFieldName());

//      relationColumn.setFieldValue(entity, elements); proxy 예정
      return entities;
    }

    List<T> entitiesWithCollection = jdbcTemplate.query(joinQuery, elementRowMapper);

    return entitiesWithCollection;
  }

  public class MethodLazyLoader implements LazyLoader {

    private final String joinQuery;
    private final RowMapper<?> rowMapper;

    public MethodLazyLoader(String joinQuery, RowMapper<?> rowMapper) {
      this.joinQuery = joinQuery;
      this.rowMapper = rowMapper;
    }

    @Override
    public Object loadObject() throws Exception {
      return jdbcTemplate.query(joinQuery, elementRowMapper);
    }

  }

}
