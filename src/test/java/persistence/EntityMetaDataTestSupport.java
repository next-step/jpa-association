package persistence;

import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.model.PersistentClassMapping;
import persistence.sql.Order;
import persistence.sql.OrderItem;
import persistence.sql.ddl.PersonV1;
import persistence.sql.ddl.PersonV2;
import persistence.sql.ddl.PersonV3;

public abstract class EntityMetaDataTestSupport {
    private static final Logger log = LoggerFactory.getLogger(EntityMetaDataTestSupport.class);
    @BeforeAll
    static void setUpEntityMetaDataTestSupportClass() {
        log.info("set up test class");
        PersistentClassMapping.putPersistentClass(PersonV1.class);
        PersistentClassMapping.putPersistentClass(PersonV2.class);
        PersistentClassMapping.putPersistentClass(PersonV3.class);
        PersistentClassMapping.putPersistentClass(Order.class);
        PersistentClassMapping.putPersistentClass(OrderItem.class);
    }
}
