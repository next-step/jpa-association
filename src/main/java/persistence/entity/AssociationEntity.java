package persistence.entity;

import jakarta.persistence.FetchType;

public interface AssociationEntity {

    String getJoinColumnName();

    String getJoinFieldName();

    boolean isLazy();
}
