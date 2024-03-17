package persistence.study.proxy;

import net.sf.cglib.proxy.FixedValue;
import persistence.study.HelloTarget;

public class HelloTargetFixedValue implements FixedValue {

    private final HelloTarget target;

    public HelloTargetFixedValue(final HelloTarget target) {
        this.target = target;
    }

    @Override
    public Object loadObject() throws Exception {
        return "고정된 값이 나갑니다.";
    }
}
