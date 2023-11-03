package persistence.sql.dml;

import persistence.entity.attribute.OneToManyField;

import java.util.List;
import java.util.stream.Collectors;

public class JoinClause {
    private final StringBuilder conditions = new StringBuilder();

    public JoinClause(List<OneToManyField> oneToManyFields) {
        conditions.append(oneToManyFields.stream().map(
                OneToManyField::prepareJoinDML
        ).collect(Collectors.joining(" ")).trim());
    }

    public String toString() {
        if (conditions.length() == 0) {
            return "";
        }
        return " " + conditions;
    }
}
