package persistence.sql.dml;

import domain.FixtureAssociatedEntity.WithId;
import domain.FixtureAssociatedEntity.WithOneToMany;
import domain.FixtureAssociatedEntity.WithOneToManyFetchTypeEAGER;
import extension.EntityMetadataExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import persistence.core.EntityMetadata;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(EntityMetadataExtension.class)
class LeftJoinClauseBuilderTest {


    @Test
    @DisplayName("@OneToMany(fetch=EAGAR) 를 가진 Entity 정보로 left join 쿼리문을 만들 수 있다.")
    void eagarLeftJoinClauseTest() {
        final EntityMetadata<WithOneToManyFetchTypeEAGER> withOneToManyEntityMetadata = new EntityMetadata<>(WithOneToManyFetchTypeEAGER.class);

        final String result = LeftJoinClauseBuilder.builder()
                .addJoin(withOneToManyEntityMetadata)
                .build();

        assertThat(result).isEqualTo(" left join WithId on WithOneToManyFetchTypeEAGER.id = WithId.withIds_id");
    }
    @Test
    @DisplayName("@OneToMany(fetch=LAZY) 를 가진 Entity 정보는 left join 쿼리문을 만들지 않는다.")
    void lazyLeftJoinClauseTest() {
        final EntityMetadata<WithOneToMany> withOneToManyEntityMetadata = new EntityMetadata<>(WithOneToMany.class);

        final String result = LeftJoinClauseBuilder.builder()
                .addJoin(withOneToManyEntityMetadata)
                .build();

        assertThat(result).isEqualTo("");
    }

    @Test
    @DisplayName("@OneToMany 가 없는 Entity 정보는 left join 쿼리문을 만들지 않는다.")
    void leftJoinClauseEmptyTest() {
        final EntityMetadata<WithId> withIdEntityMetadata = new EntityMetadata<>(WithId.class);

        final String result = LeftJoinClauseBuilder.builder()
                .addJoin(withIdEntityMetadata)
                .build();

        assertThat(result).isEqualTo("");
    }
}
