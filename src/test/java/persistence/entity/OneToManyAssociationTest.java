package persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.meta.EntityMeta;
import persistence.testFixtures.assosiate.LazyLoadOrder;
import persistence.testFixtures.assosiate.NoHasJoinColumnOrder;
import persistence.testFixtures.assosiate.Order;

class OneToManyAssociationTest {

    @Test
    @DisplayName("연관관계 엔티티를 생성한다")
    void createAssociate() throws Exception {
        //given
        OneToManyAssociation oneToManyColumn = OneToManyAssociation
                .createOneToMayAssociationByClass(Order.class, EntityMeta.from(Order.class));

        //when
        final EntityMeta manyEntityMeta = oneToManyColumn.getManyEntityMeta();

        //then
        assertSoftly((it)-> {
            assertThat(oneToManyColumn.isLazy()).isFalse();
            assertThat(oneToManyColumn.isHasJoinColumn()).isTrue();
            assertThat(manyEntityMeta).isNotNull();
            assertThat(manyEntityMeta.getTableName()).isEqualTo("order_items");
        });
    }

    @Test
    @DisplayName("JoinColumn이 없는 연관관계 엔티티를 생성한다")
    void noJoinColumnCreateAssociate() throws Exception {
        //given
        OneToManyAssociation oneToManyColumn = OneToManyAssociation.createOneToMayAssociationByClass(NoHasJoinColumnOrder.class, EntityMeta.from(NoHasJoinColumnOrder.class));

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
    @DisplayName("oneToMany fetch type이 lazy인 생성한다")
    void oneToManyFetch() throws Exception {
        //given
        OneToManyAssociation oneToManyColumn = OneToManyAssociation
                .createOneToMayAssociationByClass(LazyLoadOrder.class, EntityMeta.from(LazyLoadOrder.class));

        //when
        final EntityMeta manyEntityMeta = oneToManyColumn.getManyEntityMeta();

        //then
        assertSoftly((it)-> {
            assertThat(oneToManyColumn.isLazy()).isTrue();
            assertThat(oneToManyColumn.isHasJoinColumn()).isTrue();
            assertThat(manyEntityMeta).isNotNull();
            assertThat(manyEntityMeta.getTableName()).isEqualTo("order_items");
        });
    }


    @Test
    @DisplayName("연관관계 엔티티의 pk컬럼을 가져온다")
    void getManyPkColumn() {
        //given
        OneToManyAssociation oneToManyColumn = OneToManyAssociation
                .createOneToMayAssociationByClass(Order.class, EntityMeta.from(Order.class));

        //when
        final EntityMeta manyEntityMeta = oneToManyColumn.getManyEntityMeta();

        //then
        assertSoftly((it) -> {
            it.assertThat(oneToManyColumn.getManyPkColumn().getName()).isEqualTo("id");
            it.assertThat(manyEntityMeta).isNotNull();
            it.assertThat(manyEntityMeta.getTableName()).isEqualTo("order_items");
        });
    }

}
