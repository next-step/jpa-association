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
    private final Field mappingField;
    private final FetchType fetchType;
    private final Class<?> manyAssociationClassType;

    private OneToManyAssociation(Field mappingField, EntityColumn pkColumn) {
        if (!hasOneToManyField(mappingField)) {
            throw new IllegalArgumentException("해당 필드는 OneToMany 어노테이션이 있어야 합니다.");
        }

        this.pkColumn = pkColumn;
        this.mappingField = mappingField;
        this.manyAssociationClassType = getManyAssociationClassType(mappingField);
        this.fetchType = mappingField.getAnnotation(OneToMany.class).fetch();

        final ForeignerColumn foreignerColumn = ForeignerColumn.of(manyAssociationClassType, pkColumn.getField(),
                joinColumnName());
        this.manyEntityMeta = EntityMeta.createManyEntityMeta(manyAssociationClassType, foreignerColumn);

    }

    public static OneToManyAssociation createOneToMayAssociationByField(Field oneField, EntityColumn pkColumn) {
        return new OneToManyAssociation(oneField, pkColumn);
    }

    public static OneToManyAssociation createOneToMayAssociationByClass(Class<?> clazz, EntityMeta entityMeta) {
        return createOneToMayAssociationByField(getMappingField(clazz), entityMeta.getPkColumn());
    }

    private static Field getMappingField(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(OneToManyAssociation::hasOneToManyField)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 클래스는 OneToMany 어노테이션이 있어야 합니다."));
    }

    public EntityMeta getManyEntityMeta() {
        return manyEntityMeta;
    }

    public EntityColumn getManyPkColumn() {
        return manyEntityMeta.getPkColumn();
    }

    public boolean isHasJoinColumn() {
        return mappingField.isAnnotationPresent(JoinColumn.class);
    }

    private Class<?> getManyAssociationClassType(Field mappingField) {
        return (Class<?>) ((ParameterizedType) mappingField.getGenericType()).getActualTypeArguments()[0];
    }

    private String joinColumnName() {
        final JoinColumn joinColumn = mappingField.getAnnotation(JoinColumn.class);
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

    public Class<?> getManyAssociationClassType() {
        return manyAssociationClassType;
    }


    public Field getMappingField() {
        return mappingField;
    }

    public boolean isLazy() {
        return FetchType.LAZY.equals(fetchType);
    }


    private static boolean hasOneToManyField(Field it) {
        return it.isAnnotationPresent(OneToMany.class);
    }


}
