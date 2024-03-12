package persistence.entity;

import persistence.sql.column.JoinEntityColumn;

public interface AssociationEntity {

    String getJoinFieldName();

    boolean isLazy();

    <T> void setAssociationColumn(T rootEntity, Object o);

    JoinEntityColumn getJoinEntityColumn();
}
