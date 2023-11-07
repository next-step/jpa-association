package persistence.study.proxy;

public class HelloUpperCaseTarget extends HelloTarget {

    @Override
    public String sayHello(String name) {
        return super.sayHello(name).toUpperCase();
    }

    @Override
    public String sayHi(String name) {
        return super.sayHi(name).toUpperCase();
    }

    @Override
    public String sayThankYou(String name) {
        return super.sayThankYou(name).toUpperCase();
    }
}
