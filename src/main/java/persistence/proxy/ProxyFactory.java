package persistence.proxy;

public interface ProxyFactory {
    Object createProxy(Class<?> clazz);
}
