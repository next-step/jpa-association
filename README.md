# jpa-association


# 1단계



## 1. 요구사항 JOIN QUERY 만들기 --> 이거는 jpa로 query 일단 뽑아내기 join -> fetch join으로 나타내기
예시:

```sql
SELECT orders.id, orders.orderNumber, orderItems.id, orderItems.product, orderItems.quantity
FROM orders
JOIN orderItems on orders.id = orderItems.order_id
WHERE orders.id IN [1,2,3]
```

```sql
SELECT [<TABLE>.<COLUMNNAME> ...] -- COLUMN CLAUSE
FROM [<TABLE>] -- FROM
JOIN [<TABLE TO JOIN>] -- JOIN TABLE 
ON [<TABLE>.<COLUMN> = <TABLE TO JOIN>.<COLUMN FKEY>] -- JOIN ON CLAUSE
WHERE <TABLE>.<COLUMNNAME> IN [<VALUES>] -- WHERE CLAUSE
```

1. Column clause -> table 이름 앞에 붙이기
2. FROM -> table 이름
3. JOIN TABLE -> table to join 이름
4. JOIN ON CLAUSE ON Table의 id 와 TABLE TO JOIN 의 fk
5. WHERE CLAUSE  orders.id IN

인수 (table, list<column> columns, whereclause)
인수 2(tabletojoin, list<column> columns, fkey)


구현 요소
1. JoinQueryBuilder -> JoinQuery(불변) 생성하기
    - Builder 객체들이 각각 select절을 만들고 where 절을 만들게 되는데 Clause 생성객체가 분리되어야한다.
    - 하지만 수정범위가 크니깐 JoinQuery를 미래에 리팩토링이 편하게 만들어 두자.
2. 아쉬운점
   추상화 요소들 (SELECT CLAUSE, WHERE CLAUSE, JOIN CLAUSE) 들에 대해서 너무 늦게 파악했다.

검증 요소
1. JOIN QUERY 생성 검증

## 2. Join Query 만들어서 Entity 화하기

select 이후에 eager이면 join fetch 진행 아니면 lazy

1. Loader에서 가져오는 로직 분기 필요 -> isRelation 이면 subEntity 필요해서 fetch join --> 얘도 분리가 필요하다고 강의에서 들은것같은데
    - 여기도 추상화가 필요하다
    - CollectionElementLoader(column, entity) -> 피드백 참고
        - rowmapper 따로 분기
        - eager
        - lazy
        - join 문 2번이면 recursive인가?

2. EntityClass에 IsRelation을 만들거나 RelationalEntity를 만들어야되나 생각중이다.
    - Relation interface 로 emptyClass(null pattern) 와 RelationColumn(fetch type, collection type) 구현 필요
    - loader에서 사용해야 될 relation, joincolumn 정보 필요
    - IsRelation로 만들자
    - mapping은 조금더 봐야할듯
3. Rowmapper를 사용할때 여기서도 isRelation 값이 따로 필요한데 흠... oneToManyRowMapper를 따로 해야할지 생각필요하다
    - 이거 가져오는 로직이 있어서 다행이다. -> 피드백 참고
    - 얘도 따로 분기되야할듯 -> collection type 가져오는 부분이 단일 rowmapper 와 다르다.
      검증 요소

1. Loader에서 CollectionEntity 로드 확인
2. EntityClass에서 Relationship 관련 컬럼확인
3. RowMapper에서 Collection Entity 만드는것을 확인
~
0 단계 리뷰사항

- 테스트 멱등성을 위해 테스트마다 다른 pk 삽입
- create table시에 있다면 생성하지 않는 방향으로 table 생성
- 너무 작은 변경에도 쉽게 허물어지는데 mapping 로직 확인 필요하다.


-----------------------------------------------------------------------------------------------------------------
1,2 단계 리뷰사항

1. CollectionRowMapper에서 elementEntity와 MetaEntity 추출 시에 T, V 를 사용할 필요없이 뽑아내도된다.
2. JoinQueryBuilder에서 select 시에 table.field 로 깔끔하게 매개변수 받아서 넘길수있다.
3. 사용 하지 않는 변수 제거
4. nullObject returns null보다는 throw exception
5. 이런... orElse, orElseGet 확인해서 제거하기
   - else는 무조건 부르고 get은
6. stream 사용내에 메서드 레퍼런스로 수정
7. 내 스스로의 리뷰사항 -> 메서드 분리로 코드내에 굉장히 긴 METHOD들을 정리
   - 맵핑로직, 쿼리빌더 로직 전반적 개선이 필요해보이긴하다.
   - 새로 추가된 객체들 유닛 테스트 안되고있음

3단계 구현사항
1. Lazy로 변경
2. 프록시 생성되는지 검증
3. query 로드시에 나가는지 검증
4. Entity 객체 검증하기


-------------------------------------------------
1. 리뷰 적용 우선

2. order lazy 변경
3. loader 에서 프록시 생성해서 넣어주기
4. 테스트만 작성해주면 완성

orderItems 에 ORDER 자체가 들어오는 구조다.
ElementRowMapper는 OrderItems만 들고오면 되지 Order을 들고 올 필요가없다.

elementRowMapper list 반환하고 -> Loader에서 묶어주자.



1. 포맷터 적용 + 코드 리팩토링을 후순위
2. 냄새가 나는 부분들을 추후에 잡아서 개선하자




질문들
----------
1. 검증사항들은 어떻게 보강시킬수있을까요?
   - lazy load 상태값 이전과 후 비교하거나 mockito로 해당 method invoke 됐는지 확인해야할까요?
   - 테스트 하는데 조언이 필요합니다!!!
