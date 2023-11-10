package persistence.association;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.meta.EntityMeta;
import persistence.testFixtures.assosiate.NoHasJoinColumnOrder;
import persistence.testFixtures.assosiate.Order;

class OneToManyAssociationTest {

    @Test
    @DisplayName("연관관계 엔티티를 생성한다")
    void createAssociate() throws Exception {
        //given
        OneToManyAssociation oneToManyColumn = OneToManyAssociation.of(Order.class, EntityMeta.from(Order.class));

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
        OneToManyAssociation oneToManyColumn = OneToManyAssociation.of(NoHasJoinColumnOrder.class, EntityMeta.from(NoHasJoinColumnOrder.class));

        //when
        final EntityMeta manyEntityMeta = oneToManyColumn.getManyEntityMeta();

        //then
        assertSoftly((it)-> {
            assertThat(oneToManyColumn.isHasJoinColumn()).isFalse();
            assertThat(manyEntityMeta).isNotNull();
            assertThat(manyEntityMeta.getTableName()).isEqualTo("order_items");
        });
    }
}
