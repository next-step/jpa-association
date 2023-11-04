package persistence.core;

import jakarta.persistence.FetchType;

public interface EntityAssociatedColumn extends EntityColumn {
    FetchType getFetchType();

    default boolean isFetchTypeLazy() {
        return this.getFetchType().equals(FetchType.LAZY);
    };

    Class<?> getJoinColumnType();
}
