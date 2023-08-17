package CGLib;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class HelloUppercase implements MethodInterceptor {

    private final Object target;

    private HelloUppercase(Object target) {
        this.target = target;
    }

    public static Object createProxy(Object target) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(new HelloUppercase(target));
        return enhancer.create();
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        return ((String) proxy.invoke(target, args)).toUpperCase();
    }
}
