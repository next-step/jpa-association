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
@DisplayName("LongTypeIdAttribute 클래스의")
class LongTypeIdAttributeTest {

    EntityFixtures.SampleOneWithValidAnnotation sample
            = new EntityFixtures.SampleOneWithValidAnnotation(1, "test_nick_name", 29);

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
                LongTypeIdAttribute longTypeIdAttribute = new LongTypeIdAttribute(field);

                //then
                Assertions.assertAll(
                        () -> assertThat(longTypeIdAttribute.getFieldName()).isEqualTo("id"),
                        () -> assertThat(longTypeIdAttribute.getGenerationType()).isEqualTo(GenerationType.IDENTITY),
                        () -> assertThat(longTypeIdAttribute.getColumnName()).isEqualTo("id"),
                        () -> assertThat(longTypeIdAttribute.getField().getType().toString()).isEqualTo("class java.lang.Long")
                );
            }
        }
    }
}
