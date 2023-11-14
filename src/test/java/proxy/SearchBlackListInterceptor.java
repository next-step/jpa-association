package proxy;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class SearchBlackListInterceptor implements MethodInterceptor {
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if(args[0].equals("zinzo")) {
            return BlackList.blackList((String) args[0]);
        }
        return proxy.invokeSuper(obj, args);
    }
}
