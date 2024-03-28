package proxy.code;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class MethodResultCastUpperCaseInterceptor implements MethodInterceptor {
    @Override
    public Object intercept(final Object o, final Method method, final Object[] args, final MethodProxy methodProxy) throws Throwable {
        final Object result = methodProxy.invokeSuper(o, args);
        if (result instanceof String) {
            return ((String) result).toUpperCase();
        }

        return result;
    }
}
