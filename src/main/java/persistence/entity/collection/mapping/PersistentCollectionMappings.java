package persistence.entity.collection.mapping;

import java.util.ArrayList;
import java.util.List;

public class PersistentCollectionMappings {

    private static PersistentCollectionMappings INSTANCE;
    private final List<PersistentCollectionMapping> mappings;

    private PersistentCollectionMappings() {
        this.mappings = init();
    }

    public static synchronized PersistentCollectionMappings getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PersistentCollectionMappings();
        }

        return INSTANCE;
    }

    private List<PersistentCollectionMapping> init() {
        final ArrayList<PersistentCollectionMapping> mappings = new ArrayList<>();
        mappings.add(new PersistentBagMapping());
        mappings.add(new PersistentSetMapping());

        return mappings;
    }

    public PersistentCollectionMapping findMapping(final Class<?> collectionType) {
        return mappings.stream()
                .filter(mappings -> mappings.support(collectionType))
                .findFirst().orElseThrow();
    }
}
