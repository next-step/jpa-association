package proxy;

import java.lang.reflect.Method;
import java.util.Arrays;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class HelloTargetInterceptor implements MethodInterceptor {

    @Override
    public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        Object[] parameter = Arrays.stream(args)
                .map(o -> o.toString().toUpperCase())
                .toArray(Object[]::new);

        return methodProxy.invokeSuper(object, parameter);
    }
}
