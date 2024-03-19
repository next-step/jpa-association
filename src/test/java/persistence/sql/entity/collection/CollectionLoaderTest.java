package persistence.sql.entity.collection;

import domain.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.db.H2Database;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CollectionLoaderTest extends H2Database {

    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        this.orderItem = new OrderItem(1L, "만들면서 배우는 JPA", 1);

        entityPersister.delete(orderItem);
        entityPersister.insert(orderItem);
    }

    @DisplayName("subEntity를 조회한다.")
    @Test
    void collectionFindById() {
        List<Object> collectionOrderItem = collectionLoader.findById(OrderItem.class, 1L);

        assertThat(collectionOrderItem).containsExactly(orderItem);
    }

    @DisplayName("데이터가 없을 경우 빈값을 반한한다")
    @Test
    void existCollectionTest() {
        List<Object> collectionOrderItem = collectionLoader.findById(OrderItem.class, 2L);

        assertThat(collectionOrderItem).isEqualTo(Collections.emptyList());
    }

}
