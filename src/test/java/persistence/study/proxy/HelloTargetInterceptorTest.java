package persistence.study.proxy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.lang.reflect.Method;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Interceptor CallBack 테스트")
class HelloTargetInterceptorTest {

    @Test
    @DisplayName("Enhancer를 통해 반환값을 대문자로 변경하는 Proxy를 생성할 수 있다.")
    public void canMakeReturnValueToUppercaseProxy() {
        final Enhancer enhancer = new Enhancer();

        enhancer.setSuperclass(HelloTarget.class);
        enhancer.setCallback(new UppercaseInterceptor());

        final HelloTarget helloTarget = (HelloTarget) enhancer.create();

        //when
        final String sayHelloUpper = helloTarget.sayHello("test");
        final String sayHiUpper = helloTarget.sayHi("test");
        final String sayThankUpper = helloTarget.sayThankYou("test");

        //then
        assertAll(
            () -> assertThat(sayHelloUpper).isEqualTo("HELLO TEST"),
            () -> assertThat(sayHiUpper).isEqualTo("HI TEST"),
            () -> assertThat(sayThankUpper).isEqualTo("THANK YOU TEST")
        );
    }

    /**
     * MethodInterceptor
     */
    public static class UppercaseInterceptor implements MethodInterceptor {

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            final Object returnValue = proxy.invokeSuper(obj, args);
            if (returnValue instanceof String) {
                return returnValue.toString().toUpperCase();
            }

            return returnValue;
        }
    }
}
