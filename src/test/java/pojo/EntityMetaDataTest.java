package pojo;

import entity.Person2;
import entity.Person3;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EntityMetaDataTest {

    @DisplayName("Table 어노테이션이 없거나 있어도 name 필드가 없을 경우 class 의 simpleName 을 반환한다.")
    @Test
    void entityNameInfo_ShouldReturnSimpleName() {
        EntityMetaData entityMetaData = new EntityMetaData(Person2.class);
        assertThat(entityMetaData.getEntityName()).isEqualTo("person2");
    }

    @DisplayName("Table 어노테이션이 있고 name 필드가 있을 경우 name 을 반환한다.")
    @Test
    void entityNameInfo_ShouldReturnTableName() {
        EntityMetaData entityMetaData = new EntityMetaData(Person3.class);
        assertThat(entityMetaData.getEntityName()).isEqualTo("users");
    }
}
