package domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class FixtureAssociatedEntity {

    @Entity
    public static class WithId {
        @Id
        private Long id;
    }


    @Entity
    public static class WithOneToMany {
        @Id
        private Long id;

        @OneToMany
        List<WithId> withIds;
    }

    @Entity
    public static class WithOneToManyFetchTypeEAGER {
        @Id
        private Long id;

        @OneToMany(fetch = FetchType.EAGER)
        List<WithId> withIds;
    }

    @Entity
    public static class WithOneToManyJoinColumn {
        @Id
        private Long id;

        @OneToMany
        @JoinColumn(name = "join_pk")
        List<WithId> withIds;
    }

    @Entity
    public static class WithOneToManyInsertableFalse {
        @Id
        private Long id;

        @OneToMany
        @JoinColumn(insertable = false)
        List<WithId> withIds;
    }
    @Entity
    public static class WithOneToManyNullableFalse {
        @Id
        private Long id;

        @OneToMany
        @JoinColumn(nullable = false)
        List<WithId> withIds;
    }


    @Entity
    @Table(name = "orders")
    public static class Order {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String orderNumber;

        @OneToMany(fetch = FetchType.EAGER)
        @JoinColumn(name = "order_id")
        private List<OrderItem> orderItems;

        protected Order() {
        }

        public Order(final Long id, final String orderNumber) {
            this.id = id;
            this.orderNumber = orderNumber;
        }

        public Order(final String orderNumber) {
            this(null, orderNumber);
        }

        public Long getId() {
            return id;
        }

        public String getOrderNumber() {
            return orderNumber;
        }

        public List<OrderItem> getOrderItems() {
            return orderItems;
        }

        public void addOrderItem(final OrderItem orderItem) {
            if(Objects.isNull(this.orderItems)) {
                this.orderItems = new ArrayList<>();
            }
            this.orderItems.add(orderItem);
        }
    }

    @Entity
    @Table(name = "order_items")
    public static class OrderItem {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String product;

        private Integer quantity;

        public OrderItem() {
        }

        public OrderItem(final Long id, final String product, final Integer quantity) {
            this.id = id;
            this.product = product;
            this.quantity = quantity;
        }

        public OrderItem(final String product, final Integer quantity) {
            this(null, product, quantity);
        }

        public Long getId() {
            return id;
        }

        public String getProduct() {
            return product;
        }

        public Integer getQuantity() {
            return quantity;
        }

    }

    @Entity
    @Table(name = "lazy_orders")
    public static class OrderLazy {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String orderNumber;

        @OneToMany(fetch = FetchType.LAZY)
        @JoinColumn(name = "lazy_order_id")
        private List<OrderLazyItem> orderItems;

        protected OrderLazy() {
        }

        public OrderLazy(final Long id, final String orderNumber) {
            this.id = id;
            this.orderNumber = orderNumber;
        }

        public OrderLazy(final String orderNumber) {
            this(null, orderNumber);
        }

        public Long getId() {
            return id;
        }

        public String getOrderNumber() {
            return orderNumber;
        }

        public List<OrderLazyItem> getOrderItems() {
            return orderItems;
        }

        public void addOrderItem(final OrderLazyItem orderItem) {
            if(Objects.isNull(this.orderItems)) {
                this.orderItems = new ArrayList<>();
            }
            this.orderItems.add(orderItem);
        }
    }

    @Entity
    @Table(name = "lazy_order_items")
    public static class OrderLazyItem {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String product;

        private Integer quantity;

        public OrderLazyItem() {
        }

        public OrderLazyItem(final Long id, final String product, final Integer quantity) {
            this.id = id;
            this.product = product;
            this.quantity = quantity;
        }

        public OrderLazyItem(final String product, final Integer quantity) {
            this(null, product, quantity);
        }

        public Long getId() {
            return id;
        }

        public String getProduct() {
            return product;
        }

        public Integer getQuantity() {
            return quantity;
        }

    }

}
