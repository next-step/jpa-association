package persistence.study.proxy;

import net.sf.cglib.proxy.ProxyRefDispatcher;
import persistence.study.HelloTarget;

public class HelloTargetProxyRefDispatcher implements ProxyRefDispatcher {

    private final HelloTarget target;

    public HelloTargetProxyRefDispatcher(final HelloTarget target) {
        this.target = target;
    }

    @Override
    public Object loadObject(Object object) throws Exception {
        return target;
    }
}
