package persistence.sql.dml.h2;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class H2WhereQueryTest {

    @DisplayName("Where 조건문을 생성할 수 있다.")
    @Test
    void build() {
        String expected = " WHERE id = 1, name = 'ghojeong'";
        Map<String, Object> condition = new LinkedHashMap<>();
        condition.put("id", 1);
        condition.put("name", "ghojeong");
        assertThat(H2WhereQuery.build(condition))
                .isEqualTo(expected);
    }
}
