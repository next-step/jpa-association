# jpa-association

## 요구사항 1 - Join Query 만들기
```sql
SELECT 
  orders.id, 
  orders.orderNumber, 
  order_items.id, 
  order_items.product, 
  order_items.quantity 
FROM 
  orders 
LEFT JOIN 
  order_items 
ON 
  orders.id = order_items.order_id
WHERE 
  orders.id = :orderId
```
- [x] Join Query를 만들 수 있다.
