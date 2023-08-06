package persistence;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JoinTableTest {

    @Test
    void name() {
        JoinTable joinTable = new JoinTable("order_item", "order_id");

        assertEquals("order_item", joinTable.name());
    }

    @Test
    void column() {
        JoinTable joinTable = new JoinTable("order_item", "order_id");

        assertEquals("order_id", joinTable.column());
    }

}
