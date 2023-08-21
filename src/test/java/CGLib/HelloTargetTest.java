package CGLib;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
class HelloTargetTest {

    @Test
    void HelloUppercase_proxy() {
        HelloTarget helloTarget = new HelloTarget();
        HelloTarget proxiedHello = (HelloTarget) HelloUppercase.createProxy(helloTarget);

        assertThat(proxiedHello.sayHello("upper")).isEqualTo("HELLO UPPER");
        assertThat(proxiedHello.sayHi("upper")).isEqualTo("HI UPPER");
        assertThat(proxiedHello.sayThankYou("upper")).isEqualTo("THANK YOU UPPER");
    }
}
