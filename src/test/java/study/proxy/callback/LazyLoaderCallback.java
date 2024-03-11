package study.proxy.callback;

import net.sf.cglib.proxy.LazyLoader;
import study.proxy.HelloTarget;

public class LazyLoaderCallback implements LazyLoader {
    @Override
    public Object loadObject() throws Exception {
        System.out.println("Creating TargetClass object lazily...");
        return new HelloTarget();
    }
}
