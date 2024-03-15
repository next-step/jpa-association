package learning.proxy;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class HelloTargetTest {

    @Test
    void convertResultToUppercase() {
        MethodInterceptor resultToUppercaseConversionMethodInterceptor = (object, method, args, methodProxy) -> {
            Object returnValue = methodProxy.invokeSuper(object, args);
            return returnValue.toString().toUpperCase();
        };

        testAll(resultToUppercaseConversionMethodInterceptor,
                "abc",
                "HELLO ABC", "HI ABC", "THANK YOU ABC");
    }

    @Test
    void convertHiAndHelloOnly() {
        MethodInterceptor convertingHiAndHelloOnly = new MethodInterceptor() {
            @Override
            public Object intercept(Object object, Method method, Object[] args,
                                    MethodProxy methodProxy) throws Throwable {
                Object returnValue = methodProxy.invokeSuper(object, args);

                if (isHiOrHello(method)) {
                    return returnValue.toString().toUpperCase();
                }
                return returnValue;
            }

            private boolean isHiOrHello(Method method) {
                return method.getName().equals("sayHi") || method.getName().equals("sayHello");
            }
        };

        testAll(convertingHiAndHelloOnly,
                "abc", "HELLO ABC", "HI ABC", "Thank You abc");
    }

    private static void testAll(Callback callback,
                                String argument,
                                String expected0, String expected1, String expected2) {
        HelloTarget proxy = (HelloTarget) Enhancer.create(HelloTarget.class, callback);

        assertAll(
                () -> assertThat(proxy.sayHello(argument)).isEqualTo(expected0),
                () -> assertThat(proxy.sayHi(argument)).isEqualTo(expected1),
                () -> assertThat(proxy.sayThankYou(argument)).isEqualTo(expected2)
        );
    }
}
