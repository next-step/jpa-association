# jpa-association

## 요구사항
### 1단계 - OneToMany (FetchType.EAGER)
- [X] Select Join 쿼리를 생성한다.
- [X] Entity를 조회한다.
- [X] OneToMany일 시 관계 맺어 있는 객체도 Save한다.

### 2단계 - Proxy
- [X] 소문자가 대문자로 변환되는지 확인
- [X] 혼합된 대소문자가 모두 대문자로 변환되는지 확인
- [X] 빈 문자열이 그대로 반환되는지 확인
- [X] 이미 대문자인 문자열이 그대로 반환되는지 확인
- [X] Lazy인 List는 프록시로 생성한다.
- [X] Proxy인 객체에 접근 시 조회한다.
