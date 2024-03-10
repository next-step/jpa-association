package study.proxy;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import study.proxy.callback.DispatcherCallback;
import study.proxy.callback.FixedValueCallback;
import study.proxy.callback.LazyLoaderCallback;
import study.proxy.callback.UpperCaseMethodInterceptor;

import static org.assertj.core.api.Assertions.assertThat;

class HelloTargetTest {

    @Test
    @DisplayName("프록시를 통해 대문자로 변환된 문자열을 반환할 수 있다.")
    void proxyWithUpperCase() {
        //given
        HelloTarget helloTarget = getProxyFrom(new UpperCaseMethodInterceptor());

        //when
        String result = helloTarget.sayHello("small name");

        //then
        assertThat(result).isUpperCase();
    }

    @Test
    @DisplayName("noOp callback 실험")
    void noOp_callback() {
        //given
        HelloTarget proxy = getProxyFrom(NoOp.INSTANCE);
        String name = "small name";

        //when
        String result = proxy.sayHello(name);

        //then
        assertThat(result).isEqualTo(new HelloTarget().sayHello(name));
    }

    @Test
    @DisplayName("fixedValue callback 실험")
    void fixedValue_callback() {
        //given
        HelloTarget proxy = getProxyFrom(new FixedValueCallback());
        String name = "small name";

        //when
        String result = proxy.sayHello(name);

        //then
        assertThat(result).isEqualTo(FixedValueCallback.FIXED_VALUE);
    }

    @Test
    @DisplayName("dispatcher callback 실험")
    void dispatcher_callback() {
        //given
        HelloTarget proxy = getProxyFrom(new DispatcherCallback());
        String name = "small name";

        //when
        String result = proxy.sayHello(name);

        //then
        assertThat(result).isEqualTo(new ByeTarget().sayHello(name));
    }

    @Test
    @DisplayName("lazyLoader callback 실험")
    void lazyLoader_callback() {
        //given
        HelloTarget proxy = getProxyFrom(new LazyLoaderCallback());
        String name = "small name";

        //when
        System.out.println("TEST");
        String result = proxy.sayHello(name);
    }


    private HelloTarget getProxyFrom(Callback callback) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HelloTarget.class);
        enhancer.setCallback(callback);
        return (HelloTarget) enhancer.create();
    }
}
