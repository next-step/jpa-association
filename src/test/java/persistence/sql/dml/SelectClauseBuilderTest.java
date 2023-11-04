package persistence.sql.dml;

import domain.FixtureAssociatedEntity;
import domain.FixtureEntity;
import extension.EntityMetadataExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import persistence.core.EntityMetadata;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(EntityMetadataExtension.class)
class SelectClauseBuilderTest {
    @Test
    @DisplayName("주어진 column 들을 이용해 SelectClause 를 생성 할 수 있다.")
    void selectClauseBuilderTest() {
        final String selectClause = SelectClauseBuilder.builder()
                .add("column")
                .add("column2")
                .build();

        assertThat(selectClause).isEqualTo("column, column2");
    }

    @Test
    @DisplayName("주어진 EntityMetadata 를 이용할 경우 해당 Entity 의 table name 을 prefix 로 붙인 SelectClause 를 생성 할 수 있다.")
    void selectClauseBuilderWithEntityMetadataTest() {
        final String selectClause = SelectClauseBuilder.builder()
                .add(new EntityMetadata<>(FixtureEntity.WithColumn.class))
                .build();

        assertThat(selectClause).isEqualTo("WithColumn.id, WithColumn.test_column, WithColumn.notNullColumn");
    }

    @Test
    @DisplayName("주어진 EntityMetadata 의 @OneToMany(fetch=EAGAR) 일 경우 해당 컬럼을 포함해 각 table name 을 prefix 로 붙인 SelectClause 를 생성 할 수 있다.")
    void selectClauseBuilderWithOneToManyFetchTypeEAGEREntityMetadataTest() {
        final String selectClause = SelectClauseBuilder.builder()
                .add(new EntityMetadata<>(FixtureAssociatedEntity.WithOneToManyFetchTypeEAGER.class))
                .build();

        assertThat(selectClause).isEqualTo("WithOneToManyFetchTypeEAGER.id, WithId.id");
    }

    @Test
    @DisplayName("주어진 EntityMetadata 를 이용해 OneToMany(fetch=LAZY) 일 경우 해당 컬럼을 제외하고 table name 을 prefix 로 붙인 SelectClause 를 생성 할 수 있다.")
    void selectClauseBuilderWithOneToManyEntityMetadataTest() {
        final String selectClause = SelectClauseBuilder.builder()
                .add(new EntityMetadata<>(FixtureAssociatedEntity.WithOneToMany.class))
                .build();

        assertThat(selectClause).isEqualTo("WithOneToMany.id");
    }

}
