package proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import proxy.code.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class ProxyTest {
    private static final String HELLO_CONSTANCE = "HELLO ";
    private static final String HI_CONSTANCE = "HI ";
    private static final String THANK_YOU_CONSTANCE = "THANK YOU ";
    private static final String NAME_CONSTANCE = "proxy";
    private static final String NAME_CONSTANCE_UPPER_CASE = NAME_CONSTANCE.toUpperCase();

    @DisplayName("HelloTarget 객체를 프록시로 생성해 MethodInterceptor 로 메서드 결과를 대문자로 반환")
    @Test
    public void returnHelloTargetUpperCase() throws Exception {
        // given
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HelloTarget.class);
        enhancer.setCallback(new MethodResultCastUpperCaseInterceptor());
        final HelloTarget hello = (HelloTarget) enhancer.create();

        // when
        final String helloResult = hello.sayHello(NAME_CONSTANCE);
        final String hiResult = hello.sayHi(NAME_CONSTANCE);
        final String thankYoeResult = hello.sayThankYou(NAME_CONSTANCE);

        // then
        assertAll(
                () -> assertThat(helloResult).isEqualTo("HELLO "+ NAME_CONSTANCE_UPPER_CASE),
                () -> assertThat(hiResult).isEqualTo("HI "+ NAME_CONSTANCE_UPPER_CASE),
                () -> assertThat(thankYoeResult).isEqualTo("THANK YOU " + NAME_CONSTANCE_UPPER_CASE)
        );
    }

    @DisplayName("NoOp 콜백으로 인터셉터를 무시하는 메서드를 지정한다")
    @Test
    public void proxyWithNoOp() throws Exception {
        // given
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HelloTarget.class);
        enhancer.setCallback(new MethodResultCastUpperCaseInterceptor());
        enhancer.setCallback(NoOp.INSTANCE);
        final HelloTarget hello = (HelloTarget) enhancer.create();

        // when
        final String helloResult = hello.sayHello(NAME_CONSTANCE);
        final String hiResult = hello.sayHi(NAME_CONSTANCE);
        final String thankYoeResult = hello.sayThankYou(NAME_CONSTANCE);

        // then
        assertAll(
                () -> assertThat(helloResult).isEqualTo("Hello " + NAME_CONSTANCE),
                () -> assertThat(hiResult).isEqualTo("Hi " + NAME_CONSTANCE),
                () -> assertThat(thankYoeResult).isEqualTo("Thank You " + NAME_CONSTANCE)
        );
    }

    @DisplayName("InvocationHandler 로 메서드 호출 전후로 로그를 남긴다")
    @Test
    public void proxyWithInvocationHandler() throws Exception {
        // given
        final PrintStream printStream = System.out;
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        final HelloTarget object = new HelloTarget();

        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HelloTarget.class);
        enhancer.setCallback(new MethodLogger(object));
        final HelloTarget hello = (HelloTarget) enhancer.create();

        final String result = "Hello " + NAME_CONSTANCE;

        // when
        final String helloResult = hello.sayHello(NAME_CONSTANCE);

        // then
        System.setOut(printStream);
        assertAll(
                () -> assertThat(outputStream.toString().trim()).isEqualTo("HelloTarget::sayHello called."),
                () -> assertThat(helloResult).isEqualTo(result)
        );
    }

    @DisplayName("LazyLoader 를 이용해 객체를 지연 초기화 한다")
    @Test
    public void proxyWithLazyLoader() throws Exception {
        // given
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(SlaveClass.class);
        final String name = "SlaveClass";
        enhancer.setCallback(new SlaveClassLazyLoader<>(SlaveClass.class, name));
        final SlaveClass slaveClass = (SlaveClass) enhancer.create();
        final MasterClass masterClass = new MasterClass(slaveClass);

        // when
        final String result = masterClass.getSlaveClass().getName();

        // then
        assertThat(result).isEqualTo(name);
    }

    @DisplayName("Dispatcher 를 이용해 객체를 구분해 초기화 한다")
    @Test
    public void proxyWithDispatcher() throws Exception {
        // given
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Repository.class);
        enhancer.setCallback(new RepositoryDispatcher(Service.class.getDeclaredFields()[0].getName()));
        final Repository repo = (Repository) enhancer.create();
        final Service service = new Service(repo);

        // when
        final String result = service.getName();

        // then
        assertThat(result).isEqualTo("ItemRepository");
    }
    
    @DisplayName("FixedValue 를 이용해 고정 값을 반환 받는다")
    @Test
    public void proxyWithFixedValue() throws Exception {
        // given
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Repository.class);
        enhancer.setCallback(new RepositoryFixedValue());
        final Repository repo = (Repository) enhancer.create();
        final Service service = new Service(repo);

        // when
        final String result = service.getName();

        // then
        assertThat(result).isEqualTo("FixedValueRepository");
    }

}
