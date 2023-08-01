package proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class CallbackProxyTest {
    @Test
    void toUppercase() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HelloTarget.class);
        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
            Object result = proxy.invokeSuper(obj, args);
            return (result instanceof String) && method.getName().startsWith("say")
                    ? ((String) result).toUpperCase()
                    : result;
        });
        Hello proxiedHello = (Hello) enhancer.create();
        assertAll(
                () -> assertThat(proxiedHello.sayHello("javajigi")).isEqualTo("HELLO JAVAJIGI"),
                () -> assertThat(proxiedHello.sayHi("javajigi")).isEqualTo("HI JAVAJIGI"),
                () -> assertThat(proxiedHello.sayThankYou("javajigi")).isEqualTo("THANK YOU JAVAJIGI"),
                () -> assertThat(proxiedHello.pingPong("javajigi")).isEqualTo("Pong javajigi")
        );

    }
}
