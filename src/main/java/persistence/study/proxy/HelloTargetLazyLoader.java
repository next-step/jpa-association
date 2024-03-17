package persistence.study.proxy;

import net.sf.cglib.proxy.LazyLoader;
import persistence.study.HelloTarget;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HelloTargetLazyLoader implements LazyLoader {

    private final List<String> messages;

    public HelloTargetLazyLoader(final List<String> messages) {
        this.messages = messages;
    }

    @Override
    public Object loadObject() throws Exception {
        messages.add("생성되었습니다.");
        return new HelloTarget();
    }
}
