package proxy;

import net.sf.cglib.proxy.Proxy;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class InterfaceProxyTest {
    @Test
    void toUppercase() {
        Hello hello = new HelloTarget();
        Hello proxiedHello = (Hello) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{Hello.class},
                (proxy, method, args) -> {
                    Object result = method.invoke(hello, args);
                    return (result instanceof String) && method.getName().startsWith("say")
                            ? ((String) result).toUpperCase()
                            : result;
                }
        );
        assertAll(
                () -> assertThat(proxiedHello.sayHello("javajigi")).isEqualTo("HELLO JAVAJIGI"),
                () -> assertThat(proxiedHello.sayHi("javajigi")).isEqualTo("HI JAVAJIGI"),
                () -> assertThat(proxiedHello.sayThankYou("javajigi")).isEqualTo("THANK YOU JAVAJIGI"),
                () -> assertThat(proxiedHello.pingPong("javajigi")).isEqualTo("Pong javajigi")
        );
    }
}
