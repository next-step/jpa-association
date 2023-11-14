package proxy;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Arrays;

public class HelloTargetEmojiInterceptor implements MethodInterceptor {

    @Override
    public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        Object[] parameter = Arrays.stream(args)
                .map(o -> "🎉" + o.toString() + "🎊")
                .toArray(Object[]::new);

        return methodProxy.invokeSuper(object, parameter);
    }
}
