package persistence.entity;

import net.sf.cglib.proxy.FixedValue;

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
