package proxy;

import proxy.domain.Hello;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class UpperCaseInvocationHandler implements InvocationHandler {

    private Hello hello;

    public UpperCaseInvocationHandler(Hello hello) {
        this.hello = hello;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String resultObject = (String) method.invoke(hello, args);
        return resultObject.toUpperCase();
    }
}
