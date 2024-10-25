package persistence.entity;

import java.io.Serializable;

public class EntityEntry {
    private Status status;
    private Serializable id;

    private EntityEntry(Status status, Serializable id) {
        this.status = status;
        this.id = id;
    }

    public static EntityEntry inSaving() {
        return new EntityEntry(Status.SAVING, 0L);
    }

    public static EntityEntry loading(Serializable id) {
        return new EntityEntry(Status.LOADING, id);
    }

    public boolean isManaged() {
        return status.isManaged();
    }

    public void updateStatus(Status status) {
        if (this.status.isValidStatusTransition(status)) {
            this.status = status;
            return;
        }

        throw new IllegalArgumentException("Invalid status transition from: " + this.status + " to: " + status);
    }

    public boolean isNotReadable() {
        return status == Status.DELETED || status == Status.GONE;
    }
}
