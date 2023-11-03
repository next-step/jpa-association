package persistence.context;

import fixtures.EntityFixtures;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.entity.attribute.EntityAttribute;

import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Nested
@DisplayName("FirstCaches 클래스의")
class FirstCachesTest {
    @Nested
    @DisplayName("getFirstCache 메소드는")
    class getFirstCache {
        @Nested
        @DisplayName("일차캐시에 존재하는 클래스타입과 아이디를 받으면")
        class withValidArgs {
            @Test
            @DisplayName("일차캐시를 반환한다.")
            void returnFirstCache() {
                FirstCaches firstCaches = new FirstCaches();

                EntityFixtures.OrderItem orderItem = new EntityFixtures.OrderItem(1L, "티비", 1);
                EntityFixtures.Order order
                        = new EntityFixtures.Order(1L, "민준", List.of(orderItem));

                firstCaches.putFirstCache(order, "1");

                Object firstCache = firstCaches.getFirstCacheOrNull(EntityFixtures.Order.class, "1");

                assertThat(firstCache.toString())
                        .isEqualTo("Order{id=1, orderNumber='민준', orderItems=[OrderItem{id=1, product='티비', quantity=1}]}");
            }
        }

        @Nested
        @DisplayName("일차캐시에 존재하지않는 클래스타입과 아이디를 받으면")
        class withNotValidArgs {
            @Test
            @DisplayName("null을 반환한다.")
            void returnNull() {
                FirstCaches firstCaches = new FirstCaches();

                Object firstCache = firstCaches.getFirstCacheOrNull(EntityFixtures.SampleOneWithValidAnnotation.class, "1");

                assertThat(firstCache).isNull();
            }
        }
    }

    @Nested
    @DisplayName("putFirstCache 메소드는")
    class putFirstCache {
        @Nested
        @DisplayName("인스턴스와 이이디를 받으면")
        class withValidArgs {
            @Test
            @DisplayName("일차캐시에 인스턴스를 저장한다.")
            void putFirstCache() {
                FirstCaches firstCaches = new FirstCaches();
                EntityFixtures.SampleOneWithValidAnnotation sample
                        = new EntityFixtures.SampleOneWithValidAnnotation(1L, "민준", 29);

                Assertions.assertDoesNotThrow(() -> firstCaches.putFirstCache(sample, "1"));
            }
        }

        @Nested
        @DisplayName("OneToMany 연관관계가 있는 인스턴스와 이이디를 받으면")
        class withValidInstanceAndIdAndEntityAttribute {
            @Test
            @DisplayName("일차캐시에 인스턴스를 저장하고, 연관된 인스턴스까지 같이 저장한다.")
            void putFirstCache() {
                FirstCaches firstCaches = new FirstCaches();
                EntityFixtures.OrderItem orderItem = new EntityFixtures.OrderItem(1L, "티비", 1);
                EntityFixtures.Order order
                        = new EntityFixtures.Order(1L, "민준", List.of(orderItem));
                EntityAttribute entityAttribute = EntityAttribute.of(EntityFixtures.Order.class, new HashSet<>());

                Assertions.assertDoesNotThrow(() -> firstCaches.putFirstCache(order, "1", entityAttribute));
            }
        }
    }

    @Nested
    @DisplayName("remove 메소드는")
    class remove {
        @Nested
        @DisplayName("인스턴스와 이이디를 받으면")
        class withValidArgs {
            @Test
            @DisplayName("엔트리 상태를 REMOVED로 변경한다.")
            void putFirstCache() {
                FirstCaches firstCaches = new FirstCaches();
                EntityFixtures.SampleOneWithValidAnnotation sample
                        = new EntityFixtures.SampleOneWithValidAnnotation(1L, "민준", 29);

                firstCaches.putFirstCache(sample, "1");

                Assertions.assertDoesNotThrow(() ->
                        firstCaches.remove(EntityFixtures.SampleOneWithValidAnnotation.class, "1"));
            }
        }
    }
}
