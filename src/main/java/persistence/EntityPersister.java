package persistence;

import builder.dml.EntityData;
import builder.dml.JoinEntityData;
import builder.dml.builder.DeleteQueryBuilder;
import builder.dml.builder.InsertQueryBuilder;
import builder.dml.builder.UpdateQueryBuilder;
import jdbc.JdbcTemplate;

public class EntityPersister {

    private final JdbcTemplate jdbcTemplate;

    private final InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder();
    private final UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder();
    private final DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder();

    public EntityPersister(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //데이터를 반영한다.
    public void persist(EntityData entityData) {
        jdbcTemplate.execute(insertQueryBuilder.buildQuery(entityData.getTableName(), entityData.getEntityColumn()));
        if (entityData.checkJoin()) {
            persistJoin(entityData);
        }
    }

    //데이터를 수정한다.
    public void merge(EntityData entityData) {
        jdbcTemplate.execute(updateQueryBuilder.buildQuery(entityData));
    }

    //데이터를 제거한다.
    public void remove(EntityData entityData) {
        jdbcTemplate.execute(deleteQueryBuilder.buildQuery(entityData));
    }

    private void persistJoin(EntityData entityData) {
        for(JoinEntityData joinEntityData : entityData.getJoinEntity().getJoinEntityData()) {
            jdbcTemplate.execute(insertQueryBuilder.buildQuery(
                    joinEntityData.getTableName(),
                    joinEntityData.getJoinColumnData())
            );
        }
    }

}
