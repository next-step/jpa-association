package study.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

    private HelloTarget getProxyFrom(MethodInterceptor interceptor) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HelloTarget.class);
        enhancer.setCallback(interceptor);
        return (HelloTarget) enhancer.create();
    }
}
