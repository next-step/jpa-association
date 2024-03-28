package proxy.code;

import net.sf.cglib.proxy.FixedValue;

public class RepositoryFixedValue implements FixedValue {
    @Override
    public Object loadObject() throws Exception {
        return "FixedValueRepository";
    }
}
