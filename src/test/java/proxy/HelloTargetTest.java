package proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class HelloTargetTest {
    @Test
    @DisplayName("프록시 테스트")
    void proxy() {
        // given
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HelloTarget.class);
        enhancer.setCallback(new UppercaseMethodInterceptor());
        Object proxyObj = enhancer.create();
        HelloTarget helloTarget = (HelloTarget) proxyObj;

        // when
        String sayHello = helloTarget.sayHello("Yohan");
        String sayHi = helloTarget.sayHi("Yohan");
        String sayThankYou = helloTarget.sayThankYou("Yohan");

        // then
        assertThat(sayHello).isEqualTo("hello yohan");
        assertThat(sayHi).isEqualTo("hi yohan");
        assertThat(sayThankYou).isEqualTo("thank you yohan");
    }

    static class UppercaseMethodInterceptor implements MethodInterceptor {
        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            Object result = proxy.invokeSuper(obj, args);

            if (result instanceof String) {
                return ((String) result).toLowerCase();
            }
            return result;
        }
    }
}