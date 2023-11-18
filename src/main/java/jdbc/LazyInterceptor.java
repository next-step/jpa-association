package jdbc;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import persistence.entity.EntityManagerFactory;
import persistence.sql.common.meta.JoinColumn;

import java.lang.reflect.Method;
import java.util.List;

public class LazyInterceptor implements MethodInterceptor {

    private final Object id;
    private final Class<?> clazz;
    private final JoinColumn joinColumn;

    public LazyInterceptor(Object id, Class<?> clazz, JoinColumn joinColumn) {
        this.id = id;
        this.clazz = clazz;
        this.joinColumn = joinColumn;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        List<?> resultList = EntityManagerFactory.get().findJoin(clazz, id, joinColumn);
        if("get".equals(method.getName())) {
            return resultList.get((Integer) args[0]);
        }

        if("size".equals(method.getName())) {
            return resultList.size();
        }

        return proxy.invokeSuper(obj, args);
    }
}
