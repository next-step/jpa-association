package persistence.model;

import persistence.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;

public class CollectionPersistentClass {

    private final List<CollectionPersistentClassAssociation> owners;
    private final Class<?> clazz;
    private final EntityFields fields;
    private final String entityName;
    private final String tableName;

    public CollectionPersistentClass(final PersistentClass<?> persistentClass) {
        this.owners = new ArrayList<>();
        this.clazz = persistentClass.getEntityClass();
        this.fields = persistentClass.getEntityFields();
        this.entityName = persistentClass.getEntityName();
        this.tableName = persistentClass.getTableName();
    }

    public void addAssociation(final PersistentClass<?> owner, final boolean lazy) {
        owners.add(new CollectionPersistentClassAssociation(owner, lazy));
    }

    public boolean hasOwner(final Class<?> ownerClass) {
        return this.owners.stream().filter(association -> association.match(ownerClass)).anyMatch(x -> true);
    }

    public <T> PersistentClass<T> getOwner(final Class<T> clazz) {
        return (PersistentClass<T>) this.owners.stream()
                .filter(association -> association.match(clazz))
                .map(CollectionPersistentClassAssociation::getOwner)
                .findFirst().get();
    }

    public Class<?> getEntityClass() {
        return this.clazz;
    }

    public Object createInstance() {
        return ReflectionUtils.createInstance(clazz);
    }

    public String getTableName() {
        return this.tableName;
    }

    public List<AbstractEntityField> getColumns() {
        return this.fields.getColumns();
    }
}
