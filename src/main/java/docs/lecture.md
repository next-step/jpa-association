## 3/16

### 구현
```markdown
- Query
- Entity
-> 이렇게 2개의 패키지로 나누고, 1주차 미션과 약간 별개일 수 있다.

- SessionImpl 에 의해 session 을 계속 넣어서 다닌다.
    - `return new StatefulPersistenceContext(this);`
- StatefulPersistenceContext
    - `entitiesByKey`
    - `collectionsByKey`
- AnnotationBinder 참고

- InFlightMetadataCollector
    - metadata 수집
    - 캐싱 설정(default = true)

```

### logging
```markdown
- logging.level.org.hibernate.boot=trace
- logging.level.org.hibernate=trace => 직접 api 실행해서 로깅 확인
- Spring 은 로깅해서 확인해보는 것을 추천 => Spring 공부!
```

