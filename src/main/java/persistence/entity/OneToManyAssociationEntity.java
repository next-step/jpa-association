package persistence.entity;

import jakarta.persistence.FetchType;
import persistence.sql.column.JoinEntityColumn;
import utils.CamelToSnakeCaseConverter;

public class OneToManyAssociationEntity implements AssociationEntity {
    private final JoinEntityColumn joinColumn;
    private final FetchType fetchType;


    public OneToManyAssociationEntity(JoinEntityColumn joinColumn, FetchType fetchType) {
        this.joinColumn = joinColumn;
        this.fetchType = fetchType;
    }

    @Override
    public String getJoinColumnName() {
        return CamelToSnakeCaseConverter.convert(joinColumn.getColumnName());
    }

    @Override
    public String getJoinFieldName() {
        return joinColumn.getFieldName();
    }

    @Override
    public boolean isLazy() {
        return FetchType.LAZY.equals(fetchType);
    }

}
