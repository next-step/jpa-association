package persistence.entity;

import jakarta.persistence.FetchType;

public interface AssociationEntity {

    FetchType getFetchType();

    String getJoinColumnName();
}
