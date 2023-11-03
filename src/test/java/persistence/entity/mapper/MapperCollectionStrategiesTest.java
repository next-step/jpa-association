package persistence.entity.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.exception.PersistenceException;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MapperCollectionStrategiesTest {

    private MapperCollectionStrategies mapperCollectionStrategies;

    @BeforeEach
    void setUp() {
        mapperCollectionStrategies = MapperCollectionStrategies.getInstance();
    }

    @Test
    @DisplayName("MapperCollectionStrategies.createCollectionBy(List.class) 를 이용해 ArrayList 객체를 생성할 수 있다.")
    void mapperCollectionStrategyForList() {
        final Collection<Object> result = mapperCollectionStrategies.createCollectionBy(List.class);

        assertThat(result).isInstanceOf(ArrayList.class);
    }

    @Test
    @DisplayName("MapperCollectionStrategies.createCollectionBy(Set.class) 를 이용해 LinkedHashSet 객체를 생성할 수 있다.")
    void mapperCollectionStrategyForSet() {
        final Collection<Object> result = mapperCollectionStrategies.createCollectionBy(Set.class);

        assertThat(result).isInstanceOf(LinkedHashSet.class);
    }

    @Test
    @DisplayName("등록되어있지 않은 타입은 에러를 던진다..")
    void mapperCollectionStrategyFailureTest() {
        assertThatThrownBy(() -> mapperCollectionStrategies.createCollectionBy(NonCollection.class))
                .isInstanceOf(PersistenceException.class);
    }

    private static class NonCollection {

    }
}
