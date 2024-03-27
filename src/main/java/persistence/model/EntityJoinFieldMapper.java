package persistence.model;

import java.lang.reflect.Field;
import java.util.Arrays;

public class EntityJoinFieldMapper {
    private final EntityJoinFieldMapping[] joinFieldMappings = initJoinFieldMappings();

    private EntityJoinFieldMapping[] initJoinFieldMappings() {
        return new EntityJoinFieldMapping[]{new EntityManyToManyFieldMapping(), new EntityManyToOneFieldMapping(), new EntityOneToManyFieldMapping(), new EntityOneToOneFieldMapping()};
    }

    public EntityJoinFieldMapping findJoinFieldMapping(final Field field) {
        return Arrays.stream(this.joinFieldMappings).filter(mapping -> mapping.support(field))
                .findFirst()
                .orElseThrow();
    }
}
