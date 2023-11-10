package persistence.proxy;

import java.lang.reflect.Method;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class MethodCallResultUpperStringInterceptor implements MethodInterceptor {
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {

        Object returnValue = proxy.invokeSuper(obj, args);
        if (returnValue instanceof String) {
            return ((String) returnValue).toUpperCase();
        }

        return returnValue;
    }
}
