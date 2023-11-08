package persistence.sql.dml;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.dialect.h2.H2Dialect;

import static org.assertj.core.api.Assertions.assertThat;

class OnClauseBuilderTest {

    @Test
    @DisplayName("OnClauseBuilder 를 통해 on 절을 만들 수 있다.")
    void onClauseBuilderTest() {
        final OnClauseBuilder builder = OnClauseBuilder.builder(new SelectQueryBuilder(new H2Dialect()));

        builder.on("left", "right");
        final String result = builder.build();

        assertThat(result).isEqualTo(" on left = right");
    }
}
