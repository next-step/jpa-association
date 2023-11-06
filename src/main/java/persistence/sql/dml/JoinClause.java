package persistence.sql.dml;

import jakarta.persistence.FetchType;
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
                ).filter(joinDML -> !joinDML.isBlank())
                .collect(Collectors.joining(" ")).trim());
    }

    public String prepareDML() {
        if (conditions.length() == 0) {
            return "";
        }
        return " " + conditions;
    }

    public String prepareJoinDML(String ownerTableName, IdAttribute ownerIdAttribute, OneToManyField oneToManyField) {
        EntityAttribute oneToManyFieldEntityAttribute = oneToManyField.getEntityAttribute();

        if (oneToManyField.getFetchType() == FetchType.LAZY) {
            return "";
        }

        return String.format("join %s as %s on %s.%s = %s.%s",
                oneToManyFieldEntityAttribute.getTableName(),
                oneToManyFieldEntityAttribute.getTableName(),
                ownerTableName,
                ownerIdAttribute.getColumnName(),
                oneToManyFieldEntityAttribute.getTableName(),
                oneToManyField.getJoinColumnName());
    }
}
