package persistence.study.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class HelloTargetTest {

    @Test
    @DisplayName("Target 객체 기능 테스트 : 입력문자를 그대로 출력")
    void printTarget() {
        HelloTarget helloTarget = new HelloTarget();

        assertThat(helloTarget.sayHello("Lim")).isEqualTo("Hello Lim");
        assertThat(helloTarget.sayHi("Lim")).isEqualTo("Hi Lim");
        assertThat(helloTarget.sayThankYou("Lim")).isEqualTo("Thank You Lim");
    }

    @Test
    @DisplayName("Proxy 객체 기능테스트 : Method Interceptor 를 활용해 문자를 모두 대문자로 변환해 출력")
    void printProxyMethodInterceptor() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HelloTarget.class);
        enhancer.setCallback(new MethodUpperCaseInterceptor());
        HelloTarget helloTargetProxy = (HelloTarget) enhancer.create();

        assertThat(helloTargetProxy.sayHello("Lim")).isEqualTo("HELLO LIM");
        assertThat(helloTargetProxy.sayHi("Lim")).isEqualTo("HI LIM");
        assertThat(helloTargetProxy.sayThankYou("Lim")).isEqualTo("THANK YOU LIM");
    }

    @Test
    @DisplayName("Proxy 객체 기능테스트 : Dispatcher 프록시를 활용해 문자를 모두 대문자로 변환해 출력")
    void printProxyDispatcher() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HelloTarget.class);
        enhancer.setCallback(new UpperCaseDispatcher());
        HelloTarget helloTargetProxy = (HelloTarget) enhancer.create();

        assertThat(helloTargetProxy.sayHello("Lim")).isEqualTo("HELLO LIM");
        assertThat(helloTargetProxy.sayHi("Lim")).isEqualTo("HI LIM");
        assertThat(helloTargetProxy.sayThankYou("Lim")).isEqualTo("THANK YOU LIM");
    }

    @Test
    @DisplayName("Proxy 객체 기능테스트 : LazyLoader 프록시를 활용한 지연로딩 테스트")
    void getLazyLoadingProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(List.class);
        enhancer.setCallback((LazyLoader) () -> Arrays.asList("test1", "test2", "test3"));

        HelloTarget helloTarget = new HelloTarget();
        helloTarget.setChildItems((List) enhancer.create());

        assertThat(helloTarget.isChildLoaded()).isFalse();
        assertThat(helloTarget.getChildItems().size()).isEqualTo(3);
        assertThat(helloTarget.isChildLoaded()).isTrue();
    }

}
