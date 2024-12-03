package persistence.sql.dml.select;

import persistence.sql.component.ColumnInfo;
import persistence.sql.component.Condition;
import persistence.sql.component.JoinCondition;
import persistence.sql.component.TableInfo;

import java.util.List;

public class SelectQuery {
    private List<ColumnInfo> selectColumnInfos;
    private TableInfo fromTableInfo;
    private Condition whereCondition;
    private List<JoinCondition> joinConditions;

    public SelectQuery(TableInfo fromTableInfo, Condition whereCondition) {
        this.fromTableInfo = fromTableInfo;
        this.whereCondition = whereCondition;
    }

    public void setSelectColumnInfos(List<ColumnInfo> selectColumnInfos) {
        this.selectColumnInfos = selectColumnInfos;
    }

    public void setJoinConditions(List<JoinCondition> joinConditions) {
        this.joinConditions = joinConditions;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(getSelectClause())
                .append(getFromClause());
        if (joinConditions != null) {
            stringBuilder.append(getJoinClauses());
        }
        if (whereCondition != null) {
            stringBuilder.append(getWhereClause());
        }

        stringBuilder.setLength(stringBuilder.length() - 1);
        stringBuilder.append(";");
        return stringBuilder.toString();
    }

    private String getSelectClause() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("select ");

        if (selectColumnInfos == null) {
            stringBuilder.append("* ");
            return stringBuilder.toString();
        }

        selectColumnInfos.forEach(
                columnInfo -> stringBuilder
                        .append(columnInfo.getFullName())
                        .append(", ")
        );
        stringBuilder.setLength(stringBuilder.length() - 2);
        stringBuilder.append(" ");
        return stringBuilder.toString();
    }

    private String getFromClause() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("from ")
                .append(fromTableInfo.getTableName())
                .append(" ");
        return stringBuilder.toString();
    }

    private String getWhereClause() {
        StringBuilder stringBuilder = new StringBuilder();
        ColumnInfo columnInfo = whereCondition.getColumnInfo();

        stringBuilder
                .append("where ")
                .append(columnInfo.getFullName())
                .append(" ");

        List<String> values = whereCondition.getValues();
        if (values.isEmpty()) {
            stringBuilder.append("= null");
        } else if (values.size() == 1) {
            stringBuilder.append("= ").append(values.get(0));
        } else {
            stringBuilder.append("in (");
            values.forEach(value -> stringBuilder.append(value).append(", "));
            stringBuilder.setLength(stringBuilder.length() - 2);
            stringBuilder.append(")");
        }
        /* todo : andCondition and orCondition */
        stringBuilder.append(" ");
        return stringBuilder.toString();
    }

    private String getJoinClauses() {
        StringBuilder stringBuilder = new StringBuilder();
        joinConditions.forEach(
                joinCondition -> stringBuilder.append(getSingleJoinClause(joinCondition))
        );
        return stringBuilder.toString();
    }

    private String getSingleJoinClause(JoinCondition joinCondition) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(joinCondition.getJoinType().getValue())
                .append(" ")
                .append(joinCondition.getTableInfo().getTableName())
                .append(" on ");
        ColumnInfo sourceColumnInfo = joinCondition.getSourceColumnInfo();
        stringBuilder
                .append(sourceColumnInfo.getFullName())
                .append(" = ");
        ColumnInfo targetColumnInfo = joinCondition.getTargetColumnInfo();
        stringBuilder
                .append(targetColumnInfo.getFullName())
                .append(" ");
        return stringBuilder.toString();
    }
}
