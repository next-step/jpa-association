package persistence.entity.context;

import persistence.entity.Status;

public interface EntityEntry {
    Status getStatus();

    void setSaving();

    void setManaged();

    void setLoading();

    void setDeleted();

    void setGone();

    void setReadOnly();

    boolean isReadOnly();
}
