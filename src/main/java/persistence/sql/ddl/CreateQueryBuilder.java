package persistence.sql.ddl;

import dialect.Dialect;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import pojo.ColumnField;
import pojo.EntityMetaData;
import pojo.FieldInfos;
import pojo.IdField;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static constants.CommonConstants.COMMA;
import static constants.CommonConstants.SPACE;
import static pojo.Constraints.NOT_NULL;
import static pojo.Constraints.PRIMARY_KEY;

public class CreateQueryBuilder {

    private static final String CREATE_TABLE_QUERY = "CREATE TABLE %s ( %s );";

    private final Dialect dialect;
    private final EntityMetaData entityMetaData;

    public CreateQueryBuilder(Dialect dialect, EntityMetaData entityMetaData) {
        this.dialect = dialect;
        this.entityMetaData = entityMetaData;
    }

    public String createTable(Object entity) {
        FieldInfos fieldInfos = new FieldInfos(entity.getClass().getDeclaredFields());
        return String.format(CREATE_TABLE_QUERY, entityMetaData.getEntityName(), createClause(fieldInfos.getFieldDataList(), entity));
    }

    //create 시에 JoinColumn 어노테이션이 있는 필드는 -> JoinColumn 의 name 을 그 필드 객체 생성 시 만들어줘야 한다.
    private String createClause(List<Field> fields, Object entity) {
        return fields.stream()
                .filter(field -> !field.isAnnotationPresent(Transient.class))
                .map(field -> getFieldCreateClause(field, entity))
                .collect(Collectors.joining(COMMA))
                .replaceAll(",[\\s,]*$", "");
    }

    private String getFieldCreateClause(Field field, Object entity) {
        boolean varcharType = dialect.getJavaTypeByClass(field.getType()).equals(Types.VARCHAR);

        if (field.isAnnotationPresent(Id.class)) {
            IdField idField = new IdField(field, entity);

            return Stream.of(idField.getFieldNameData(), dialect.getTypeToStr(field.getType()),
                            NOT_NULL.getName(), PRIMARY_KEY.getName(),
                            idField.getGenerationTypeStrategy())
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(SPACE));
        }
        ColumnField columnField = new ColumnField(field, entity);

        return Stream.of(columnField.getFieldNameData(), dialect.getTypeToStr(field.getType()),
                        columnField.getFieldLength(varcharType), columnField.getColumnNullConstraint())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(SPACE));
    }
}
