package builder.dml.builder;

import builder.dml.QueryBuildUtil;
import builder.dml.EntityData;
import builder.dml.JoinEntityData;

public class SelectByIdQueryBuilder {

    public String buildQuery(EntityData entityData) {
        return findByIdQuery(entityData);
    }
    //findAll 쿼리문을 생성한다.
    private String findByIdQuery(EntityData entityData) {

        if (entityData.checkJoin()) {
            JoinEntityData joinEntityData = entityData.getJoinEntity().getJoinEntityData().getFirst();
            return new SelectQueryBuilder()
                    .select(QueryBuildUtil.getColumnNames(entityData))
                    .from(QueryBuildUtil.getTableName(entityData))
                    .join(QueryBuildUtil.getContainAliasTableName(joinEntityData.getTableName(), joinEntityData.getAlias()))
                    .on(QueryBuildUtil.getContainAliasColumnName(entityData.getPkNm(), entityData.getAlias()), QueryBuildUtil.getContainAliasColumnName(joinEntityData.getJoinColumnName(), joinEntityData.getAlias()))
                    .where(QueryBuildUtil.getContainAliasColumnName(entityData.getPkNm(), entityData.getAlias()), String.valueOf(entityData.wrapString()))
                    .build();
        }

        return new SelectQueryBuilder()
                .select(QueryBuildUtil.getColumnNames(entityData))
                .from(QueryBuildUtil.getTableName(entityData))
                .where(entityData.getPkNm(), String.valueOf(entityData.wrapString()))
                .build();
    }

}
