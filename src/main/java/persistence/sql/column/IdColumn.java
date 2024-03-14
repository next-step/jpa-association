package persistence.sql.column;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import persistence.sql.dialect.Dialect;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;

public class IdColumn implements Column {

    private static final String PK_FORMAT = "%s %s %s";
    private static final String PRIMARY_KEY = "primary key";

    private final GeneralColumn generalColumn;
    private final Field field;

    public IdColumn(Field[] fields) {
        this(getIdField(fields), GeneralColumn::new);
    }

    public IdColumn(Object object) {
        this(getIdField(object.getClass().getDeclaredFields()),
                field -> new GeneralColumn(object, field));
    }

    private static Field getIdField(Field[] object) {
        return Arrays.stream(object)
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("[INFO] No @Id annotation"));
    }

    private IdColumn(Field idField, Function<Field, GeneralColumn> generalColumnCreator) {
        validateGeneratedValue(idField);
        this.field = idField;
        this.generalColumn = generalColumnCreator.apply(idField);
    }


    private void validateGeneratedValue(Field field) {
        if (!field.isAnnotationPresent(GeneratedValue.class)) {
            throw new IllegalArgumentException("[INFO] No @GeneratedValue annotation");
        }
    }

    public boolean isNull() {
        return getValue() == null;
    }

    public IdGeneratedStrategy getIdGeneratedStrategy(Dialect dialect) {
        GeneratedValue annotation = field.getAnnotation(GeneratedValue.class);
        return dialect.getIdGeneratedStrategy(annotation.strategy());
    }

    public String getIdGeneratedDefinition(Dialect dialect) {
        return getIdGeneratedStrategy(dialect).getValue();
    }
    public <T> T getValue(){
        return (T) generalColumn.getValue();
    }

    @Override
    public String getDefinition(Dialect dialect) {
        return String.format(PK_FORMAT,
                generalColumn.getDefinition(dialect),
                getIdGeneratedDefinition(dialect),
                PRIMARY_KEY);
    }

    @Override
    public String getName() {
        return generalColumn.getName();
    }

    @Override
    public String getFieldName() {
        return generalColumn.getFieldName();
    }

    @Override
    public Field getField() {
        return field;
    }

}
