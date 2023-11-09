package persistence.sql.dml;

import domain.FixtureAssociatedEntity.WithOneToManyFetchTypeEAGER;
import extension.EntityMetadataExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import persistence.core.EntityMetadata;
import persistence.dialect.h2.H2Dialect;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(EntityMetadataExtension.class)
class LeftJoinClauseBuilderTest {


    @Test
    @DisplayName("LeftJoinClauseBuilder 를 이용해 left join 절을 만들 수 있다.")
    void leftJoinClauseBuilderTest() {
        final EntityMetadata<WithOneToManyFetchTypeEAGER> withOneToManyEntityMetadata = new EntityMetadata<>(WithOneToManyFetchTypeEAGER.class);

        final LeftJoinClauseBuilder queryBuilder = LeftJoinClauseBuilder.builder(new SelectQueryBuilder(new H2Dialect()));
        withOneToManyEntityMetadata.getEagerOneToManyColumns()
                .forEach(entityOneToManyColumn -> {
                    queryBuilder.leftJoin(entityOneToManyColumn.getAssociatedEntityTableName())
                            .on(withOneToManyEntityMetadata.getIdColumnNameWithAlias(), entityOneToManyColumn.getNameWithAliasAssociatedEntity());
                    queryBuilder.leftJoin("testTable")
                            .on("testColumnLeft", "testColumnRight");
                });

        assertThat(queryBuilder.build()).isEqualTo(" left join WithId on WithOneToManyFetchTypeEAGER.id = WithId.withIds_id left join testTable on testColumnLeft = testColumnRight");
    }

}
