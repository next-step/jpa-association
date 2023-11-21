package persistence.entity;

public enum EntityStatus {
    MANAGED,
    READ_ONLY,
    DELETED,
    GONE,
    LOADING,
    SAVING;

    public boolean isManaged() {
        return this.equals(MANAGED);
    }

    public boolean isSaving() {
        return this.equals(SAVING);
    }

    public boolean isDeleted() {
        return this.equals(DELETED);
    }
}
