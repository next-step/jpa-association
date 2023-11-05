package persistence.association;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Optional;
import persistence.meta.EntityColumn;
import persistence.meta.EntityMeta;

public class OneToManyAssociate {
    private final EntityMeta manyEntityMeta;
    private final Field field;

    private OneToManyAssociate(Class<?> clazz) {
        final Optional<Field> oneToManyField = getOneToManyField(clazz);
        if (oneToManyField.isEmpty()) {
            throw new IllegalArgumentException("해당 엔티티는 OneToMany 관계가 없습니다.");
        }
        this.field = oneToManyField.get();
        this.manyEntityMeta = generateManyEntityMeta((ParameterizedType) field.getGenericType());
    }

    private static Optional<Field> getOneToManyField(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(OneToMany.class))
                .findFirst();
    }


    public static Optional<OneToManyAssociate> from(Class<?> clazz) {
        if (getOneToManyField(clazz).isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new OneToManyAssociate(clazz));
    }

    private EntityMeta generateManyEntityMeta(ParameterizedType genericType) {
        return EntityMeta.from((Class<?>) genericType.getActualTypeArguments()[0]);
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

    public String joinColumnName() {
        if (isHasJoinColumn()) {
            return field.getAnnotation(JoinColumn.class).name();
        }
        return manyEntityMeta.getTableName() + "_" + manyEntityMeta.getPkColumn().getName();
    }

}
