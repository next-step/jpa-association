package persistence.study.proxy;

import net.sf.cglib.proxy.Enhancer;
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
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HelloTarget.class);
        enhancer.setCallback(new MethodUpperCaseInterceptor());
        HelloTarget helloTargetProxy = (HelloTarget) enhancer.create();

        assertThat(helloTargetProxy.sayHello("Lim")).isEqualTo("HELLO LIM");
        assertThat(helloTargetProxy.sayHi("Lim")).isEqualTo("HI LIM");
        assertThat(helloTargetProxy.sayThankYou("Lim")).isEqualTo("THANK YOU LIM");
    }

}
