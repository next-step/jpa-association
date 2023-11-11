package persistence.proxy;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import domain.HelloTarget;
import jakarta.persistence.OneToMany;
import net.sf.cglib.proxy.Enhancer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CGLib 프록시 테스트")
public class CGLibTest {

    @Test
    @DisplayName("대문자로 반환하는 프록시 객체 테스트")
    void upperProxy() throws Exception {
        //given
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HelloTarget.class);
        enhancer.setCallback(new MethodCallResultUpperStringInterceptor());

        //when
        final Object o = enhancer.create();
        HelloTarget helloTarget = (HelloTarget) o;

        //then
        assertSoftly((it) -> {
            it.assertThat(helloTarget.sayHello("kbh")).isEqualTo("HELLO KBH");
            it.assertThat(helloTarget.sayHi("kbh")).isEqualTo("HI KBH");
            it.assertThat(helloTarget.sayThankYou("kbh")).isEqualTo("THANK YOU KBH");
        });
    }

    @Test
    @DisplayName("프록시를 호출하지 않았을때 비교 테스트")
    @OneToMany
    void vsProxy() throws Exception {
        //given
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HelloTarget.class);
        enhancer.setCallback(new MethodCallResultUpperStringInterceptor());

        //when
        final Object o = enhancer.create();
        HelloTarget proxy = (HelloTarget) o;
        HelloTarget origin = new HelloTarget();


        //then
        assertSoftly((it) -> {
            it.assertThat(proxy instanceof HelloTarget).isTrue();
            it.assertThat(proxy).isNotEqualTo(origin);
            it.assertThat(proxy.sayHello("kbh")).isNotEqualTo(origin.sayHello("kbh"));
            it.assertThat(proxy.sayHi("kbh")).isNotEqualTo(origin.sayHi("kbh"));
            it.assertThat(proxy.sayThankYou("kbh")).isNotEqualTo(origin.sayThankYou("kbh"));
        });
    }

    @Test
    @DisplayName("CallbackFilter 테스트")
    @OneToMany
    void callbackFilter() throws Exception {
        //given
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HelloTarget.class);
        enhancer.setCallbacks(CallBackProxyType.getCallbacks());
        enhancer.setCallbackFilter(new HelloTargetCallbackFilter());

        //when
        final Object o = enhancer.create();
        HelloTarget proxy = (HelloTarget) o;


        //then
        assertSoftly((it) -> {
            it.assertThat(proxy.sayHello("kbh")).isEqualTo("HELLO KBH");
            it.assertThat(proxy.sayHi("kbh")).isEqualTo("HI KBH");
            it.assertThat(proxy.sayThankYou("kbh")).isEqualTo("[Thank You kbh]");
        });
    }

}
