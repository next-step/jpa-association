package persistence.entity.proxy;

import net.sf.cglib.proxy.FixedValue;
import persistence.entity.common.EntityId;

public class EntityGetIdProxy implements FixedValue {

    private final EntityId id;

    public EntityGetIdProxy(EntityId id) {
        this.id = id;
    }

    @Override
    public Object loadObject() {
        return id.value();
    }
}
