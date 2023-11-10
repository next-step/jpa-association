package persistence.association;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.meta.EntityMeta;
import persistence.testFixtures.assosiate.Order;

@DisplayName("OneToMany 연관 관계 포함경로")
class EntityOneToManyPathTest {

    @Test
    @DisplayName("OneToMany 연관 관계 포함경로 정보를 가진 객체를 생성한다.")
    void test() {
        Order order = new Order();
        EntityOneToManyPath entityOneToManyPath = new EntityOneToManyPath(EntityMeta.from(Order.class), order);

        assertSoftly((it) -> {
            assertThat(entityOneToManyPath.totalLevel()).isEqualTo(2);
            assertThat(entityOneToManyPath.getRootInstance()).isEqualTo(order);

        });
    }

}
