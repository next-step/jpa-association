package persistence.study;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

class UpperCaseMethodInterceptor implements MethodInterceptor {
    private final Object target;

    public UpperCaseMethodInterceptor(Object target) {
        this.target = target;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof String) {
                args[i] = ((String) args[i]).toUpperCase();
            }
        }
        return method.invoke(target, args);
    }
}
