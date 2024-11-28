# jpa-association
# 1단계 - OneToMany (FetchType.EAGER)
- 미션의 목표
  - `연관 관계` 에 대한 신규 요구사항을 1단계 Query Builder 에 추가
  - 클린 코드를 유지 하면서 확장 가능 하도록 재구조화
- 요구 사항 1 - Select Join Query 만들기 (EAGER)
  - Sql 쿼리 문을 수정해 보자
- 요구 사항 2 - Join Query 를 만들어 Entity 화 해보기
  - FetchType.EAGER 인 경우
- 요구 사항 3 - Save 시 Insert Query
  - 연관 관계에 대한 데이터 저장 시 쿼리 만들어 보기
  - 부모 데이터가 있는 경우, 부모 데이터가 없는 경우 나누어서 구현

### 구현 기능 목록
- [ ] ~~연관관계에 따른 create query~~
  - ~~연관 관계 종류에 따라 foreign key 컬럼에 대한 정보가 부모에 있을 수도 있고, 자식에 있을 수도 있다.~~
  - ~~엔티티 패키지를 스캔하여 모든 연관 관계 정보를 추출하고, 이 정보를 바탕으로 foreign key 에 대한 query string 을 만든다.~~
- [ ] 연관관계가 있는 엔티티 조회를 위한 select join query
- [ ] 데이터베이스로부터 조회한 연관관계가 있는 엔티티를 인스턴스로 매핑

# 2단계 - Proxy
- 미션의 목표
  - 프록시 사용의 장점 (성능 최적화 · 리소스 절약) 관점에서 프록시를 사용하여 지연 로딩을 구현 해보기
- 요구 사항 1  - Dynamic Proxy 연습
  - 주어진 테스트 코드에 맞는 프록시 객체 만들기
- 요구 사항 2 - Proxy 활용
  - InvocationHandler 를 활용해 LazyLoading 구현

### 구현 기능 목록
- [ ] 소문자를 대문자로 캐스팅하는 Proxy 구현
- [ ] Proxy 를 활용하여 LazyLoadingHandler 구현

# 3단계 - OneToMany (FetchType.LAZY)
- 미션의 목표
  - 프록시를 사용하여 지연 로딩 구현하기
  - FetchType 에 따라 어떻게 분기 처리를 해야 할지 고민하기 -> Proxy 를 활용하는 경우와 아닌 경우
- 요구 사항 1 - 로딩 전략에 따른 분기 (FetchType.LAZY)
- 요구 사항 2 - Proxy 를 활용한 지연 로딩 구현

### 구현 기능 목록
- [ ] FetchType.EAGER & FetchType.LAZY 모두 처리 가능하도록 구현
- [ ] Proxy 를 활용해서 FetchType.LAZY 처리 구현
