package builder.dml.builder;

import builder.dml.QueryBuildUtil;
import builder.dml.EntityData;
import builder.dml.JoinEntityData;

public class SelectAllQueryBuilder {

    public String buildQuery(EntityData entityData) {
        return findAllQuery(entityData);
    }

    //findAll 쿼리문을 생성한다.
    private String findAllQuery(EntityData entityData) {

        if (entityData.checkJoinAndEager()) {
            JoinEntityData joinEntityData = entityData.getJoinEntity().getJoinEntityData().getFirst();
            return new SelectQueryBuilder()
                    .select(QueryBuildUtil.getColumnNames(entityData))
                    .from(QueryBuildUtil.getTableName(entityData))
                    .join(QueryBuildUtil.getContainAliasTableName(joinEntityData.getTableName(), joinEntityData.getAlias()))
                    .on(
                            QueryBuildUtil.getContainAliasColumnName(entityData.getPkNm(), entityData.getAlias()),
                            QueryBuildUtil.getContainAliasColumnName(joinEntityData.getJoinColumnName(), joinEntityData.getAlias())
                    )
                    .build();
        }

        return new SelectQueryBuilder()
                .select(QueryBuildUtil.getColumnNames(entityData))
                .from(QueryBuildUtil.getTableName(entityData))
                .build();
    }

}
