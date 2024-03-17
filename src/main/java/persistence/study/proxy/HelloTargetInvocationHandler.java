package persistence.study.proxy;

import net.sf.cglib.proxy.InvocationHandler;
import persistence.study.HelloTarget;

import java.lang.reflect.Method;

public class HelloTargetInvocationHandler implements InvocationHandler {

    private final HelloTarget target;

    public HelloTargetInvocationHandler(final HelloTarget target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
        return String.format("[%s] %s", "실행시간", method.invoke(target, args));
    }
}
