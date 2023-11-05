package persistence.entity.attribute;

import fixtures.EntityFixtures;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.converter.SqlConverter;
import persistence.sql.infra.H2SqlConverter;

import java.util.HashSet;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Nested
@DisplayName("EntityAttribute 클래스의")
public class EntityAttributeTest {
    SqlConverter sqlConverter = new H2SqlConverter();

    @Nested
    @DisplayName("of 메소드는")
    class of {
        @Nested
        @DisplayName("유효한 클래스 정보와 파서가 들어오면")
        class withValidArgs {
            @Test
            @DisplayName("EntityAttribute를 반환한다.")
            void returnEntityAttribute() {
                //given
                //when
                EntityAttribute entityAttribute =
                        EntityAttribute.of(EntityFixtures.SampleOneWithValidAnnotation.class, new HashSet<>());

                //then
                Assertions.assertAll(
                        () -> assertThat(entityAttribute.getTableName())
                                .isEqualTo("entity_name")
                );
            }
        }

        @Nested
        @DisplayName("@Id 가 여러개인 클래스 정보와 파서가 들어오면")
        class withMultiIdClass {
            @Test
            @DisplayName("예외를 반환한다.")
            void throwException() {
                //given
                //when
                //then
                Assertions.assertThrows(
                        IllegalStateException.class, () -> EntityAttribute.of(
                                EntityFixtures.EntityWithMultiIdAnnotation.class, new HashSet<>()));
            }
        }

        @Nested
        @DisplayName("@Entity 가 없는 클래스 정보와 파서가 들어오면")
        class withEntityWithOutEntityAnnotation {
            @Test
            @DisplayName("예외를 반환한다.")
            void throwException() {
                //given
                //when
                //then
                Assertions.assertThrows(
                        IllegalStateException.class, () -> EntityAttribute.of(
                                EntityFixtures.EntityWithOutEntityAnnotation.class, new HashSet<>()));
            }
        }
    }
}
