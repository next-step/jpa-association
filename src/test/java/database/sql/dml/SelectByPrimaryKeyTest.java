package database.sql.dml;

import database.mapping.EntityMetadata;
import database.mapping.EntityMetadataFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SelectByPrimaryKeyTest {
    private final SelectByPrimaryKey selectByPrimaryKey;

    {
        EntityMetadata entityMetadata = EntityMetadataFactory.get(Person4.class);
        selectByPrimaryKey = new SelectByPrimaryKey(
                entityMetadata.getTableName(),
                entityMetadata.getAllColumnNamesWithAssociations(),
                entityMetadata.getPrimaryKeyName(),
                entityMetadata.getGeneralColumnNames()
        );
    }

    @Test
    void buildSelectPrimaryKeyQuery() {
        String actual = selectByPrimaryKey.byId(1L).buildQuery();
        assertThat(actual).isEqualTo("SELECT id, nick_name, old, email FROM users WHERE id = 1");
    }
}
