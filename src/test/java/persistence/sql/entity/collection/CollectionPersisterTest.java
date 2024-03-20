package persistence.sql.entity.collection;

import domain.LazyOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.db.H2Database;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CollectionPersisterTest extends H2Database {

    private LazyOrder lazyOrder;

    @BeforeEach
    void setUp() {
        this.lazyOrder = new LazyOrder(1L, "jpa 만들기", List.of());
    }

    @DisplayName("1차 캐시가 된 값을 가져온다.")
    @Test
    void findFirstCacheTest() {
        collectionPersister.addEntity(LazyOrder.class, lazyOrder.getId(), List.of(lazyOrder));
        List<Object> entity = collectionPersister.getEntity(LazyOrder.class, 1L);

        assertThat(entity).isEqualTo(List.of(lazyOrder));
    }

    @DisplayName("캐시 되지 않은 값을 가져올 경우 널을 반환한다.")
    @Test
    void notCacheFindTest() {
        List<Object> entity = collectionPersister.getEntity(LazyOrder.class, 1L);

        assertThat(entity).isNull();
    }
}
