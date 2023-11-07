package persistence.study.proxy;

import net.sf.cglib.proxy.Dispatcher;

public class UpperCaseDispatcher implements Dispatcher {

    @Override
    public Object loadObject() throws Exception {
        return new HelloUpperCaseTarget();
    }
}
