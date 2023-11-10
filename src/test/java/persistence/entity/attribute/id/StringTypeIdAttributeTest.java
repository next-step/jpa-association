package persistence.entity.attribute.id;

import fixtures.EntityFixtures;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.sql.infra.H2SqlConverter;

import java.lang.reflect.Field;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Nested
@DisplayName("StringTypeIdAttribute 클래스의")
class StringTypeIdAttributeTest {
    EntityFixtures.EntityWithStringId sample
            = new EntityFixtures.EntityWithStringId("test id", "test_nick_name", 29);

    @Nested
    @DisplayName("생성자는")
    class constructor {
        @Nested
        @DisplayName("필드가 인자로 주어지면")
        class withValidArgs {
            @Test
            @DisplayName("아이디 필드를 파싱해서 메타데이터 상태를 가진다.")
            void holdMetaData() throws NoSuchFieldException {
                //given
                Field field = sample.getClass().getDeclaredField("id");

                //when
                StringTypeIdAttribute stringTypeIdAttribute = new StringTypeIdAttribute(field);

                //then
                Assertions.assertAll(
                        () -> assertThat(stringTypeIdAttribute.getColumnName()).isEqualTo("id"),
                        () -> assertThat(stringTypeIdAttribute.getFieldName()).isEqualTo("id"),
                        () -> assertThat(stringTypeIdAttribute.getGenerationType()).isNull(),
                        () -> assertThat(stringTypeIdAttribute.getField().getType().toString()).isEqualTo("class java.lang.String")
                );
            }
        }
    }
}
