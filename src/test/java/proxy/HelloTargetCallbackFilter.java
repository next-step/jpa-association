package proxy;

import net.sf.cglib.proxy.CallbackFilter;

import java.lang.reflect.Method;

public class HelloTargetCallbackFilter implements CallbackFilter {
    @Override
    public int accept(Method method) {
        if (method.getName().equals("sayHello")) {
            return 0;
        } else {
            return 1;
        }
    }
}
