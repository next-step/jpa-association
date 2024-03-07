package persistence.entity;

import jakarta.persistence.FetchType;
import utils.CamelToSnakeCaseConverter;

public class OneToManyAssociationEntity implements AssociationEntity {
    private final JoinEntityColumn joinColumn;
    private final String mappedBy;
    private final FetchType fetchType;


    public OneToManyAssociationEntity(JoinEntityColumn joinColumn, String mappedBy, FetchType fetchType) {
        this.joinColumn = joinColumn;
        this.mappedBy = mappedBy;
        this.fetchType = fetchType;
    }

    @Override
    public String getJoinColumnName() {
        return CamelToSnakeCaseConverter.convert(joinColumn.getName());
    }

    @Override
    public FetchType getFetchType() {
        return fetchType;
    }
}
