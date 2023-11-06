package persistence.study;

import fixtures.HelloTarget;
import net.sf.cglib.proxy.Enhancer;
import org.junit.jupiter.api.Test;
import persistence.proxy.ConvertToUpperCaseInterceptor;

public class HelloProxyTest {

    @Test
    void test() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HelloTarget.class);
        enhancer.setCallback(new ConvertToUpperCaseInterceptor());
        Object obj = enhancer.create();
        HelloTarget helloTarget = (HelloTarget) obj;
        System.out.print(helloTarget.sayHello("mj"));
    }
}
