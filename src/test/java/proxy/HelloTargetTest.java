package proxy;

import net.sf.cglib.proxy.Enhancer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import proxy.HelloTarget;
import proxy.HelloTargetInterceptor;

import static org.assertj.core.api.Assertions.assertThat;

class HelloTargetTest {
    @Test
    @DisplayName("sayHello 대문자로 찍기")
    void sayHello() {
        //given
        final String expected = "Hello ZINZO";

        Object object = 프록시_생성();

        //when
        HelloTarget helloTarget = (HelloTarget) object;
        String result = helloTarget.sayHello("zinzo");

        //then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("sayHi 대문자로 찍기")
    void sayHi() {
        //given
        final String expected = "Hi ZINZO";

        Object object = 프록시_생성();

        //when
        HelloTarget helloTarget = (HelloTarget) object;
        String result = helloTarget.sayHi("zinzo");

        //then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("sayThankYou 대문자로 찍기")
    void sayThankYou() {
        //given
        final String expected = "Thank You ZINZO";

        Object object = 프록시_생성();

        //when
        HelloTarget helloTarget = (HelloTarget) object;
        String result = helloTarget.sayThankYou("zinzo");

        //then
        assertThat(result).isEqualTo(expected);
    }

    private Object 프록시_생성() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HelloTarget.class);
        enhancer.setCallback(new HelloTargetInterceptor());

        return enhancer.create();
    }
}
