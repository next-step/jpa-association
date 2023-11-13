package persistence.study.proxy;

import java.util.List;

public class HelloTarget {

    private List<String> childItems;
    private boolean isChildLoaded = false;

    public String sayHello(String name) {
        return "Hello " + name;
    }

    public String sayHi(String name) {
        return "Hi " + name;
    }

    public String sayThankYou(String name) {
        return "Thank You " + name;
    }

    public void setChildItems(List<String> childItems) {
        this.childItems = childItems;
    }

    public List<String> getChildItems() {
        this.isChildLoaded = true;
        return childItems;
    }

    public boolean isChildLoaded() {
        return isChildLoaded;
    }
}
