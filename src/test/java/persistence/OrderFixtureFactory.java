package persistence;

import persistence.sql.LazyOrderItem;
import persistence.sql.Order;
import persistence.sql.EagerOrderItem;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OrderFixtureFactory {

    public static Order generateOrderStub(final Long id, final List<EagerOrderItem> eagerOrderItems, final List<LazyOrderItem> lazyOrderItems) {
        final String orderNumber = "1";
        return new Order(id, orderNumber, eagerOrderItems, lazyOrderItems);
    }

    public static Order generateOrderStub(final Long id) {
        return generateOrderStub(id, generateEagerOrderItemsStub(), generateLazyItemsStub());
    }

    public static Order generateOrderStub() {
        return generateOrderStub(0L, generateEagerOrderItemsStub(), generateLazyItemsStub());
    }

    public static List<EagerOrderItem> generateEagerOrderItemsStub(final Long... ids) {
        return Arrays.stream(ids).map(id -> new EagerOrderItem(id, "상품 " + id, (int) (id * 1000))).collect(Collectors.toList());
    }

    public static List<EagerOrderItem> generateEagerOrderItemsStub() {
        return generateEagerOrderItemsStub(1L, 2L, 3L);
    }

    public static List<LazyOrderItem> generateLazyItemsStub(final Long... ids) {
        return Arrays.stream(ids).map(id -> new LazyOrderItem(id, "상품 " + id, (int) (id * 1000))).collect(Collectors.toList());
    }

    public static List<LazyOrderItem> generateLazyItemsStub() {
        return generateLazyItemsStub(1L, 2L, 3L);
    }

}
