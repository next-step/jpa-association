package persistence.proxy;

public class HelloTarget {

    private final boolean proxied;

    public HelloTarget() {
        this(false);
    }

    public HelloTarget(final boolean proxied) {
        this.proxied = proxied;
    }

    public String sayHello(final String name) {
        return "Hello " + name;
    }

    public String sayHi(final String name) {
        return "Hi " + name;
    }

    public String sayThankYou(final String name) {
        return "Thank You " + name;
    }

    public boolean isProxied() {
        return this.proxied;
    }
}
