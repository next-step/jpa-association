package persistence.entity.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import persistence.meta.EntityMeta;

public class EntityOneToManyPath {
    private static final int START_PATH_LEVEL = 0;

    private final Map<Integer, List<Object>> path = new HashMap<>();

    public EntityOneToManyPath(EntityMeta entityMeta, Object rootEntity) {
        path.put(START_PATH_LEVEL, List.of(rootEntity));
        pathSearch(entityMeta, START_PATH_LEVEL );
    }

    private void pathSearch(EntityMeta entityMeta, int level) {
        if (level != START_PATH_LEVEL) {
            path.put(level, new ArrayList<>());
        }

        if (!hasNextPath(entityMeta)) {
            return;
        }

        final EntityMeta manyEntityMeta = entityMeta.getOneToManyAssociation().getManyEntityMeta();
        pathSearch(manyEntityMeta, level + 1);
    }

    public List<Object> get(int level) {
        return path.get(level);
    }

    public Object getRootInstance() {
        return path.get(START_PATH_LEVEL).get(0);
    }

    public int totalLevel() {
        return path.size();
    }

    private boolean hasNextPath(EntityMeta entityMeta) {
        return entityMeta.hasOneToManyAssociation();
    }

}
