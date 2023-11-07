package persistence.study.proxy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HelloTargetTest {

    @Test
    @DisplayName("Target 객체 기능 테스트 : 입력문자를 그대로 출력")
    void printTarget() {
        HelloTarget helloTarget = new HelloTarget();

        assertThat(helloTarget.sayHello("Lim")).isEqualTo("Hello Lim");
        assertThat(helloTarget.sayHi("Lim")).isEqualTo("Hi Lim");
        assertThat(helloTarget.sayThankYou("Lim")).isEqualTo("Thank You Lim");
    }

    @Test
    @DisplayName("Proxy 객체 기능테스트 : 문자를 모두 대문자로 변환해 출력")
    void printProxy() {
        HelloTarget helloTarget = new HelloTarget();

        assertThat(helloTarget.sayHello("Lim")).isEqualTo("HELLO LIM");
        assertThat(helloTarget.sayHi("Lim")).isEqualTo("HI LIM");
        assertThat(helloTarget.sayThankYou("Lim")).isEqualTo("THANK YOU LIM");
    }

}
