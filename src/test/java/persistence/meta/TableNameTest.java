package persistence.meta;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.Table;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.testFixtures.Person;

@DisplayName("테이블 이름 테스트")
class TableNameTest {

    @Test
    @DisplayName("테이블 이름 생성 테스트")
    void create() {
        final TableName tableName = TableName.from(Person.class);

        assertThat(tableName.getValue()).isEqualTo("users");
    }

    @Test
    @DisplayName("테이블 어노테이션이 없으면 클래스 이름으로 테이블 이름이 생성된다")
    void noNameTable() {
        class Person {
        }

        final TableName tableName = TableName.from(Person.class);


        assertThat(tableName.getValue()).isEqualTo("Person");
    }

    @Test
    @DisplayName("테이블 어노테이션에 이름이 없으면 테이블 이름이 생성된다.")
    void emptyTableName() {
        @Table(name = "")
        class Person {
        }

        final TableName tableName = TableName.from(Person.class);


        assertThat(tableName.getValue()).isEqualTo("Person");
    }

}
