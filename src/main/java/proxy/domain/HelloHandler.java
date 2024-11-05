package proxy.domain;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class HelloHandler implements InvocationHandler {
    private Hello hello;

    public HelloHandler(Hello hello) {
        this.hello = hello;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(hello, args);
    }
}
