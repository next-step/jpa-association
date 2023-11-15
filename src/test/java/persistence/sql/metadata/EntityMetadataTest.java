package persistence.sql.metadata;

import domain.Person;
import domain.TestDomain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EntityMetadataTest {
    @DisplayName("Person 객체의 테이블 명을 가져온다.")
    @Test
    void test_getTableName() {
        //Given
        EntityMetadata entityMetadata = new EntityMetadata(Person.class);

        //When & Then
        assertEquals(entityMetadata.getTableName(), "users");
    }

    @DisplayName("Entity 클래스가 아닐 경우 Exception이 발생한다.")
    @Test
    void When_IsNotEntityClass_Then_ThrowException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new EntityMetadata(TestDomain.class)
        );
    }

}
