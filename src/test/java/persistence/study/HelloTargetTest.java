package persistence.study;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class HelloTargetTest {

    private final HelloTarget target = new HelloTarget();

    @Test
    void sayHello() {
        HelloTarget helloTarget = createProxy(target);

        assertThat(helloTarget.sayHello("John")).isEqualTo("Hello JOHN");
    }

    @Test
    void sayHi() {
        HelloTarget helloTarget = createProxy(target);

        assertThat(helloTarget.sayHi("Hong Gil Dong")).isEqualTo("Hi HONG GIL DONG");
    }

    @Test
    void sayThankYou() {
        HelloTarget helloTarget = createProxy(target);

        assertThat(helloTarget.sayThankYou("James")).isEqualTo("Thank You JAMES");
    }

    private static HelloTarget createProxy(HelloTarget helloTarget) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HelloTarget.class);
        enhancer.setCallback(new UpperCaseMethodInterceptor(helloTarget));
        return (HelloTarget) enhancer.create();
    }
}
