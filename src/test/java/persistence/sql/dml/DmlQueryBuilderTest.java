package persistence.sql.dml;

import domain.Order;
import domain.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.metadata.EntityMetadata;
import persistence.sql.metadata.Values;

import static org.junit.jupiter.api.Assertions.*;

class DmlQueryBuilderTest {
	private final Person person = new Person(1L, "hhhhhwi", 1, "aab555586@gmail.com", 0);

	private final EntityMetadata entityMetadata = new EntityMetadata(Person.class);

	private final DmlQueryBuilder queryBuilder = DmlQueryBuilder.build();

	@DisplayName("Person 객체로 PK 조건의 DELETE 쿼리 생성 테스트")
	@Test
	void test_deleteQuery() {
		assertEquals(
				queryBuilder.deleteQuery(entityMetadata, 1L),
				"DELETE FROM users WHERE users.id = 1;"
		);
	}

	@DisplayName("Person 객체로 INSERT 쿼리 생성 테스트")
	@Test
	void test_insertQuery() {
		assertEquals(
				queryBuilder.insertQuery(entityMetadata, Values.of(person.getClass().getDeclaredFields(), person)),
				"INSERT INTO users (nick_name, old, email) VALUES ('hhhhhwi',1,'aab555586@gmail.com');"
		);
	}

	@DisplayName("Person 객체로 PK 조건의 SELECT 쿼리 생성 테스트")
	@Test
	void test_selectQuery() {
		assertEquals(
				queryBuilder.selectQuery(Person.class, 1),
				"SELECT * FROM users WHERE users.id = 1;"
		);
	}

	@DisplayName("Order 객체로 JOIN 절을 포함한 SELECT 쿼리 생성 테스트")
	@Test
	void test_selectQueryWithJoinQuery() {
		assertEquals(
				queryBuilder.selectQuery(Order.class, 1),
				"SELECT * FROM orders JOIN order_items ON order_items.order_id = orders.id WHERE orders.id = 1;"
		);
	}

	@DisplayName("Person 객체로 PK 조건의 UPDATE 쿼리 생성 테스트")
	@Test
	void test_updateQuery() {
		Person updatePerson = new Person("update", 2, "update@email.com", 0);

		assertEquals(queryBuilder.updateQuery(entityMetadata, Values.from(updatePerson), 1),
				"UPDATE users SET nick_name = 'update', old = 2, email = 'update@email.com' WHERE users.id = 1;");
	}
}
