package persistence.study.proxy;

import java.util.ArrayList;
import java.util.List;

public class HelloTarget {

    private List<Address> address = new ArrayList<>();

    private boolean isLoaded = false;

    public HelloTarget() {
    }

    public HelloTarget(List<Address> address) {
        this.address = address;
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

    public boolean isLoaded() {
        return isLoaded;
    }

    public List<Address> getAddress() {
        isLoaded = true;
        return address;
    }
}
