package proxy;

import org.junit.jupiter.api.Test;
import proxy.domain.Hello;
import proxy.domain.HelloTarget;

import java.lang.reflect.Proxy;

import static org.assertj.core.api.Assertions.assertThat;

public class HelloProxyTest {

    @Test
    void testUpperCaseConversion() {
        Hello hello = (Hello) Proxy.newProxyInstance(Hello.class.getClassLoader(),
                new Class[] {Hello.class},
                new UpperCaseInvocationHandler(new HelloTarget()));
        assertThat(hello.sayHello("hi")).isEqualTo("HELLO HI");
    }

    @Test
    void testUpperCaseConversionWithMixedCase() {
        Hello hello = (Hello) Proxy.newProxyInstance(Hello.class.getClassLoader(),
                new Class[] {Hello.class},
                new UpperCaseInvocationHandler(new HelloTarget()));
        assertThat(hello.sayHello("HI")).isEqualTo("HELLO HI");
    }

    @Test
    void testEmptyString() {
        Hello hello = (Hello) Proxy.newProxyInstance(Hello.class.getClassLoader(),
                new Class[] {Hello.class},
                new UpperCaseInvocationHandler(new HelloTarget()));
        assertThat(hello.sayHello("")).isEqualTo("HELLO ");
    }

    @Test
    void testAlreadyUpperCase() {
        Hello hello = (Hello) Proxy.newProxyInstance(Hello.class.getClassLoader(),
                new Class[] {Hello.class},
                new UpperCaseInvocationHandler(new HelloTarget()));
        assertThat(hello.sayThankYou("HELLO")).isEqualTo("THANK YOU HELLO");
    }
}
