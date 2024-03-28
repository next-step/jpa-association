package proxy.code;

import net.sf.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;

public class MethodLogger implements InvocationHandler {

    private final Object originObject;

    public MethodLogger(Object originObject) {
        this.originObject = originObject;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        System.out.println(method.getDeclaringClass().getSimpleName() + "::" + method.getName() + " called.");
        return method.invoke(originObject, args);
    }
}
