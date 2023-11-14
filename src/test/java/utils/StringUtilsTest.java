package utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class StringUtilsTest {

    @Nested
    @DisplayName("Object가 문자열이면 콜론으로 감싸 반환")
    class parseChar {

        @ParameterizedTest
        @ValueSource(strings = " ")
        @NullAndEmptySource
        @DisplayName("null 혹은 빈 값이 들어왔을 때 null 반환")
        void paramIsNull(String input) {
            //when
            String result = StringUtils.parseChar(input);

            //then
            assertThat(result).isNull();
        }

        @ParameterizedTest
        @ValueSource(strings = {"a", "가", "1"})
        @DisplayName("string 들어오면 '' 붙여서 반환")
        void success(String input) {
            //when
            String result = StringUtils.parseChar(input);

            //then
            assertThat(result).isEqualTo("'" + input + "'");
        }

        @ParameterizedTest
        @ValueSource(chars = {'a', '1', '가'})
        @DisplayName("chars 들어오면 '' 붙여서 반환")
        void success(char input) {
            //when
            String result = StringUtils.parseChar(input);

            //then
            assertThat(result).isEqualTo("'" + input + "'");
        }

        @ParameterizedTest
        @ValueSource(chars = {'a', '1', '가'})
        @DisplayName("Character 들어오면 '' 붙여서 반환")
        void success(Character input) {
            //when
            String result = StringUtils.parseChar(input);

            //then
            assertThat(result).isEqualTo("'" + input + "'");
        }

        @Test
        @DisplayName("int 들어면 '' 없이 반환")
        void inputInt() {
            //given
            Integer input = 1;

            //when
            String result = StringUtils.parseChar(input);

            //then
            assertSoftly(softAssertions -> {
                softAssertions.assertThat(result).isNotEqualTo("'" + input + "'");
                softAssertions.assertThat(result).isEqualTo(input.toString());
            });
        }

        @Test
        @DisplayName("long 들어면 '' 없이 반환")
        void inputLong() {
            //given
            Long input = 1L;

            //when
            String result = StringUtils.parseChar(input);

            //then
            assertSoftly(softAssertions -> {
                softAssertions.assertThat(result).isNotEqualTo("'" + input + "'");
                softAssertions.assertThat(result).isEqualTo(input.toString());
            });
        }
    }

    @Nested
    @DisplayName("카멜케이스 문자열을 스네이크 문자열로 변환")
    class camelToSnake {

        @ParameterizedTest
        @CsvSource({"snake, snake", "helloWorld, hello_world", "happyBirthDay, happy_birth_day"})
        @DisplayName("성공적으로 스네이크 문자열로 변경")
        void success(String before, String after) {
            //when
            String result = StringUtils.camelToSnake(before);

            //then
            assertThat(result).isEqualTo(after);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = " ")
    @NullAndEmptySource
    @DisplayName("null 혹은 빈 값이 들어왔을 때 null 반환")
    void paramIsNull(String input) {
        //when
        String result = StringUtils.camelToSnake(input);

        //then
        assertThat(result).isNull();
    }
}
