package persistence.study;

import net.sf.cglib.proxy.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.shouldHaveThrown;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("HelloTarget 클래스에서 프록시를 통해")
class HelloTargetTest {

    private final HelloTarget target = new HelloTarget();

    @DisplayName("인자로 넘긴 이름만 대문자로 변환된다.")
    @Test
    void sayHello() {
        HelloTarget helloTarget = createProxy(target);

        assertThat(helloTarget.sayHello("John")).isEqualTo("Hello JOHN");
    }

    @DisplayName("인자로 넘긴 이름만 대문자로 변환된다.")
    @Test
    void sayHi() {
        HelloTarget helloTarget = createProxy(target);

        assertThat(helloTarget.sayHi("Hong Gil Dong")).isEqualTo("Hi HONG GIL DONG");
    }

    @DisplayName("인자로 넘긴 이름만 대문자로 변환된다.")
    @Test
    void sayThankYou() {
        HelloTarget helloTarget = createProxy(target);

        assertThat(helloTarget.sayThankYou("James")).isEqualTo("Thank You JAMES");
    }

    private static HelloTarget createProxy(HelloTarget helloTarget) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HelloTarget.class);
        enhancer.setCallback(new UpperCaseMethodInterceptor(helloTarget));
        return (HelloTarget) enhancer.create();
    }

    @DisplayName("MethodInterceptor를 사용하여 메서드 결과를 대문자로 변경하는 프록시를 적용한다.")
    @Test
    void createProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HelloTarget.class);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                Object result = method.invoke(target, args);

                if (result instanceof String) {
                    result = ((String) result).toUpperCase();
                }
                return result;
            }
        });
        HelloTarget helloTarget = (HelloTarget) enhancer.create();


        assertThat(helloTarget.sayHello("John")).isEqualTo("HELLO JOHN");
    }

    @DisplayName("NoOp을 사용하여 메서드 결과를 그대로 반환하는 프록시를 적용한다.")
    @Test
    void createProxyWithNoOp() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HelloTarget.class);
        enhancer.setCallback(NoOp.INSTANCE);
        HelloTarget helloTarget = (HelloTarget) enhancer.create();

        assertThat(helloTarget.sayHello("John")).isEqualTo("Hello John");
    }

    @DisplayName("InvocationHandler를 사용하여 메서드 결과를 대문자로 변경하는 프록시를 적용한다.")
    @Test
    void createProxyWithInvocationHandler() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HelloTarget.class);
        enhancer.setCallback(new InvocationHandler() {
            @Override
            public Object invoke(Object o, Method method, Object[] args) throws Throwable {
                Object result = method.invoke(target, args);

                if (result instanceof String) {
                    result = ((String) result).toUpperCase();
                }
                return result;
            }
        });
        HelloTarget helloTarget = (HelloTarget) enhancer.create();
        assertThat(helloTarget.sayHello("John")).isEqualTo("HELLO JOHN");
    }

    @Test
    @DisplayName("LazyLoader를 사용하여 프록시 객체를 반환한다.")
    void createProxyWithLowerProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Car.class);
        enhancer.setCallback(new LazyLoader() {
            @Override
            public Object loadObject() throws Exception {
                return new Car("프록시", 100);
            }
        });
        Car proxy = (Car) enhancer.create();

        HelloTarget helloTarget = new HelloTarget(proxy);

        assertAll(
                () -> assertThat(helloTarget.getCar()).isNotExactlyInstanceOf(Car.class),
                () -> assertThat(helloTarget.getCar().testGetName()).isEqualTo("test : 프록시"),
                () -> assertThat(helloTarget.getCar().testGetPrice()).isEqualTo("test : 100")
        );
    }
}
