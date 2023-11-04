package persistence.proxy;

import net.sf.cglib.proxy.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

public class ProxyTest {

    private Enhancer enhancer;

    @BeforeEach
    void setUp() {
        enhancer = new Enhancer();
        enhancer.setSuperclass(HelloTarget.class);
    }

    @Test
    @DisplayName("NoOp Callback 을 이용하면 원본 객체의 메서드를 그대로 실행 시킨다.")
    void noOpTest() {
        enhancer.setCallback(NoOp.INSTANCE);
        final Object obj = enhancer.create();
        final HelloTarget helloTarget = (HelloTarget) obj;

        assertSoftly(softly -> {
            softly.assertThat(helloTarget.sayHi("종민")).isEqualTo("Hi 종민");
            softly.assertThat(helloTarget.sayHello("종민")).isEqualTo("Hello 종민");
            softly.assertThat(helloTarget.sayThankYou("종민")).isEqualTo("Thank You 종민");
        });
    }

    @Test
    @DisplayName("UppercaseInterceptor Callback 을 이용하면 원본 객체의 메서드 반환값이 String 인 경우 대문자로 변환해 반환한다.")
    void methodInterceptorTest() {
        enhancer.setCallback(new UppercaseInterceptor());
        final Object obj = enhancer.create();
        final HelloTarget helloTarget = (HelloTarget) obj;

        assertSoftly(softly -> {
            softly.assertThat(helloTarget.sayHi("종민")).isEqualTo("HI 종민");
            softly.assertThat(helloTarget.sayHello("종민")).isEqualTo("HELLO 종민");
            softly.assertThat(helloTarget.sayThankYou("종민")).isEqualTo("THANK YOU 종민");
        });
    }

    @Test
    @DisplayName("CallbackFilter 을 이용하면 원하는 Callback 을 선택해 실행할 수 있다.")
    void callbackFilterTest() {
        final Callback[] callbacks = new Callback[]{
                new UppercaseInterceptor(), // 인덱스 0
                new TrimInterceptor() // 인덱스 1
        };
        enhancer.setCallbacks(callbacks);

        enhancer.setCallbackFilter(method -> {
            if (method.getName().contains("ThankYou")) {
                return 1;
            }
            return 0;
        });
        final Object obj = enhancer.create();
        final HelloTarget helloTarget = (HelloTarget) obj;

        assertSoftly(softly -> {
            softly.assertThat(helloTarget.sayHi("종민")).isEqualTo("HI 종민");
            softly.assertThat(helloTarget.sayHello("종민")).isEqualTo("HELLO 종민");
            softly.assertThat(helloTarget.sayThankYou("  ")).isEqualTo("Thank You");
        });
    }

    @Test
    @DisplayName("LazyLoader 을 이용하면 원본 객체를 Lazy 하게 생성할 수 있다.")
    void lazyLoaderTest() {
        enhancer.setCallback((LazyLoader) () -> new HelloTarget(true));
        final Object obj = enhancer.create();
        final HelloTarget helloTarget = new HelloTarget();
        final HelloTarget proxyHelloTarget = (HelloTarget) obj;

        assertSoftly(softly -> {
            softly.assertThat(Enhancer.isEnhanced(proxyHelloTarget.getClass())).isTrue();
            softly.assertThat(helloTarget.isProxied()).isFalse();
            softly.assertThat(proxyHelloTarget.isProxied()).isTrue();
        });
    }

    private static class UppercaseInterceptor implements MethodInterceptor {
        @Override
        public Object intercept(final Object obj, final Method method, final Object[] args, final MethodProxy proxy) throws Throwable {
            final Object returnValue = proxy.invokeSuper(obj, args);
            if (returnValue instanceof String) {
                return ((String) returnValue).toUpperCase();
            }
            return returnValue;
        }
    }

    private static class TrimInterceptor implements MethodInterceptor {
        @Override
        public Object intercept(final Object obj, final Method method, final Object[] args, final MethodProxy proxy) throws Throwable {
            final Object returnValue = proxy.invokeSuper(obj, args);
            if (returnValue instanceof String) {
                return ((String) returnValue).trim();
            }
            return returnValue;
        }
    }
}
