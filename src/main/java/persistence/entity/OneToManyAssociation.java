package persistence.entity;

import jakarta.persistence.FetchType;
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
    private final Field oneField;
    private final FetchType fetchType;

    private OneToManyAssociation(Field oneField, EntityColumn pkColumn) {
        if (!hasOneToManyField(oneField)) {
            throw new IllegalArgumentException("해당 필드는 OneToMany 어노테이션이 있어야 합니다.");
        }

        this.oneField = oneField;
        this.pkColumn = pkColumn;
        this.fetchType = oneField.getAnnotation(OneToMany.class).fetch();
        final Class<?> manyAssociationType = getFieldGenericType(oneField);
        final ForeignerColumn foreignerColumn = ForeignerColumn.of(manyAssociationType, pkColumn.getField(),
                joinColumnName());
        this.manyEntityMeta = EntityMeta.createManyEntityMeta(manyAssociationType, foreignerColumn);

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
        return oneField.isAnnotationPresent(JoinColumn.class);
    }

    private String joinColumnName() {
        final JoinColumn joinColumn = oneField.getAnnotation(JoinColumn.class);
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

    public Field getOneField() {
        return oneField;
    }

    public boolean isLazy() {
        return FetchType.LAZY.equals(fetchType);
    }


    private static boolean hasOneToManyField(Field it) {
        return it.isAnnotationPresent(OneToMany.class);
    }


}
