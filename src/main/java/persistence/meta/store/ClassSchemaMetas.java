package persistence.meta.store;

import java.util.HashMap;
import java.util.Map;
import persistence.meta.SchemaMeta;

public class ClassSchemaMetas {

    private static final Map<Class<?>, SchemaMeta> schemaMetas = new HashMap<>();

    private ClassSchemaMetas() {

    }

    public static SchemaMeta get(Class<?> clazz) {
        if (notContainsKey(clazz)) {
            schemaMetas.put(clazz, new SchemaMeta(clazz));
        }
        return schemaMetas.get(clazz);
    }

    private static boolean notContainsKey(Class<?> clazz) {
        return !schemaMetas.containsKey(clazz);
    }

}
