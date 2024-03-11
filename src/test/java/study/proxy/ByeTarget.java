package study.proxy;

public class ByeTarget extends HelloTarget{
    @Override
    public String sayHello(String name) {
        return "Bye " + name;
    }
}
