package persistence.study.proxy;

import net.sf.cglib.proxy.Enhancer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.study.HelloTarget;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

public class HelloUpperCaseProxyTest {

    private final HelloTarget helloTarget = buildHelloUpperCaseProxy();

    private HelloTarget buildHelloUpperCaseProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HelloTarget.class);
        enhancer.setCallback(new HelloTargetToUpperCaseInterceptor());

        Object helloUpperCaseProxy = enhancer.create();
        return (HelloTarget) helloUpperCaseProxy;
    }

    @Test
    @DisplayName("HelloUpperCaseProxy를 사용하면 대문자가 반환된다.")
    void testHelloUpperCaseProxy() {
        String sayHello = helloTarget.sayHello("name");
        String sayHi = helloTarget.sayHi("name");
        String sayThankYou = helloTarget.sayThankYou("name");

        assertSoftly(softly -> {
            softly.assertThat(sayHello).isEqualTo("HELLO NAME");
            softly.assertThat(sayHi).isEqualTo("HI NAME");
            softly.assertThat(sayThankYou).isEqualTo("THANK YOU NAME");
        });
    }
}
