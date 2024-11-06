package proxy;

import builder.dml.EntityData;
import builder.dml.JoinEntityData;
import persistence.EntityLoader;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

public class ProxyInvocationHandler implements InvocationHandler {

    private final JoinEntityData joinEntityData;
    private final EntityLoader entityLoader;

    public ProxyInvocationHandler(JoinEntityData joinEntityData, EntityLoader entityLoader) {
        this.joinEntityData = joinEntityData;
        this.entityLoader = entityLoader;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<?> list = entityLoader.findByIdLazy(joinEntityData);
        return method.invoke(list, args);
    }
}
