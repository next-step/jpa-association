package persistence.sql.dml;

import domain.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.metadata.Column;
import persistence.sql.metadata.Columns;
import persistence.sql.metadata.EntityMetadata;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class DmlQueryBuilderTest {
	private final Person person = new Person(1L, "hhhhhwi", 1, "aab555586@gmail.com", 0);

	private final EntityMetadata entityMetadata = new EntityMetadata(person);

	private final DmlQueryBuilder queryBuilder = DmlQueryBuilder.build();

	@DisplayName("Person 객체로 PK 조건의 DELETE 쿼리 생성 테스트")
	@Test
	void test_deleteQuery() {
		assertEquals(
				queryBuilder.deleteQuery(entityMetadata),
				"DELETE FROM users WHERE id = 1;"
		);
	}

	@DisplayName("Person 객체로 INSERT 쿼리 생성 테스트")
	@Test
	void test_insertQuery() {
		assertEquals(
				queryBuilder.insertQuery(entityMetadata),
				"INSERT INTO users (nick_name, old, email) VALUES ('hhhhhwi',1,'aab555586@gmail.com');"
		);
	}

	@DisplayName("Person 객체로 PK 조건의 SELECT 쿼리 생성 테스트")
	@Test
	void test_buildFindAllQuery() {
		assertEquals(
				queryBuilder.selectQuery(Person.class, "1"),
				"SELECT * FROM users WHERE id = 1;"
		);
	}

	@DisplayName("Person 객체로 PK 조건의 UPDATE 쿼리 생성 테스트")
	@Test
	void test_buildByIdQuery() {
		Person updatePerson = new Person("update", 2, "update@email.com", 0);

		assertEquals(queryBuilder.updateQuery(
				new Columns(Arrays.stream(updatePerson.getClass().getDeclaredFields()).map(x -> {
					try {
						x.setAccessible(true);
						return new Column(x, String.valueOf(x.get(updatePerson)));
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				}).collect(Collectors.toList())), entityMetadata),
				"UPDATE users SET nick_name = 'update', old = 2, email = 'update@email.com' WHERE id = 1;");
	}
}
