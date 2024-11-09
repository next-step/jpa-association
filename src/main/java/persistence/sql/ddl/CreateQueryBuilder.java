package persistence.sql.ddl;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinTable;
import persistence.sql.Dialect;
import persistence.sql.entity.EntityJoin;
import persistence.sql.exception.ExceptionMessage;
import persistence.sql.exception.RequiredClassException;
import persistence.sql.model.TableName;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CreateQueryBuilder implements QueryBuilder {
    private static final String LEFT_PARENTHESIS = "(";
    private static final String RIGHT_PARENTHESIS = ")";
    private static final String SPACE = " ";

    private final Class<?> clazz;
    private final Dialect dialect;
    private final List<String> createQueries = new ArrayList<>();

    public CreateQueryBuilder(Class<?> clazz, Dialect dialect) {
        if (clazz == null) {
            throw new RequiredClassException(ExceptionMessage.REQUIRED_CLASS);
        }

        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("Entity 클래스가 아닙니다.");
        }

        this.clazz = clazz;
        this.dialect = dialect;
    }

    @Override
    public String build() {
        StringBuilder makeStringBuilder = new StringBuilder();
        makeStringBuilder.append(createTableIfNotExistsStatement(this.clazz));
        makeStringBuilder.append(LEFT_PARENTHESIS);
        makeStringBuilder.append(generateColumnDefinitions(this.clazz));
        makeStringBuilder.append(RIGHT_PARENTHESIS);
        createQueries.add(makeStringBuilder.toString());


        return String.join(";", createQueries);
    }

    private String createTableIfNotExistsStatement(Class<?> clazz) {
        TableName tableName = new TableName(clazz);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CREATE TABLE IF NOT EXISTS");
        stringBuilder.append(SPACE);
        stringBuilder.append(tableName.getValue());

        return stringBuilder.toString();
    }

    private String generateColumnDefinitions(Class<?> clazz) {
        DDLColumn ddlColumns = new DDLColumn(clazz.getDeclaredFields(), dialect);
        return ddlColumns.makeColumnsDDL();
    }


}
