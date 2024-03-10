package study.proxy.callback;

import net.sf.cglib.proxy.FixedValue;

public class FixedValueCallback implements FixedValue {
    public static final String FIXED_VALUE = "FIXED_VALUE";

    @Override
    public Object loadObject() {
        return FIXED_VALUE;
    }
}
