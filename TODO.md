# 개선할 만한 부분

- 생성자에 들어있는 로직들을 별도 메서드로 분리해내기(생성자는 생성만 하고 끝마치도록)
- EntityManager 의 역할이 너무 많다.
- 로직을 너무 객체 만들기 직전에 하려는 경향이 있는 것 같다
- 
# 구현 피드백

- PersistenceContext 의 상태값 중에 entitiesByKey, entitySnapshotsByKey 가 별도로 있는 이유 <- 필요할 때 도입
- merge/detach https://edu.nextstep.camp/s/JMAAwqKL/ls/99iS45z3

# 커밋 전에 확인할 것

* import 정리
* else 키워드를 줄여보기
* 테스트 코드는 충분한가?
* TODO 를 잘 마무리했는가?
* 생성자에 접근자를 잘 적용했나?
* 일관되게 작성했나? (비슷한 일을 하는 클래스가 여러 스타일로 작성됐는가?)
* 불필요하게 쪼개져있진 않은가?
* public/private modifier 기준 소트
* 객체가 할만한 일은 객체에게 맡기기(getter/setter 호출하는 대신 하위 객체에게 맡길 일이 있는지)
* 디미터 법칙을 잘 지켜보기

# 읽어볼 아티클

* https://jessyt.tistory.com/134
* https://reflectoring.io/do-not-use-checked-exceptions/
* https://www.baeldung.com/parameterized-tests-junit-5
* https://developerfarm.wordpress.com/2012/02/03/object_calisthenics_summary/
* 
