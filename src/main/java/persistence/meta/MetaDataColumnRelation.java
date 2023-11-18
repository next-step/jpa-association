package persistence.meta;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import java.lang.reflect.Field;
import jakarta.persistence.FetchType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class MetaDataColumnRelation implements Relation {

  private final String fieldName;
  private final String dbName;
  private final FetchType fetchType;
  private final MetaEntity<?> metaEntity;
  private final Class<?> relation;

  public MetaDataColumnRelation(String fieldName,String dbName , FetchType fetchType, MetaEntity<?> metaEntity,
      Class<?> relation) {
    this.fieldName = fieldName;
    this.dbName = dbName;
    this.fetchType = fetchType;
    this.metaEntity = metaEntity;
    this.relation = relation;
  }

  public static MetaDataColumnRelation of(Field field) {

    String dbName = field.getAnnotation(JoinColumn.class).name();
    FetchType fetchType = field.getAnnotation(OneToMany.class).fetch();
    Class<?> relation = OneToMany.class;

    Type type = field.getGenericType();
    Type actualTypeArgument = ((ParameterizedType) type).getActualTypeArguments()[0];

    return new MetaDataColumnRelation(field.getName(), dbName, fetchType, MetaEntity.of((Class<?>) actualTypeArgument) , relation);
  }

  @Override
  public boolean isRelation() {
    return true;
  }

  @Override
  public String getDbName() {
    return dbName;
  }

  @Override
  public FetchType getFetchType() {
    return fetchType;
  }

  @Override
  public MetaEntity<?> getMetaEntity() {
    return metaEntity;
  }

  @Override
  public Class<?> getRelation() {
    return relation;
  }

  @Override
  public String getFieldName() {
    return fieldName;
  }


}
