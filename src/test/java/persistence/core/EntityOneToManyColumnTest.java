package persistence.core;

import domain.FixtureAssociatedEntity;
import extension.EntityMetadataExtension;
import jakarta.persistence.FetchType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@ExtendWith(EntityMetadataExtension.class)
class EntityOneToManyColumnTest {

    private Class<?> mockClass;

    @Test
    @DisplayName("EntityAssociatedColumn 을 통해 @OneToMany 필드의 정보를 가진 객체를 생성할 수 있다.")
    void defaultAssociatedEntityTest() throws NoSuchFieldException {
        mockClass = FixtureAssociatedEntity.WithOneToMany.class;
        final EntityAssociatedColumn associatedColumn = new EntityOneToManyColumn(mockClass.getDeclaredField("withIds"), "WithOneToMany");

        assertSoftly(softly -> {
            softly.assertThat(associatedColumn.getFetchType()).isEqualTo(FetchType.LAZY);
            softly.assertThat(associatedColumn.getName()).isEqualTo("withIds_id");
            softly.assertThat(associatedColumn.getFieldName()).isEqualTo("withIds");
            softly.assertThat(associatedColumn.getJoinColumnType()).isEqualTo(FixtureAssociatedEntity.WithId.class);
            softly.assertThat(associatedColumn.isInsertable()).isTrue();
            softly.assertThat(associatedColumn.isNotNull()).isFalse();
        });
    }

    @Test
    @DisplayName("@OneToMany(fetch) 를 통해 FetchType.EAGER 로 설정 할 수 있다.")
    void fetchTypeEagerTest() throws NoSuchFieldException {
        mockClass = FixtureAssociatedEntity.WithOneToManyFetchTypeEAGER.class;
        final EntityAssociatedColumn associatedColumn = new EntityOneToManyColumn(mockClass.getDeclaredField("withIds"), "WithOneToManyFetchTypeEAGER");

        assertThat(associatedColumn.getFetchType()).isEqualTo(FetchType.EAGER);
    }

    @Test
    @DisplayName("@JoinColumn(name) 를 통해 조인 column 이름을 정할 수 있다.")
    void joinColumnNameTest() throws NoSuchFieldException {
        mockClass = FixtureAssociatedEntity.WithOneToManyJoinColumn.class;
        final EntityAssociatedColumn associatedColumn = new EntityOneToManyColumn(mockClass.getDeclaredField("withIds"), "WithOneToManyJoinColumn");

        assertSoftly(softly -> {
            softly.assertThat(associatedColumn.getName()).isEqualTo("join_pk");
            softly.assertThat(associatedColumn.getFieldName()).isEqualTo("withIds");
            softly.assertThat(associatedColumn.getJoinColumnType()).isEqualTo(FixtureAssociatedEntity.WithId.class);
        });
    }

    @Test
    @DisplayName("@JoinColumn(insertable) 를 통해 column 의 insertable 여부를 결정할 수 있다.")
    void joinColumnInsertableTest() throws NoSuchFieldException {
        mockClass = FixtureAssociatedEntity.WithOneToManyInsertableFalse.class;
        final EntityAssociatedColumn associatedColumn = new EntityOneToManyColumn(mockClass.getDeclaredField("withIds"), "WithOneToManyInsertableFalse");

        assertThat(associatedColumn.isInsertable()).isFalse();
    }

    @Test
    @DisplayName("@JoinColumn(nullable) 를 통해 column 의 nullable 를 결정할 수 있다.")
    void joinColumnNullableTest() throws NoSuchFieldException {
        mockClass = FixtureAssociatedEntity.WithOneToManyNullableFalse.class;
        final EntityAssociatedColumn associatedColumn = new EntityOneToManyColumn(mockClass.getDeclaredField("withIds"), "WithOneToManyNullableFalse");

        assertThat(associatedColumn.isNotNull()).isTrue();
    }

    @Test
    @DisplayName("getAssociatedEntityMetadata 를 통해 연관관계 Entity 의 Metadata 를 반환 받을 수 있다.")
    void getAssociatedEntityMetadataTest() throws NoSuchFieldException {
        mockClass = FixtureAssociatedEntity.Order.class;
        final EntityOneToManyColumn entityOneToManyColumn = new EntityOneToManyColumn(mockClass.getDeclaredField("orderItems"), "WithOneToManyNullableFalse");

        assertThat(entityOneToManyColumn.getAssociatedEntityMetadata())
                .isEqualTo(EntityMetadata.from(FixtureAssociatedEntity.OrderItem.class));
    }

    @Test
    @DisplayName("getAssociatedEntityColumns 를 통해 연관관계 Entity column 들을 반환 받을 수 있다.")
    void getAssociatedEntityColumnsTest() throws NoSuchFieldException {
        mockClass = FixtureAssociatedEntity.Order.class;
        final EntityOneToManyColumn entityOneToManyColumn = new EntityOneToManyColumn(mockClass.getDeclaredField("orderItems"), "WithOneToManyNullableFalse");

        assertThat(entityOneToManyColumn.getAssociatedEntityColumns())
                .extracting(EntityColumn::getName)
                .containsExactly("id", "product", "quantity");
    }

    @Test
    @DisplayName("getAssociatedEntityColumnNamesWithAlias 를 통해 연관관계 Entity column 들을 Alias 와 함께 반환 받을 수 있다.")
    void getAssociatedEntityColumnNamesWithAliasTest() throws NoSuchFieldException {
        mockClass = FixtureAssociatedEntity.Order.class;
        final EntityOneToManyColumn entityOneToManyColumn = new EntityOneToManyColumn(mockClass.getDeclaredField("orderItems"), "WithOneToManyNullableFalse");

        assertThat(entityOneToManyColumn.getAssociatedEntityColumnNamesWithAlias())
                .containsExactly("order_items.id", "order_items.product", "order_items.quantity");
    }

    @Test
    @DisplayName("getNameWithAliasAssociatedEntity 를 통해 연관관계 Entity 의 id Column 이름을 Alias 와 함께 반환 받을 수 있다.")
    void getNameWithAliasAssociatedEntityTest() throws NoSuchFieldException {
        mockClass = FixtureAssociatedEntity.Order.class;
        final EntityOneToManyColumn entityOneToManyColumn = new EntityOneToManyColumn(mockClass.getDeclaredField("orderItems"), "WithOneToManyNullableFalse");

        assertThat(entityOneToManyColumn.getNameWithAliasAssociatedEntity())
                .isEqualTo("order_items.order_id");
    }

    @Test
    @DisplayName("getAssociatedEntityTableName 를 통해 연관관계 Entity 의 tableName 을 반환 받을 수 있다.")
    void getAssociatedEntityTableNameTest() throws NoSuchFieldException {
        mockClass = FixtureAssociatedEntity.Order.class;
        final EntityOneToManyColumn entityOneToManyColumn = new EntityOneToManyColumn(mockClass.getDeclaredField("orderItems"), "WithOneToManyNullableFalse");

        assertThat(entityOneToManyColumn.getAssociatedEntityTableName())
                .isEqualTo("order_items");
    }
}
