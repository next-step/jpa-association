package persistence.core;

import jakarta.persistence.FetchType;

public interface EntityAssociatedColumn extends EntityColumn {
    FetchType getFetchType();

    Class<?> getJoinColumnType();
}
