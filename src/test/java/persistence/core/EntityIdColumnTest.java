package persistence.core;


import domain.FixtureEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class EntityIdColumnTest {

    private Class<?> mockClass;

    @Test
    @DisplayName("Id 컬럼 정보로 EntityColumn 인스턴스를 생성 할 수 있다.")
    void testEntityColumnWithId() throws Exception {
        mockClass = FixtureEntity.WithId.class;
        final Field field = mockClass.getDeclaredField("id");
        final EntityColumn column = new EntityIdColumn(field, "WithId");
        assertResult(column, "id", "id", false, Long.class);
    }

    @Test
    @DisplayName("Id 컬럼인데 Column 설정으로 이름을 설정해 EntityColumn 인스턴스를 생성 할 수 있다.")
    void testEntityColumnWithIdAndColumn() throws Exception {
        mockClass = FixtureEntity.WithIdAndColumn.class;
        final Field field = mockClass.getDeclaredField("id");
        final EntityColumn column = new EntityIdColumn(field, "WithIdAndColumn");
        assertResult(column, "test_id", "id", false, Long.class);
    }

    @Test
    @DisplayName("Id 컬럼의 GeneratedValue 를 이용하면 autoIncrement 값이 true 인 EntityColumn 인스턴스를 생성 할 수있다.")
    void testEntityColumnWithIdGeneratedValue() throws Exception {
        mockClass = FixtureEntity.IdWithGeneratedValue.class;
        final Field field = mockClass.getDeclaredField("id");
        final EntityColumn column = new EntityIdColumn(field, "IdWithGeneratedValue");
        assertResult(column, "id", "id", true, Long.class);
    }

    @Test
    @DisplayName("Id 컬럼에는 @Cloumn 의 insertable 이 작동하지 않으며 항상 false 를 리턴한다.")
    void testEntityColumnWithIdNonInsertableNotWorking() throws Exception {
        mockClass = FixtureEntity.WithIdInsertable.class;
        final Field field = mockClass.getDeclaredField("id");
        final EntityColumn column = new EntityIdColumn(field, "WithIdInsertable");
        assertResult(column, "id", "id", false, Long.class);
    }

    private void assertResult(final EntityColumn result,
                              final String columnName,
                              final String fieldName,
                              final boolean isAutoIncrement,
                              final Class<?> type) {
        assertSoftly(softly -> {
            softly.assertThat(result.getName()).isEqualTo(columnName);
            softly.assertThat(result.getFieldName()).isEqualTo(fieldName);
            softly.assertThat(result.isId()).isEqualTo(true);
            softly.assertThat(result.isNotNull()).isEqualTo(true);
            softly.assertThat(result.isAutoIncrement()).isEqualTo(isAutoIncrement);
            softly.assertThat(result.isInsertable()).isEqualTo(false);
            softly.assertThat(result.getType()).isEqualTo(type);
        });

    }

}
