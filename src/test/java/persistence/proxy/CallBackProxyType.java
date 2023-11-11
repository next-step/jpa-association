package persistence.proxy;

import java.util.Arrays;
import java.util.Comparator;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.MethodInterceptor;

public enum CallBackProxyType {
    HELLO_TARGET_UPPER_STRING(new MethodCallResultUpperStringInterceptor(), 1),
    HELLO_TARGET_BRACE_STRING(new MethodCallResultBraceStringInterceptor(), 0);

    private final MethodInterceptor methodInterceptor;
    private final int index;

    CallBackProxyType(MethodInterceptor methodInterceptor, int index) {
        this.methodInterceptor = methodInterceptor;
        this.index = index;
    }

    public MethodInterceptor getMethodInterceptor() {
        return methodInterceptor;
    }
    public int getIndex() {
        return index;
    }

    public static Callback[] getCallbacks() {
        return Arrays.stream(CallBackProxyType.values())
                .sorted(Comparator.comparingInt(CallBackProxyType::getIndex))
                .map(CallBackProxyType::getMethodInterceptor)
                .toArray(Callback[]::new);
    }
}
