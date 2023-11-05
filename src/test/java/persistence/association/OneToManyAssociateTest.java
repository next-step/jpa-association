package persistence.association;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.lang.reflect.Field;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.meta.EntityMeta;
import persistence.testFixtures.NoHasJoinColumnOrder;
import persistence.testFixtures.Order;
import persistence.testFixtures.OrderItem;

class OneToManyAssociateTest {

    @Test
    @DisplayName("연관관계 엔티티를 생성한다")
    void createAssociate() throws Exception {
        //given
        OneToManyAssociate oneToManyColumn = OneToManyAssociate.from(Order.class).get();

        //when
        final EntityMeta manyEntityMeta = oneToManyColumn.getManyEntityMeta();

        //then
        assertSoftly((it)-> {
            assertThat(oneToManyColumn.isHasJoinColumn()).isTrue();
            assertThat(manyEntityMeta).isNotNull();
            assertThat(manyEntityMeta.getTableName()).isEqualTo("order_items");
        });
    }

    @Test
    @DisplayName("JoinColumn이 없는 연관관계 엔티티를 생성한다")
    void noJoinColumnCreateAssociate() throws Exception {
        //given
        OneToManyAssociate oneToManyColumn = OneToManyAssociate.from(NoHasJoinColumnOrder.class).get();

        //when
        final EntityMeta manyEntityMeta = oneToManyColumn.getManyEntityMeta();

        //then
        assertSoftly((it)-> {
            assertThat(oneToManyColumn.isHasJoinColumn()).isFalse();
            assertThat(manyEntityMeta).isNotNull();
            assertThat(manyEntityMeta.getTableName()).isEqualTo("order_items");
        });
    }

    @Test
    @DisplayName("연관관계가 없는 엔티티를 확인한다.")
    void noAssociate() throws Exception {
        assertThat(OneToManyAssociate.from(OrderItem.class)).isEmpty();
    }

}
