package persistence.core;

import domain.FixtureAssociatedEntity;
import domain.FixtureEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.exception.ColumnNotExistException;

import static org.assertj.core.api.Assertions.*;

class EntityColumnsTest {

    private Class<?> mockClass;
    private String tableName;

    @BeforeEach
    void setUp() {
        tableName = "WithId";
    }

    @Test
    @DisplayName("Id 컬럼 정보가 있는 클래스를 이용해 EntityColumns 인스턴스를 생성할 수 있다.")
    void entityColumnsCreateTest() {
        mockClass = FixtureEntity.WithId.class;
        final EntityColumns columns = new EntityColumns(mockClass, tableName);
        assertThat(columns).isNotNull();
    }

    @Test
    @DisplayName("Id 컬럼 정보가 존재하지 않으면 EntityColumns 인스턴스 생성에 실패해야한다.")
    void entityColumnsCreateFailureTest() {
        mockClass = FixtureEntity.WithoutId.class;
        assertThatThrownBy(() -> new EntityColumns(mockClass, tableName))
                .isInstanceOf(ColumnNotExistException.class);
    }

    @Test
    @DisplayName("EntityColumns.getId 메서드를 통해 Id 컬럼을 가져올 수 있어야 한다.")
    void entityColumnsGetIdTest() throws Exception {
        mockClass = FixtureEntity.WithId.class;
        final EntityColumns columns = new EntityColumns(mockClass, tableName);
        final EntityColumn idColumn = new EntityIdColumn(mockClass.getDeclaredField("id"), tableName);
        assertThat(columns.getId()).isEqualTo(idColumn);
    }

    @Test
    @DisplayName("@Transient 를 가진 필드는 EntityColumns 에 포함되지 않는다.")
    void entityColumnsWithTransientNotExistTest() throws Exception {
        mockClass = FixtureEntity.WithTransient.class;
        final EntityColumns columns = new EntityColumns(mockClass, tableName);
        final EntityColumn idColumn = new EntityFieldColumn(mockClass.getDeclaredField("column"), tableName);
        assertThatIterable(columns).doesNotContain(idColumn);
    }

    @Test
    @DisplayName("EntityColumns.getNames 를 통해 컬럼들의 이름들을 조회할 수 있다.")
    void entityColumnsGetNamesTest() {
        mockClass = FixtureEntity.WithColumn.class;
        final EntityColumns columns = new EntityColumns(mockClass, tableName);

        assertThatIterable(columns.getNames()).containsExactly("id", "test_column", "notNullColumn");
    }

    @Test
    @DisplayName("EntityColumns.getFieldNames 를 통해 컬럼들의 필드 이름들을 조회할 수 있다.")
    void entityColumnsGetFieldNamesTest() {
        mockClass = FixtureEntity.WithColumn.class;
        final EntityColumns columns = new EntityColumns(mockClass, tableName);

        assertThatIterable(columns.getFieldNames()).containsExactly("id", "column", "notNullColumn");
    }

    @Test
    @DisplayName("EntityColumns.getOneToManyColumns 를 통해 OneToMany 컬럼들을 조회할 수 있다.")
    void entityColumnsGetOneToManyColumnsTest() throws Exception {
        mockClass = FixtureAssociatedEntity.WithOneToManyJoinColumn.class;

        final EntityColumns columns = new EntityColumns(mockClass, tableName);
        final EntityOneToManyColumn oneToManyColumn = new EntityOneToManyColumn(mockClass.getDeclaredField("withIds"), tableName);

        assertThatIterable(columns.getOneToManyColumns()).containsExactly(oneToManyColumn);
    }

    @Test
    @DisplayName("EntityColumns.getFieldColumns 를 통해 일반 컬럼들을 조회할 수 있다.")
    void entityColumnsGetFieldColumnsTest() throws Exception {
        mockClass = FixtureAssociatedEntity.OrderItem.class;

        final EntityColumns columns = new EntityColumns(mockClass, tableName);
        final EntityFieldColumn productColumn = new EntityFieldColumn(mockClass.getDeclaredField("product"), tableName);
        final EntityFieldColumn quantityColumn = new EntityFieldColumn(mockClass.getDeclaredField("quantity"), tableName);

        assertThatIterable(columns.getFieldColumns()).containsExactly(productColumn, quantityColumn);
    }
}
