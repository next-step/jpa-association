package testsupport;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;

public class LoggingJdbcMethodInterceptor implements MethodInterceptor {
    public List<String> executedQueries;

    public LoggingJdbcMethodInterceptor(List<String> executedQueries) {
        this.executedQueries = executedQueries;
    }

    @Override
    public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        if (method.getName().equals("execute") || method.getName().equals("query")) {
            String query = args[0].toString();
            executedQueries.add(query);
        }
        return methodProxy.invokeSuper(object, args);
    }
}
