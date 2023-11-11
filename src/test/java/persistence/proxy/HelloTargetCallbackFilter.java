package persistence.proxy;

import java.lang.reflect.Method;
import net.sf.cglib.proxy.CallbackFilter;

public class HelloTargetCallbackFilter implements CallbackFilter {
    @Override
    public int accept(Method method) {
        if (method.getName().equals("sayThankYou")) {
            return CallBackProxyType.HELLO_TARGET_BRACE_STRING.getIndex();
        } else {
            return CallBackProxyType.HELLO_TARGET_UPPER_STRING.getIndex();
        }
    }
}
