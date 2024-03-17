package database.mapping;

import java.util.ArrayList;
import java.util.List;

/**
 * 임시로 모든 엔터티를 목록으로 담아서 여기저기서 사용합니다.
 */
public class AllEntities {
    private static final AllEntities INSTANCE = new AllEntities();

    private final List<Class<?>> entities;

    public AllEntities() {
        entities = new ArrayList<>();
    }

    public static void register(Class<?> clazz) {
        if (INSTANCE.entities.contains(clazz)) return;

        INSTANCE.entities.add(clazz);
    }

    public static List<Class<?>> getEntities() {
        return INSTANCE.entities;
    }
}
