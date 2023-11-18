package persistence.study;

import domain.HelloTarget;
import net.sf.cglib.proxy.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProxyTest {
	@DisplayName("HelloTarget의 메소드 실행 시, 정해진 문자열을 반환한다.")
	@Test
	void test_sayHello() {
		HelloTarget helloTarget = new HelloTarget();

		Assertions.assertAll(
				() -> assertEquals(helloTarget.sayHello("hhhhhwi"), "Hello hhhhhwi"),
				() -> assertEquals(helloTarget.sayHi("hhhhhwi"), "Hi hhhhhwi"),
				() -> assertEquals(helloTarget.sayThankYou("hhhhhwi"), "Thank You hhhhhwi")
		);
	}

	@DisplayName("프록시 Interceptor 콜백을 사용하여 Hello 대문자를 반환한다.")
	@Test
	void test_sayHello_With_Interceptor() {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(HelloTarget.class);
		enhancer.setCallback(new UpperCaseInterceptor());

		HelloTarget helloTarget = (HelloTarget) enhancer.create();

		Assertions.assertAll(
				() -> assertEquals(helloTarget.sayHello("hhhhhwi"), "HELLO HHHHHWI"),
				() -> assertEquals(helloTarget.sayHi("hhhhhwi"), "HI HHHHHWI"),
				() -> assertEquals(helloTarget.sayThankYou("hhhhhwi"), "THANK YOU HHHHHWI")
		);
	}

	@DisplayName("프록시 콜백 필터를 통해 알맞는 문자열을 반환한다.")
	@Test
	void test_sayHello_With_CallbackFilter() {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(HelloTarget.class);
		enhancer.setCallbacks(new Callback[] {
				new UpperCaseInterceptor(),
				new LowerCaseInterceptor()
		});
		enhancer.setCallbackFilter(new HelloTargetCallbackFilter());

		HelloTarget helloTarget = (HelloTarget) enhancer.create();

		Assertions.assertAll(
				() -> assertEquals(helloTarget.sayHello("hhhhhwi"), "hello hhhhhwi"),
				() -> assertEquals(helloTarget.sayHi("hhhhhwi"), "hi hhhhhwi"),
				() -> assertEquals(helloTarget.sayThankYou("hhhhhwi"), "THANK YOU HHHHHWI")
		);
	}

	static class UpperCaseInterceptor implements MethodInterceptor {
		@Override
		public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
			String returnValue = (String) proxy.invokeSuper(obj, args);

			return returnValue.toUpperCase();
		}
	}

	static class LowerCaseInterceptor implements MethodInterceptor {
		@Override
		public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
			String returnValue = (String) proxy.invokeSuper(obj, args);

			return returnValue.toLowerCase();
		}
	}

	static class HelloTargetCallbackFilter implements CallbackFilter {
		@Override
		public int accept(Method method) {
			if(method.getName().contains("ThankYou")) {
				return 0;
			}

			return 1;
		}
	}
}
