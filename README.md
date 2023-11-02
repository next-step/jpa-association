# jpa-association

## 1단계 - OneToMany (FetchType.EAGER)
### 요구사항 1 - Join Query 만들기
- 기존 SelectQueryBuilder에서 map으로 추가 정보들을 받을 수 있다.
```sql
select orders.id, orders.orderNumber, orderItem.id, orderItem.produce, orderItem.quantity
from orders
join orderItem on orders.id = orderItem.id
where orders.id = ?;
```
