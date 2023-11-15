package persistence.sql.metadata;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import persistence.sql.metadata.association.Association;
import persistence.sql.metadata.association.AssociationType;

import java.lang.reflect.Field;
import java.util.Objects;

public class Column {
    private final String name;

    private final Class<?> type;

    private final Constraint constraint;

    private final Association association;

    public Column(Field field) {
        this.name = findName(field);
        this.type = field.getType();
        this.constraint = new Constraint(field);
        this.association = findAssociation(field);
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public Association getAssociation() {
        return association;
    }

    public boolean checkPossibleToBeCreate() {
        return !constraint.isTransient() && !hasAssociation();
    }

    public boolean checkPossibleToBeValue() {
        return !constraint.isTransient() && !constraint.isPrimaryKey() && !hasAssociation();
    }

    public boolean isPrimaryKey() {
        return constraint.isPrimaryKey();
    }

    public boolean isNullable() {
        return constraint.isNullable();
    }

    public String getGeneratedType() {
        return constraint.getGeneratedType();
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
