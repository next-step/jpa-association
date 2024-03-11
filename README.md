# jpa-association

# 🚀 1단계 - OneToMany (FetchType.EAGER)
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

## 요구사항 2 - Join Query 를 만들어 Entity 화 해보기

- [x] Join query로 Entity를 매핑할 수 있다.

# 🚀 2단계 - LazyLoading by Proxy

## 요구사항 1 - CGLib Proxy 적용

- [x] cglib 의존성 추가
- [x] 인터페이스가 없는 클래스의 메서드 수정
- [x] 대문자로 출력될 수 있도록 구현

## 요구사항 2 - 조회 시 프록시 객체를 사용해 적용해보자

### CGLib Callback의 종류

1. MethodInterceptor
   메서드 호출 전 후에 interceptor 역할을 할 수 있다.
2. NoOp
   아무 작업도 수행하지 않고 곧바로 원본 객체를 호출한다.
3. FixedValue
   고정된 값을 반환한다.
4. Dispatcher
   다른 타입의 클래스를 반환한다.
5. LazyLoader
   호출하는 시점에 클래스를 생성한다.

