package persistence.sql.dml;

import persistence.entity.attribute.EntityAttribute;
import persistence.entity.attribute.OneToManyField;
import persistence.entity.attribute.id.IdAttribute;

import java.util.List;
import java.util.stream.Collectors;

public class JoinClause {
    private final StringBuilder conditions = new StringBuilder();

    public JoinClause(String tableName, IdAttribute idAttribute, List<OneToManyField> oneToManyFields) {
        conditions.append(oneToManyFields.stream().map(oneToManyField ->
                prepareJoinDML(tableName, idAttribute, oneToManyField)
        ).collect(Collectors.joining(" ")).trim());
    }

    public String toString() {
        if (conditions.length() == 0) {
            return "";
        }
        return " " + conditions;
    }

    public String prepareJoinDML(String ownerTableName, IdAttribute ownerIdAttribute, OneToManyField oneToManyField) {
        EntityAttribute oneToManyFieldEntityAttribute = oneToManyField.getEntityAttribute();
        return String.format("join %s as %s on %s.%s = %s.%s",
                oneToManyFieldEntityAttribute.getTableName(),
                oneToManyFieldEntityAttribute.getTableName(),
                ownerTableName,
                ownerIdAttribute.getColumnName(),
                oneToManyFieldEntityAttribute.getTableName(),
                oneToManyFieldEntityAttribute.getIdAttribute().getColumnName());
    }
}
