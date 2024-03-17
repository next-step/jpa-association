package persistence.study.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.study.HelloTarget;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;


class HelloTargetTest {

    private static final String NAME = "성수";
    private HelloTarget helloTarget;

    @BeforeEach
    void setup() {
        this.helloTarget = new HelloTarget();
    }

    @DisplayName("methodInterceptor을 이용한 대문자로 변환 시킨다.")
    @Test
    void methodInterceptorTest() {
        final HelloTarget hello = (HelloTarget) Enhancer.create(
                HelloTarget.class,
                new HelloTargetMethodInterceptor(helloTarget)
        );

        assertAll(
                () -> assertThat(hello.sayHello(NAME)).isEqualTo("HELLO 성수"),
                () -> assertThat(hello.sayHi(NAME)).isEqualTo("HI 성수"),
                () -> assertThat(hello.sayThankYou(NAME)).isEqualTo("THANK YOU 성수")
        );
    }

    @DisplayName("NoOp을 이용하여 메소드를 실행시킨다.")
    @Test
    void noOpTest() {
        final HelloTarget hello = (HelloTarget) Enhancer.create(
                HelloTarget.class,
                NoOp.INSTANCE
        );

        assertAll(
                () -> assertThat(hello.sayHello(NAME)).isEqualTo("Hello 성수"),
                () -> assertThat(hello.sayHi(NAME)).isEqualTo("Hi 성수"),
                () -> assertThat(hello.sayThankYou(NAME)).isEqualTo("Thank You 성수")
        );
    }

    @DisplayName("Dispatcher을 사용하여 메소드를 실행시킨다.")
    @Test
    void dispatcherTest() {
        final AtomicInteger count = new AtomicInteger(0);

        final HelloTarget hello = (HelloTarget) Enhancer.create(
                HelloTarget.class,
                new HelloTargetDispatcher(count, helloTarget)
        );

        hello.sayHello(NAME);
        hello.sayHello(NAME);

        assertThat(count.get()).isEqualTo(2);
    }

    @DisplayName("FixedValue를 사용하여 메소드를 실행시킨다.")
    @Test
    void fixedValueTest() {
        final HelloTarget hello = (HelloTarget) Enhancer.create(
                HelloTarget.class,
                new HelloTargetFixedValue(helloTarget)
        );

        assertAll(
                () -> assertThat(hello.sayHello(NAME)).isEqualTo("고정된 값이 나갑니다."),
                () -> assertThat(hello.sayHi(NAME)).isEqualTo("고정된 값이 나갑니다."),
                () -> assertThat(hello.sayThankYou(NAME)).isEqualTo("고정된 값이 나갑니다.")
        );
    }

    @DisplayName("InvocationHandler을 사용하여 메소드를 실행시킨다.")
    @Test
    void invocationHandlerTest() {
        final HelloTarget hello = (HelloTarget) Enhancer.create(
                HelloTarget.class,
                new HelloTargetInvocationHandler(helloTarget)
        );

        assertAll(
                () -> assertThat(hello.sayHello(NAME)).isEqualTo("[실행시간] Hello 성수"),
                () -> assertThat(hello.sayHi(NAME)).isEqualTo("[실행시간] Hi 성수"),
                () -> assertThat(hello.sayThankYou(NAME)).isEqualTo("[실행시간] Thank You 성수")
        );
    }

    @DisplayName("LazyLoader을 사용하여 메소드를 실행한다.")
    @Test
    void lazyLoaderTest() {
        List<String> message = new ArrayList<>();

        final HelloTarget hello = (HelloTarget) Enhancer.create(
                HelloTarget.class,
                new HelloTargetLazyLoader(message)
        );

        message.add("일단 넣기");
        hello.sayHello(NAME);

        assertThat(message).containsExactly("일단 넣기", "생성되었습니다.");
    }

    @DisplayName("ProxyRefDispatcher을 사용하여 메소드를 실행한다.")
    @Test
    void proxyRefDispatcherTest() {
        final HelloTarget hello = (HelloTarget) Enhancer.create(
                HelloTarget.class,
                new HelloTargetProxyRefDispatcher(helloTarget)
        );

        assertAll(
                () -> assertThat(hello.sayHello(NAME)).isEqualTo("Hello 성수"),
                () -> assertThat(hello.sayHi(NAME)).isEqualTo("Hi 성수"),
                () -> assertThat(hello.sayThankYou(NAME)).isEqualTo("Thank You 성수")
        );
    }

}
