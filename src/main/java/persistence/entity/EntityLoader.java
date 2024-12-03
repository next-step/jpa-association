package persistence.entity;

import jdbc.EntityRowMapper;
import jdbc.JdbcTemplate;
import persistence.sql.component.ColumnInfo;
import persistence.sql.component.ConditionBuilder;
import persistence.sql.component.JoinCondition;
import persistence.sql.component.JoinConditionBuilder;
import persistence.sql.component.JoinInfo;
import persistence.sql.component.JoinType;
import persistence.sql.component.TableInfo;
import persistence.sql.dml.select.SelectQueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EntityLoader {
    private final JdbcTemplate jdbcTemplate;

    public EntityLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Object find(Class<?> entityClass, Long id) {
        TableInfo fromTable = TableInfo.from(entityClass);
        ColumnInfo idColumn = fromTable.getIdColumn();

        SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder()
                .fromTableInfo(fromTable);

        List<JoinCondition> joinConditions = getJoinConditions(fromTable);
        if (!joinConditions.isEmpty()) {
            selectQueryBuilder
                    .joinConditions(joinConditions);
        }

        selectQueryBuilder
                .whereCondition(
                        new ConditionBuilder()
                                .columnInfo(idColumn)
                                .values(Collections.singletonList(id.toString()))
                                .build()
                );

        String query = selectQueryBuilder.build().toString();
        return jdbcTemplate.queryForObject(query, new EntityRowMapper<>(entityClass));
    }

    private List<JoinCondition> getJoinConditions(TableInfo tableInfo) {
        List<JoinCondition> joinConditions = new ArrayList<>();

        List<JoinInfo> joinInfos = tableInfo.getJoinInfos();
        for (JoinInfo joinInfo : joinInfos) {
            JoinCondition joinCondition = new JoinConditionBuilder()
                    .joinType(JoinType.LEFT_JOIN)
                    .tableInfo(joinInfo.getTargetColumnInfo().getTableInfo())
                    .sourceColumnInfo(joinInfo.getSourceColumnInfo())
                    .targetColumnInfo(joinInfo.getTargetColumnInfo())
                    .build();
            joinConditions.add(joinCondition);
        }

        return joinConditions;
    }
}
