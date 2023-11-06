package persistence.entity.loader;

import java.util.Arrays;
import java.util.List;

public class MapperResolverHolder {
    public static final List<MapperResolver> MAPPER_RESOLVERS = Arrays.asList(
            new IdFieldMapper(),
            new GeneralFieldMapper()
    );

    public static final List<MapperResolver> COLLECTION_MAPPER_RESOLVERS = Arrays.asList(
            new OneToManyFieldMapper()
    );
}
