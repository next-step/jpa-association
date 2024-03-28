package proxy.code;

public class SlaveClass {

    private final String name;

    public SlaveClass() {
        this("no name");
    }

    public SlaveClass(final String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

}
