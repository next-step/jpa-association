package persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EntityEntryTest {

    @Test
    @DisplayName("Saving -> Managed")
    void updateStatus1() {
        EntityEntry entityEntry = EntityEntry.inSaving();

        entityEntry.updateStatus(Status.MANAGED);

        assertThat(entityEntry.isManaged()).isTrue();
    }

    @Test
    @DisplayName("Loading -> Managed")
    void updateStatus2() {
        EntityEntry entityEntry = EntityEntry.loading(1);

        entityEntry.updateStatus(Status.MANAGED);

        assertThat(entityEntry.isManaged()).isTrue();
    }

    @Test
    @DisplayName("Managed -> Loading")
    void updateStatus3() {
        EntityEntry entityEntry = EntityEntry.inSaving();
        entityEntry.updateStatus(Status.MANAGED);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> entityEntry.updateStatus(Status.LOADING));
        assertThat(e).hasMessage("Invalid status transition from: MANAGED to: LOADING");
    }

    @Test
    @DisplayName("Managed -> Deleted")
    void updateStatus4() {
        EntityEntry entityEntry = EntityEntry.inSaving();
        entityEntry.updateStatus(Status.MANAGED);

        entityEntry.updateStatus(Status.DELETED);
        assertThat(entityEntry.isManaged()).isFalse();
    }

}
