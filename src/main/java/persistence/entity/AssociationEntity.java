package persistence.entity;

public interface AssociationEntity {

    String getJoinColumnName();

    String getJoinFieldName();

    boolean isLazy();

    <T> void setAssociationColumn(T rootEntity, Object o);
}
