package persistence.entity.attribute.id;

import fixtures.EntityFixtures;
import jakarta.persistence.GenerationType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.sql.infra.H2SqlConverter;

import java.lang.reflect.Field;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Nested
@DisplayName("IntegerTypeIdAttribute 클래스의")
class IntegerTypeIdAttributeTest {
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
                EntityFixtures.EntityWithIntegerId sample
                        = new EntityFixtures.EntityWithIntegerId(1, "민준", 29);
                Field field = sample.getClass().getDeclaredField("id");

                //when
                IdAttribute idAttribute = new IntegerTypeIdAttribute(field);

                //then
                Assertions.assertAll(
                        () -> assertThat(idAttribute.getColumnName()).isEqualTo("entity_with_integer_id"),
                        () -> assertThat(idAttribute.getFieldName()).isEqualTo("id"),
                        () -> assertThat(idAttribute.getGenerationType()).isEqualTo(GenerationType.AUTO),
                        () -> assertThat(idAttribute.getField().getType().toString()).isEqualTo("class java.lang.Integer")
                );
            }
        }
    }
}
