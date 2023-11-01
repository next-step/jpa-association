package persistence.core;

import domain.FixtureAssociatedEntity;
import jakarta.persistence.FetchType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class EntityAssociatedColumnTest {

    private Class<?> mockClass;
    @Test
    @DisplayName("EntityAssociatedColumn 을 통해 @OneToMany 필드의 정보를 가진 객체를 생성할 수 있다.")
    void defaultAssociatedEntityTest() throws NoSuchFieldException {
        mockClass = FixtureAssociatedEntity.WithOneToMany.class;
        final EntityAssociatedColumn associatedColumn = new EntityOneToManyColumn(mockClass.getDeclaredField("withIds"));

        assertSoftly(softly->{
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
        final EntityAssociatedColumn associatedColumn = new EntityOneToManyColumn(mockClass.getDeclaredField("withIds"));

        assertThat(associatedColumn.getFetchType()).isEqualTo(FetchType.EAGER);
    }

    @Test
    @DisplayName("@JoinColumn(name) 를 통해 조인 column 이름을 정할 수 있다.")
    void joinColumnNameTest() throws NoSuchFieldException {
        mockClass = FixtureAssociatedEntity.WithOneToManyJoinColumn.class;
        final EntityAssociatedColumn associatedColumn = new EntityOneToManyColumn(mockClass.getDeclaredField("withIds"));

        assertSoftly(softly->{
            softly.assertThat(associatedColumn.getName()).isEqualTo("join_pk");
            softly.assertThat(associatedColumn.getFieldName()).isEqualTo("withIds");
            softly.assertThat(associatedColumn.getJoinColumnType()).isEqualTo(FixtureAssociatedEntity.WithId.class);
        });
    }

    @Test
    @DisplayName("@JoinColumn(insertable) 를 통해 column 의 insertable 여부를 결정할 수 있다.")
    void joinColumnInsertableTest() throws NoSuchFieldException {
        mockClass = FixtureAssociatedEntity.WithOneToManyInsertableFalse.class;
        final EntityAssociatedColumn associatedColumn = new EntityOneToManyColumn(mockClass.getDeclaredField("withIds"));

        assertThat(associatedColumn.isInsertable()).isFalse();
    }

    @Test
    @DisplayName("@JoinColumn(nullable) 를 통해 column 의 nullable 를 결정할 수 있다.")
    void joinColumnNullableTest() throws NoSuchFieldException {
        mockClass = FixtureAssociatedEntity.WithOneToManyNullableFalse.class;
        final EntityAssociatedColumn associatedColumn = new EntityOneToManyColumn(mockClass.getDeclaredField("withIds"));

        assertThat(associatedColumn.isNotNull()).isTrue();
    }
}
