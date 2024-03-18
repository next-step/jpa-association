package persistence.entity.metadata;

public enum RelationType {

    ONE_TO_ONE("OneToOne"),
    ONE_TO_MANY("OneToMany"),
    MANY_TO_ONE("ManyToOne"),
    MANY_TO_MANY("ManyToMany");

    private final String type;

    RelationType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
