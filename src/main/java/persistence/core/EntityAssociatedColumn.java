package persistence.core;

import jakarta.persistence.FetchType;

import java.util.List;

public interface EntityAssociatedColumn extends EntityColumn {
    List<String> getAssociatedEntityColumnNamesWithAlias();

    String getNameWithAliasAssociatedEntity();

    String getAssociatedEntityTableName();

    FetchType getFetchType();

    default boolean isFetchTypeLazy() {
        return this.getFetchType().equals(FetchType.LAZY);
    }

    Class<?> getJoinColumnType();
}
