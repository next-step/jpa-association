package proxy;

import builder.dml.EntityData;
import entity.OrderItem;
import entity.OrderLazy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LazyProxyBuilderTest {

    @DisplayName("프록시를 생성한다.")
    @Test
    void createProxyTest() {
        OrderLazy order = new OrderLazy(1L, "1234", List.of(createOrderItem(1, 1L)));
        EntityData entityData = EntityData.createEntityData(order);

        List<?> list = new LazyProxyBuilder<>().createProxy(entityData.getJoinEntity().getJoinEntityData().getFirst());

        assertTrue(Proxy.isProxyClass(list.getClass()), "생성된 객체는 프록시 객체여야 합니다.");
    }

    private OrderItem createOrderItem(int i, Long orderId) {
        return new OrderItem((long) i, orderId, "테스트상품"+i, 1);
    }
}
