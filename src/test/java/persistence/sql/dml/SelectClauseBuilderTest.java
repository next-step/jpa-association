package persistence.sql.dml;

import extension.EntityMetadataExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(EntityMetadataExtension.class)
class SelectClauseBuilderTest {
    @Test
    @DisplayName("주어진 column 들을 이용해 SelectClause 를 생성 할 수 있다.")
    void selectClauseBuilderTest() {
        final String selectClause = SelectClauseBuilder.builder()
                .add("column")
                .add("column2")
                .addAll(List.of("column4, column6"))
                .build();

        assertThat(selectClause).isEqualTo("column, column2, column4, column6");
    }

}
