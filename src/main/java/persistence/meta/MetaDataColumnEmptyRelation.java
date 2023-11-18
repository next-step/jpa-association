package persistence.meta;

import jakarta.persistence.FetchType;

public class MetaDataColumnEmptyRelation implements Relation {

  public MetaDataColumnEmptyRelation() {
  }

  @Override
  public boolean isRelation() {
    return false;
  }

  @Override
  public String getDbName() {
    return null;
  }

  @Override
  public FetchType getFetchType() {
    return null;
  }

  @Override
  public MetaEntity<?> getMetaEntity() {
    return null;
  }

  @Override
  public Class<?> getRelation() {
    return null;
  }

  @Override
  public String getFieldName() {
    return null;
  }
}
