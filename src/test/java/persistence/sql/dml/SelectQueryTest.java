package persistence.sql.dml;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static persistence.sql.common.meta.MetaUtils.Columns을_생성함;
import static persistence.sql.common.meta.MetaUtils.JoinColumn을_생성함;
import static persistence.sql.common.meta.MetaUtils.TableName을_생성함;

import domain.NonExistentTablePerson;
import domain.NotEntityPerson;
import domain.Order;
import domain.Person;
import domain.SelectPerson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.EntityMeta;
import persistence.exception.InvalidEntityException;
import persistence.sql.common.meta.Columns;
import persistence.sql.common.meta.JoinColumn;
import persistence.sql.common.meta.TableName;

class SelectQueryTest {

    private static Query query;

    @BeforeAll
    static void beforeAll() {
        query = Query.getInstance();
    }

    @Test
    @DisplayName("@Entity 없는 클래스 select quert 생성시 오류")
    void notEntity() {
        //given
        final Class<NotEntityPerson> aClass = NotEntityPerson.class;
        final String methodName = "findAll";

        //when & then
        assertThrows(InvalidEntityException.class
            , () -> query.selectAll(methodName, TableName을_생성함(aClass), Columns을_생성함(aClass)));
    }

    @Test
    @DisplayName("@Table name이 없을 경우 클래스 이름으로 select query 생성")
    void nonTableName() {
        //given
        final Class<NonExistentTablePerson> aClass = NonExistentTablePerson.class;
        final String methodName = "findAll";

        final TableName tableName = TableName을_생성함(aClass);
        final Columns columns = Columns을_생성함(aClass);

        String wildcardPattern = "SELECT .*\\.id, .*\\.nick_name, .*\\.old, .*\\.email FROM NonExistentTablePerson .*";

        //when
        String q = query.selectAll(methodName, tableName, columns);

        //then
        assertThat(q.matches(wildcardPattern)).isTrue();
    }

    @Test
    @DisplayName("@OneToMany 즉시 조회 join문으로 select문 생성")
    void oneToMany() {
        //given
        final Class<Order> aClass = Order.class;
        final String methodName = "findById";

        final TableName tableName = TableName을_생성함(aClass);
        final Columns columns = Columns을_생성함(aClass);
        final JoinColumn joinColumn = JoinColumn을_생성함(aClass);

        String expectedQuery = "SELECT .*\\.id, .*\\.order_number, .*\\.id, .*\\.product, .*\\.quantity FROM orders .* JOIN order_items .* ON .*\\.id = .*\\.order_id WHERE .*\\.id = 1";

        final EntityMeta entityMeta = new EntityMeta(methodName, tableName, columns, joinColumn);

        //when
        String q = query.select(entityMeta, 1);

        //then
        assertThat(q.matches(expectedQuery)).isTrue();
    }

    @Test
    @DisplayName("전체 데이터 조회하는 select문 생성")
    void findAll() {
        //given
        final Class<Person> aClass = Person.class;

        final TableName tableName = TableName을_생성함(aClass);
        final Columns columns = Columns을_생성함(aClass);

        String wildcardPattern = "SELECT .*\\.id, .*\\.nick_name, .*\\.old, .*\\.email FROM users .*";

        //when
        String q = query.selectAll(new Object() {
        }.getClass().getEnclosingMethod().getName(), tableName, columns);

        //then
        assertThat(q.matches(wildcardPattern)).isTrue();
    }

    @Test
    @DisplayName("findById 쿼리를 성공적으로 생성")
    void findById() {
        //given
        Class<SelectPerson> clazz = SelectPerson.class;
        final TableName tableName = TableName을_생성함(clazz);
        final Columns columns = Columns을_생성함(clazz);
        final JoinColumn joinColumn = JoinColumn을_생성함(clazz);

        String wildcardPattern = "SELECT .*\\.select_person_id, .*\\.nick_name, .*\\.old, .*\\.email FROM selectPerson .* WHERE .*\\.select_person_id = 1";

        final EntityMeta entityMeta = new EntityMeta("findById", tableName, columns, joinColumn);

        //when
        String q = query.select(entityMeta, 1L);

        //then
        assertThat(q.matches(wildcardPattern)).isTrue();
    }
}
