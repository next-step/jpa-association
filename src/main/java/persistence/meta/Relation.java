package persistence.meta;

import jakarta.persistence.FetchType;

public interface Relation {

  boolean isRelation();

  String getDbName();
  FetchType getFetchType();
  MetaEntity<?> getMetaEntity();
  Class<?> getRelation();
  String getFieldName();
}
