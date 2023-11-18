package study;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.NoOp;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.fixture.PersonFixtureStep3;
import persistence.sql.fixture.PersonInstances;

public class ProxyTest {
  @Test
  @DisplayName("프록시를 사용해 클래스가 RETURN 하는 값을 그대로 return 합니다.")
  void proxyToSame(){
    Enhancer enhancer = new Enhancer();

    enhancer.setSuperclass(HelloTarget.class);
    enhancer.setCallback(NoOp.INSTANCE);

    Object proxy = enhancer.create();
    HelloTarget helloTarget = (HelloTarget) proxy;

    assertThat(helloTarget.sayHello("simon")).isEqualTo("Hello simon");
    assertThat(helloTarget.sayHi("simon")).isEqualTo("Hi simon");
    assertThat(helloTarget.sayThankYou("simon")).isEqualTo("Thank You simon");
  }

  @Test
  @DisplayName("프록시를 사용해 클래스가 RETURN 하는 값을 대문자로 변경하였습니다.")
  void proxyToCapital(){
    Enhancer enhancer = new Enhancer();

    enhancer.setSuperclass(HelloTarget.class);
    enhancer.setCallback(new MethodCallLogInterceptor());

    Object proxy = enhancer.create();
    HelloTarget helloTarget = (HelloTarget) proxy;

    assertThat(helloTarget.sayHello("simon")).isEqualTo("HELLO SIMON");
    assertThat(helloTarget.sayHi("simon")).isEqualTo("HI SIMON");
    assertThat(helloTarget.sayThankYou("simon")).isEqualTo("THANK YOU SIMON");
  }
  @Test
  @DisplayName("프록시를 사용해 클래스가 lazyLoading을 합니다.")
  void proxyToLazyLoading(){
    Enhancer enhancer = new Enhancer();

    enhancer.setSuperclass(PersonFixtureStep3.class);
    enhancer.setCallback(new MethodLazyLoader());

    Object proxy = enhancer.create();
    PersonFixtureStep3 person = (PersonFixtureStep3) proxy;

    assertThat(person.getName()).isEqualTo("제임스");
    assertThat(person.getId()).isEqualTo(1L);
    assertThat(person.getAge()).isEqualTo(21);
    assertThat(person).isEqualTo(PersonInstances.첫번째사람);
  }
  public class MethodCallLogInterceptor implements MethodInterceptor {

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy)
        throws Throwable {
      Object returnValue = proxy.invokeSuper(obj, args);

      return returnValue.toString().toUpperCase();
    }
  }

  public class MethodLazyLoader implements LazyLoader{

    @Override
    public Object loadObject() throws Exception {
      return PersonInstances.첫번째사람;
    }
  }

}
