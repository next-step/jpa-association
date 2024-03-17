package learning.proxy;

import net.sf.cglib.proxy.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class HelloTargetTest {

    @Test
    void methodInterceptorTest() {
        HelloTarget proxy = (HelloTarget) Enhancer.create(
                HelloTarget.class,
                (MethodInterceptor) (object, method, args, methodProxy) ->
                        methodProxy.invokeSuper(object, args)
                                .toString().toUpperCase());

        assertAll(
                () -> assertThat(proxy.sayHello("abc")).isEqualTo("HELLO ABC"),
                () -> assertThat(proxy.sayHi("abc")).isEqualTo("HI ABC"),
                () -> assertThat(proxy.sayThankYou("abc")).isEqualTo("THANK YOU ABC")
        );
    }

    @Test
    void noOpTest() {
        HelloTarget proxy = (HelloTarget) Enhancer.create(
                HelloTarget.class,
                NoOp.INSTANCE);

        assertThat(proxy.sayHello("test")).isEqualTo("Hello test");
        assertThat(proxy.sayHi("test")).isEqualTo("Hi test");
        assertThat(proxy.sayThankYou("test")).isEqualTo("Thank You test");
    }

    @Test
    void lazyLoaderTest() {
        // 객체가 생성된 순서를 확인하기 위한 배열
        List<String> objectNames = new ArrayList<>();

        // 1번 객체-프록시 생성 (하지만 lazy loader 이므로 이때는 생성 안됨
        HelloTarget proxy = (HelloTarget) Enhancer.create(
                HelloTarget.class,
                (LazyLoader) () -> new HelloTarget(objectNames, "lazy-load-proxy"));
        // 2번 객체 생성. 이 객체는 바로 생성된다
        new HelloTarget(objectNames, "original");

        // 2번 객체만 생성된 것 확인
        assertThat(objectNames).isEqualTo((List.of("original")));
        // doSomething() 호출하면서 1번 객체 초기화
        proxy.sayHello("");
        // 1번 객체가 2번 뒤에 생성된 것을 확인
        assertThat(objectNames).isEqualTo((List.of("original", "lazy-load-proxy")));
    }

    @Test
    void dispatcherTest() {
        // 객체가 생성된 순서를 확인하기 위한 배열
        List<String> objectNames = new ArrayList<>();

        HelloTarget proxy = (HelloTarget) Enhancer.create(HelloTarget.class, (Dispatcher) () -> new HelloTarget(objectNames, "lazy-load-proxy"));

        assertThat(objectNames).isEqualTo((List.of()));

        proxy.sayHello("");
        assertThat(objectNames).isEqualTo((List.of("lazy-load-proxy")));

        proxy.sayHello("");
        assertThat(objectNames).isEqualTo((List.of("lazy-load-proxy", "lazy-load-proxy")));

        proxy.sayHello("");
        proxy.sayHello("");
        proxy.sayHello("");
        assertThat(objectNames).isEqualTo((List.of(
                "lazy-load-proxy",
                "lazy-load-proxy",
                "lazy-load-proxy",
                "lazy-load-proxy",
                "lazy-load-proxy")));
    }

    @Test
    void fixedValueTest() {
        HelloTarget proxy = (HelloTarget) Enhancer.create(HelloTarget.class, (FixedValue) () -> "fixed value");
        assertThat(proxy.sayHello("abc")).isEqualTo("fixed value");
        assertThat(proxy.sayHi("abc")).isEqualTo("fixed value");
        assertThat(proxy.sayThankYou("abc")).isEqualTo("fixed value");
    }
}
