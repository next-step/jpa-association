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
    throw new RuntimeException("relation이 존재하지 않는 Column 입니다.");
  }

  @Override
  public FetchType getFetchType() {
    throw new RuntimeException("relation이 존재하지 않는 Column 입니다.");
  }

  @Override
  public MetaEntity<?> getMetaEntity() {
    throw new RuntimeException("relation이 존재하지 않는 Column 입니다.");
  }

  @Override
  public Class<?> getRelation() {
    throw new RuntimeException("relation이 존재하지 않는 Column 입니다.");
  }

  @Override
  public String getFieldName() {
    throw new RuntimeException("relation이 존재하지 않는 Column 입니다.");
  }
}
