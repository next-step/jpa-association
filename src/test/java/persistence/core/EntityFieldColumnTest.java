package persistence.core;


import domain.FixtureEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class EntityFieldColumnTest {

    private Class<?> mockClass;

    @Test
    @DisplayName("일반 필드를 이용해 EntityColumn 인스턴스를 생성 할 수 있다.")
    void testEntityColumnWithoutColumn() throws Exception {
        mockClass = FixtureEntity.WithoutColumn.class;
        final Field field = mockClass.getDeclaredField("column");
        final EntityColumn column = new EntityFieldColumn(field, "WithoutColumn");
        assertResult(column, "WithoutColumn", "column", "column", false, true, String.class);
    }

    @Test
    @DisplayName("일반 필드에 @Cloumn 을 이용해 이름 설정해 EntityColumn 인스턴스를 생성 할 수 있다.")
    void testEntityColumnWithColumn() throws Exception {
        mockClass = FixtureEntity.WithColumn.class;
        final Field field = mockClass.getDeclaredField("column");
        final EntityColumn column = new EntityFieldColumn(field, "WithColumn");
        assertResult(column, "WithColumn", "test_column", "column", false, true, String.class);
    }

    @Test
    @DisplayName("일반 필드에 @Cloumn 을 이용해 NotNull 이 true 인 EntityColumn 인스턴스를 생성 할 수 있다.")
    void testEntityColumnWithColumnNonNull() throws Exception {
        mockClass = FixtureEntity.WithColumn.class;
        final Field field = mockClass.getDeclaredField("notNullColumn");
        final EntityColumn column = new EntityFieldColumn(field, "WithColumn");
        assertResult(column, "WithColumn", "notNullColumn", "notNullColumn", true, true, String.class);
    }

    @Test
    @DisplayName("일반 필드에 @Cloumn 을 이용해 insertable 이 false 인 EntityColumn 인스턴스를 생성 할 수 있다.")
    void testEntityColumnWithColumnNonInsertable() throws Exception {
        mockClass = FixtureEntity.WithColumnNonInsertable.class;
        final Field field = mockClass.getDeclaredField("notInsertableColumn");
        final EntityColumn column = new EntityFieldColumn(field, "WithColumnNonInsertable");
        assertResult(column, "WithColumnNonInsertable", "notInsertableColumn", "notInsertableColumn", false, false, String.class);
    }

    private void assertResult(final EntityColumn result,
                              final String tableName,
                              final String columnName,
                              final String fieldName,
                              final boolean isNotNull,
                              final boolean isInsertable,
                              final Class<?> type) {
        assertSoftly(softly -> {
            softly.assertThat(result.getTableName()).isEqualTo(tableName);
            softly.assertThat(result.getName()).isEqualTo(columnName);
            softly.assertThat(result.getFieldName()).isEqualTo(fieldName);
            softly.assertThat(result.isId()).isEqualTo(false);
            softly.assertThat(result.isNotNull()).isEqualTo(isNotNull);
            softly.assertThat(result.isAutoIncrement()).isEqualTo(false);
            softly.assertThat(result.isInsertable()).isEqualTo(isInsertable);
            softly.assertThat(result.getType()).isEqualTo(type);
        });

    }

}
