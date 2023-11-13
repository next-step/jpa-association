package proxy;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HelloTargetTest {
    @Nested
    @DisplayName("CGLib Proxy 적용")
    class proxy {
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

    @Nested
    @DisplayName("callback 적용해보기")
    class callback {
        @Test
        @DisplayName("sayHello()의 경우 이모지 찍어주는 interceptor로 callback 적용해보기")
        void callbackFilter() {
            //given
            final String expected = "Hello 🎉zinzo🎊";

            Object object = 프록시_생성();

            //when
            HelloTarget helloTarget = (HelloTarget) object;
            String result = helloTarget.sayHello("zinzo");

            //then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("sayHi()의 경우 대문자로 바꿔주는 interceptor 적용")
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

        private Object 프록시_생성() {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(HelloTarget.class);
            Callback[] callbacks = new Callback[]{
                    new HelloTargetEmojiInterceptor(),
                    new HelloTargetInterceptor()
            };
            enhancer.setCallbacks(callbacks);
            enhancer.setCallbackFilter(new HelloTargetCallbackFilter());

            return enhancer.create();
        }
    }
}
