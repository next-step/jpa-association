package persistence.study;

public class HelloTarget {

    private Car car;

    public HelloTarget() {
        this.car = new Car();
    }

    public HelloTarget(Car car) {
        this.car = car;
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

    public Car getCar() {
        return car;
    }
}
