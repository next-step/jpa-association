package persistence.model;

public class CollectionPersistentClassAssociation {

    private final PersistentClass<?> owner;
    private final boolean lazy;

    public CollectionPersistentClassAssociation(final PersistentClass<?> owner, final boolean lazy) {
        this.owner = owner;
        this.lazy = lazy;
    }

    public boolean match(final Class<?> clazz) {
        return this.owner.getEntityClass().equals(clazz);
    }

    public PersistentClass<?> getOwner() {
        return this.owner;
    }
}
