package proxy.code;

import net.sf.cglib.proxy.LazyLoader;

public class SlaveClassLazyLoader<T> implements LazyLoader {
    private final Class<T> clazz;
    private final String name;

    public SlaveClassLazyLoader(final Class<T> clazz, final String name) {
        this.clazz = clazz;
        this.name = name;
    }

    @Override
    public Object loadObject() throws Exception {
        return this.clazz.getDeclaredConstructor(String.class).newInstance(this.name);
    }
}
