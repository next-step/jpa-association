package utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CamelToSnakeCaseConverterTest {

    @DisplayName("camel case가 snake case로 바뀐다.")
    @Test
    void camelToSnakeTest(){
        String name = "CamelToSnakeCaseConverter";
        String expected = "camel_to_snake_case_converter";

        assertThat(CamelToSnakeCaseConverter.convert(name)).isEqualTo(expected);
    }

    @DisplayName("snake case는 그대로 유지된다.")
    @Test
    void snakeToSnakeTest(){
        String name = "snake_case";
        String expected = "snake_case";

        assertThat(CamelToSnakeCaseConverter.convert(name)).isEqualTo(expected);
    }

}
