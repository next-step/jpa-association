package persistence.entity;

public class EntityEntry {
    private EntityEntryStatus status;

    public EntityEntry(EntityEntryStatus status) {
        this.status = status;
    }

    public void updateStatus(EntityEntryStatus status) {
        this.status = status;
    }
}
