package persistence.study.proxy;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class HelloTargetToUpperCaseInterceptor implements MethodInterceptor {

    @Override
    public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        String returnValue = (String) methodProxy.invokeSuper(object, args);
        return returnValue.toUpperCase();
    }
}
