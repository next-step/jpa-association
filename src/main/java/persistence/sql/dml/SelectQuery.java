package persistence.sql.dml;

import persistence.entity.EntityMeta;
import persistence.sql.common.meta.Columns;
import persistence.sql.common.meta.JoinColumn;
import persistence.sql.common.meta.TableName;
import utils.ConditionUtils;

import java.util.List;

class SelectQuery {

    private static final String DEFAULT_SELECT_COLUMN_QUERY = "SELECT %s FROM %s";
    private static final String DEFAULT_JOIN_QUERY = "JOIN %s ON %s = %s";

    private String methodName;
    private TableName tableName;
    private Columns columns;
    private JoinColumn joinColumn;
    private Object arg;

    SelectQuery() { }

    String getAll(String methodName, TableName tableName, Columns columns) {
        this.methodName = methodName;
        this.tableName = tableName;
        this.columns = columns;

        return combine();
    }

    String get(EntityMeta entityMeta, Object arg) {
        this.methodName = entityMeta.getMethodName();
        this.tableName = entityMeta.getTableName();
        this.columns = entityMeta.getColumns();
        this.joinColumn = entityMeta.getJoinColumn();
        this.arg = arg;

        return combine();
    }

    public String combine() {
        String query = parseFiled();

        if(isJoin()) {
            query = String.join(" ", query, parseJoin());
        }

        if (isCondition()) {
            query = String.join(" ", query, parseWhere());
        }

        return query;
    }

    private String parseFiled() {
        return String.format(DEFAULT_SELECT_COLUMN_QUERY, parseSelectFiled(), tableName.getName() + " " + tableName.getAlias());
    }

    private String parseJoin() {
        return String.format(DEFAULT_JOIN_QUERY, joinColumn.getTableName() + " " + joinColumn.getTableAlias(), tableName.getAlias() + "." + columns.getIdName(), joinColumn.getTableAlias() + "." + joinColumn.getJoinColumn());
    }

    private String parseSelectFiled() {
        if(joinColumn == null) {
            return columns.getColumnsWithComma(tableName.getAlias());
        }

        if(!joinColumn.isEager()) {
            //TODO: LAZY 작성 필요
        }
        
        return String.join(", ", columns.getColumnsWithComma(tableName.getAlias()), joinColumn.getColumns().getColumnsWithComma(joinColumn.getTableAlias()));
    }

    private String parseWhere() {
        //TODO: 메소드명을 읽고, 필드 리스트에서 이를 꺼내오는 방식으로 개선이 필요할 것 같음
        String conditionText = methodName.replace("find", "").replace("By", "");
        List<String> conditionList = ConditionUtils.getWordsFromCamelCase(conditionText);

        String condition = ConditionBuilder.getCondition(conditionList, arg, tableName.getAlias());
        return condition.replace(".id ", setConditionField("id") + " ");
    }

    private boolean isCondition() {
        return methodName.contains("By");
    }

    private boolean isJoin() {
        return joinColumn != null;
    }

    private String setConditionField(String word) {
        if (word.equals("id")) {
            word = "." + columns.getIdName();
        }
        return word;
    }
}
