package persistence.proxy;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import domain.HelloTarget;
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
    
}
