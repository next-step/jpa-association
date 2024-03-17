package learning.proxy;

import java.util.List;

public class HelloTarget {
    public HelloTarget() {
    }

    public HelloTarget(List<String> objectNames, String name) {
        objectNames.add(name);
    }

    public String sayHello(String name) {
        return "Hello " + name;
    }

    public String sayHi(String name) {
        return "Hi " + name;
    }

    public String sayThankYou(String name) {
        return "Thank You " + name;
    }
}
