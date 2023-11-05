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
    private final boolean hasJoinColumn;

    private OneToManyAssociate(Field field) {
        this.manyEntityMeta = generateManyEntityMeta((ParameterizedType) field.getGenericType());
        this.hasJoinColumn = field.isAnnotationPresent(JoinColumn.class);
    }

    public static Optional<OneToManyAssociate> from(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(OneToMany.class))
                .findFirst()
                .map(OneToManyAssociate::new);
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
        return hasJoinColumn;
    }
}
