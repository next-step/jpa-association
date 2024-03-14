# jpa-association

## step1

### 요구사항 1 - Join Query 만들기

- `Order`와 `OrderItem`이 One-to-Many 단방향 관계일 때 JoinQuery를 만든다.
- 조인하는 엔티티와 컬럼에 대한 정보가 필요하므로 `Table` 객체에서 메타 데이터를 분석할 때 `JoinColumn` 이라는 객체를 추가로 가지도록 한다.
- `SelectQueryBuilder`에서 `JoinColumn` 객체를 이용해 조인 쿼리를 생성할 수 있도록 만든다.

## step2

### 요구사항 1 - CGLib Proxy 적용하기

- `build.gradle`에 `implementation 'cglib:cglib:3.3.0'`을 추가한다.
- `cglib`를 이용해 `HelloProxy`를 만들어 `HelloTarget`의 리턴값을 대문자로 반환하도록 만든다.
