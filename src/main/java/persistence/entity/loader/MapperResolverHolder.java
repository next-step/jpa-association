package persistence.entity.loader;

import java.util.Arrays;
import java.util.List;

public class MapperResolverHolder {
    public static final List<MapperResolver> MAPPER_RESOLVERS = Arrays.asList(
            new IdFieldMapper(),
            new GeneralFieldMapper(),
            new OneToManyFieldMapper()
    );
}
