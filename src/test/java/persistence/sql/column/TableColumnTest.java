package persistence.sql.column;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import domain.Person;

import static org.assertj.core.api.Assertions.assertThat;

class TableColumnTest {


    @DisplayName("Table에 이름이 있으면 해당 정보를 테이블의 이름으로 반환한다.")
    @Test
    void getName() {
        TableColumn tableColumn = new TableColumn(Person.class);

        assertThat(tableColumn.getName()).isEqualTo("users");
    }

    @DisplayName("Table에 이름이 없으면 클래스 이름을 테이블의 이름으로 반환한다.")
    @Test
    void getNameWhenTableAnnotationIsNotPresent() {
        TableColumn tableColumn = new TableColumn(Person2.class);

        assertThat(tableColumn.getName()).isEqualTo("person2");
    }

    @Entity
    private static class Person2 {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
    }

}
