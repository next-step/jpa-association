package study.proxy.callback;

import net.sf.cglib.proxy.Dispatcher;
import study.proxy.ByeTarget;

public class DispatcherCallback implements Dispatcher {
    @Override
    public Object loadObject() throws Exception {
        return new ByeTarget();
    }
}

