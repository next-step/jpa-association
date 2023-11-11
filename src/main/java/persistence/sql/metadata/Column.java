package persistence.sql.metadata;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import persistence.dialect.Dialect;
import persistence.sql.metadata.association.Association;
import persistence.sql.metadata.association.AssociationType;

import java.lang.reflect.Field;
import java.util.Objects;

public class Column {
    private final String name;

    private final Class<?> type;

    private final Constraint constraint;

    private final boolean isTransient;

    private final String convertedValue;

    private final Object value;

    private final Association association;

    public Column(Field field, Object value) {
        this.name = findName(field);
        this.type = field.getType();
        this.constraint = new Constraint(field);
        this.isTransient = field.isAnnotationPresent(Transient.class);
        this.convertedValue = convertValueToString(value);
        this.value = value;
        this.association = findAssociation(field);
    }

    public String getName() {
        return name;
    }

    public String getConvertedValue() {
        return convertedValue;
    }

    public Object getValue() {
        return value;
    }

    public Association getAssociation() {
        return association;
    }

    public String buildColumnsWithConstraint(Dialect dialect) {
        if(hasAssociation()) {
            return null;
        }

        return new StringBuilder()
                .append(name + " " + findType(dialect))
                .append(constraint.buildNullable())
                .append(dialect.getGeneratedStrategy(constraint.getGeneratedType()))
                .append(constraint.buildPrimaryKey())
                .toString();
    }

    public boolean checkPossibleToBeValue() {
        if("null".equals(value) && isNotNullable()) {
            return false;
        }

        return !isTransient && !constraint.isPrimaryKey() && !hasAssociation();
    }

    public boolean checkPossibleToBeCreate() {
        return !isTransient && !hasAssociation();
    }

    public boolean isPrimaryKey() {
        return constraint.isPrimaryKey();
    }

    public boolean isNotNullable() {
        return !constraint.isNullable();
    }

    private String findName(Field field) {
        if(!field.isAnnotationPresent(jakarta.persistence.Column.class)) {
            return field.getName();
        }

        jakarta.persistence.Column column = field.getDeclaredAnnotation(jakarta.persistence.Column.class);

        if("".equals(column.name())) {
            return field.getName();
        }

        return column.name();
    }

    private String findType(Dialect dialect) {
        return dialect.getColumnType(type);
    }

    private String convertValueToString(Object value) {
        if(type.equals(String.class) && value != null) {
            value = "'" + value + "'";
        }

        return String.valueOf(value);
    }

    private Association findAssociation(Field field) {
        if(field.isAnnotationPresent(OneToMany.class)) {
            return AssociationType.OneToMany.createdAssociation(field);
        }

        if(field.isAnnotationPresent(ManyToOne.class)) {
            return AssociationType.ManyToOne.createdAssociation(field);
        }

        return null;
    }

    public boolean hasAssociation() {
        return Objects.nonNull(association);
    }
}
