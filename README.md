# jpa-association

### step 1 - OneToMany (FetchType.EAGER)

- [x] 요구사항 1 - Join Query 만들기
  - ```angular2html
    select orders.id, orders.order_number, order_items.id, order_items.produce, order_items.quantity
    from orders
    join orderItem on orders.id = order_items.order_id
    where orders.id = ?;
    ```
- [x] 요구사항 2 - Join Query 를 만들어 Entity 화 해보기
