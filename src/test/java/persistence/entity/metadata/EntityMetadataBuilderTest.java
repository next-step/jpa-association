package persistence.entity.metadata;

import org.junit.jupiter.api.Test;
import persistence.entity.Person;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EntityMetadataBuilderTest {

    @Test
    public void createEntityMetadata() {
        EntityMetadata metadata = EntityMetadataBuilder.build(Person.class);

        assertAll(
                () -> assertEquals("users", metadata.getEntityTable().getTableName()),
                () -> assertEquals("Person", metadata.getEntityTable().getEntityName()),
                () -> assertEquals(4, metadata.getColumns().getColumns().size()),
                () -> assertEquals("id", metadata.getColumns().getIdColumn().getColumnName())
        );
    }

}
