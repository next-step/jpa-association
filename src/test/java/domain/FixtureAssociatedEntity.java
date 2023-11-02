package domain;

import jakarta.persistence.*;

import java.util.List;


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
    }

    @Entity
    @Table(name = "order_items")
    public static class OrderItem {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String product;

        private Integer quantity;
    }
}
