package persistence.entity;

import net.sf.cglib.proxy.CallbackFilter;

import java.lang.reflect.Method;

public class EntityCallbackFilter implements CallbackFilter {
    @Override
    public int accept(Method method) {
        String methodName = method.getName();

        if (methodName.equals("getId")) {
            return 0;
        }
        return 1;
    }
}
