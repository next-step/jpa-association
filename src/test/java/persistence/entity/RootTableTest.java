package persistence.entity;

import domain.Order;
import org.junit.jupiter.api.Test;
import persistence.CustomTable;

import static org.junit.jupiter.api.Assertions.*;

class RootTableTest {

    @Test
    void name() {
        Class<Order> targetClass = Order.class;
        CustomTable customTable = CustomTable.of(targetClass);
        RootTable rootTable = new RootTable(customTable.name(), UniqueColumn.of(targetClass));

        assertEquals("orders", rootTable.name());
    }

    @Test
    void uniqueColumn() {
        Class<Order> targetClass = Order.class;
        CustomTable customTable = CustomTable.of(targetClass);
        RootTable rootTable = new RootTable(customTable.name(), UniqueColumn.of(targetClass));

        assertEquals("id", rootTable.uniqueColumn());
    }
}
