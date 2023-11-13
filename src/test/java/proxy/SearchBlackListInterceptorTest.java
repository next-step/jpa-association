package proxy;

import net.sf.cglib.proxy.Enhancer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SearchBlackListInterceptorTest {
    @Test
    @DisplayName("특정 값이 들어왔을 경우 다른 객체 메소드 실행")
    void blackList() {
        //given
        final String name = "zinzo";
        final String expected = "blackList member -> " + name;
        Object object = 프록시_생성();

        //when
        HelloTarget helloTarget = (HelloTarget) object;
        String result = helloTarget.sayHello(name);

        //then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("특정 값이 아닐 때 기존 메소드 실행")
    void defaultMethod() {
        //given
        final String name = "hiiro";
        final String expected = "Hello " + name;
        Object object = 프록시_생성();

        //when
        HelloTarget helloTarget = (HelloTarget) object;
        String result = helloTarget.sayHello(name);

        //then
        assertThat(result).isEqualTo(expected);
    }

    private Object 프록시_생성() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HelloTarget.class);
        enhancer.setCallback(new SearchBlackListInterceptor());

        return enhancer.create();
    }
}
