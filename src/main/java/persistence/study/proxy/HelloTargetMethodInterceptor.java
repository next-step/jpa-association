package persistence.study.proxy;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import persistence.study.HelloTarget;

import java.lang.reflect.Method;

public class HelloTargetMethodInterceptor implements MethodInterceptor {

    private final HelloTarget target;

    public HelloTargetMethodInterceptor(final HelloTarget target) {
        this.target = target;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        return method.invoke(target, args)
                .toString()
                .toUpperCase();
    }
}
