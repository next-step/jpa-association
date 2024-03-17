package persistence.study.proxy;

import net.sf.cglib.proxy.Dispatcher;
import persistence.study.HelloTarget;

import java.util.concurrent.atomic.AtomicInteger;

public class HelloTargetDispatcher implements Dispatcher {

    private final AtomicInteger count;
    private final HelloTarget target;

    public HelloTargetDispatcher(
            final AtomicInteger count,
            final HelloTarget target) {
        this.count = count;
        this.target = target;
    }

    @Override
    public Object loadObject() throws Exception {
        count.incrementAndGet();
        return target;
    }
}
