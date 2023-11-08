package persistence.association;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import persistence.meta.EntityColumn;
import persistence.meta.EntityMeta;
import persistence.meta.ForeignerColumn;

public class OneToManyAssociation {
    private final EntityColumn pkColumn;
    private final EntityMeta manyEntityMeta;
    private final Field field;

    private OneToManyAssociation(Field oneField, EntityColumn pkColumn) {
        if (!hasOneToManyField(oneField)) {
            throw new IllegalArgumentException("해당 필드는 OneToMany 어노테이션이 있어야 합니다.");
        }

        this.field = oneField;
        this.pkColumn = pkColumn;
        final Class<?> manyAssociationType = getFieldGenericType(oneField);
        final ForeignerColumn foreignerColumn = ForeignerColumn.of(manyAssociationType, pkColumn.getField(), joinColumnName());
        this.manyEntityMeta = EntityMeta.createManyEntityMeta(manyAssociationType , foreignerColumn);

    }
    public static OneToManyAssociation of(Class<?> clazz, EntityMeta entityMeta) {
        return new OneToManyAssociation(getOneField(clazz), entityMeta.getPkColumn());
    }

    public static OneToManyAssociation of(Field oneField, EntityColumn pkColumn) {
        return new OneToManyAssociation(oneField, pkColumn);
    }

    public EntityMeta getManyEntityMeta() {
        return manyEntityMeta;
    }

    public EntityColumn getPkManyColumn() {
        return manyEntityMeta.getPkColumn();
    }
    public boolean isHasJoinColumn() {
        return field.isAnnotationPresent(JoinColumn.class);
    }


    private String joinColumnName() {
        final JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
        if (joinColumn == null || joinColumn.name().isBlank()) {
            return pkColumn.getName();
        }
        return joinColumn.name();
    }

    public String foreignerColumnName() {
        return manyEntityMeta
                .getForeignerColumn()
                .getName();
    }

    private Class<?> getFieldGenericType(Field field) {
        return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
    }

    private static Field getOneField(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(OneToManyAssociation::hasOneToManyField)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 클래스는 OneToMany 어노테이션이 있어야 합니다."));
    }

    private static boolean hasOneToManyField(Field it) {
        return it.isAnnotationPresent(OneToMany.class);
    }


}
