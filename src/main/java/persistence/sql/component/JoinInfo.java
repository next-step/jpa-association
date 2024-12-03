package persistence.sql.component;

import java.lang.reflect.Field;

public class JoinInfo {
    private Field sourceTableJoinField;
    private ColumnInfo sourceColumnInfo;
    private ColumnInfo targetColumnInfo;

    private JoinInfo(Field sourceTableJoinField, ColumnInfo sourceColumnInfo, ColumnInfo targetColumnInfo) {
        this.sourceTableJoinField = sourceTableJoinField;
        this.sourceColumnInfo = sourceColumnInfo;
        this.targetColumnInfo = targetColumnInfo;
    }

    public static JoinInfo of(Field sourceTableJoinField, ColumnInfo sourceColumnInfo, ColumnInfo targetColumnInfo) {
        return new JoinInfo(sourceTableJoinField, sourceColumnInfo, targetColumnInfo);
    }

    public Field getSourceTableJoinField() {
        return sourceTableJoinField;
    }

    public ColumnInfo getSourceColumnInfo() {
        return sourceColumnInfo;
    }

    public ColumnInfo getTargetColumnInfo() {
        return targetColumnInfo;
    }
}
