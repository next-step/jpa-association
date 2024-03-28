package proxy.code;

public class MasterClass {
    private final SlaveClass slaveClass;

    public MasterClass(final SlaveClass slaveClass) {
        this.slaveClass = slaveClass;
    }

    public SlaveClass getSlaveClass() {
        return this.slaveClass;
    }
}
