package persistence.sql.dml;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static persistence.sql.common.meta.MetaUtils.Columns을_생성함;
import static persistence.sql.common.meta.MetaUtils.JoinColumn을_생성함;
import static persistence.sql.common.meta.MetaUtils.TableName을_생성함;
import static persistence.sql.common.meta.MetaUtils.Values을_생성함;

import database.DatabaseServer;
import database.H2;
import java.sql.SQLException;
import java.util.List;
import jdbc.JdbcTemplate;
import jdbc.ResultMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.entity.EntityMeta;
import persistence.exception.InvalidEntityException;
import domain.InsertPerson;
import domain.NotEntityPerson;
import domain.SelectPerson;
import persistence.sql.common.instance.Values;
import persistence.sql.common.meta.Columns;
import persistence.sql.common.meta.JoinColumn;
import persistence.sql.common.meta.TableName;
import persistence.sql.ddl.DmlQuery;

class QueryDmlTest {

    private static DatabaseServer server;
    private static JdbcTemplate jdbcTemplate;

    private static Query query;
    private static DmlQuery dmlQuery;

    @BeforeAll
    static void start() throws SQLException {
        server = new H2();
        server.start();

        query = Query.getInstance();
        dmlQuery = DmlQuery.getInstance();

        jdbcTemplate = new JdbcTemplate(server.getConnection());
    }

    @Nested
    @DisplayName("insert query")
    class insert {

        Class<InsertPerson> aClass = InsertPerson.class;

        @BeforeEach
        void init() {
            테이블을_생성함(aClass);
        }

        @Test
        @DisplayName("성공적으로 insert 쿼리 생성하여 실행")
        void Success() {
            //given
            final Long id = 33L;
            final String name = "name";
            final int age = 22;
            final String email = "zz";
            final Integer index = 3;

            InsertPerson person = new InsertPerson(id, name, age, email, index);

            final TableName tableName = TableName을_생성함(person);
            final Columns columns = Columns을_생성함(person);
            final Values values = Values을_생성함(person);

            final EntityMeta entityMeta = new EntityMeta(tableName, columns);

            //when
            String q = query.insert(entityMeta, values);

            //then
            assertDoesNotThrow(() -> jdbcTemplate.execute(q));
        }

        @AfterEach
        void after() {
            dropTable(aClass);
        }
    }

    @Nested
    @DisplayName("select query")
    class selectQuery {

        Class<SelectPerson> selectPersonClass = SelectPerson.class;

        @BeforeEach
        void init() {
            테이블을_생성함(selectPersonClass);
        }

        @Test
        @DisplayName("현재 메소드를 읽어 조회하고자 하는 필드를 읽어 select문 실행하여 여러건 가져옴")
        void success() {
            //given
            final Long id = 1L;
            final String name = "홍길동";
            final int age = 20;
            final String email = "zzz";
            final Integer index = 4;

            final SelectPerson person = new SelectPerson(id, name, age, email, index);

            final TableName tableName = TableName을_생성함(person);
            final Columns columns = Columns을_생성함(person);
            final JoinColumn joinColumn = JoinColumn을_생성함(person.getClass());


            insert(person);

            //when
            List<SelectPerson> personList = jdbcTemplate.query(getSelectAllQuery("findAll", tableName, columns),
                new ResultMapper<>(SelectPerson.class));
            SelectPerson result = personList.get(0);

            //then
            assertSoftly(softAssertions -> {
                softAssertions.assertThat(result.getId()).isEqualTo(person.getId());
                softAssertions.assertThat(result.getName()).isEqualTo(person.getName());
                softAssertions.assertThat(result.getAge()).isEqualTo(person.getAge());
                softAssertions.assertThat(result.getEmail()).isEqualTo(person.getEmail());
                softAssertions.assertThat(result.getIndex()).isNull();
            });
        }

        @Test
        @DisplayName("현재 메소드를 읽어 조회하고자 하는 필드를 읽어 select문 실행하여 여러건 가져옴")
        void successList() {
            //given
            final SelectPerson person1 = new SelectPerson(2L, "홍길동", 30, "zㅋ", 2);
            final SelectPerson person2 = new SelectPerson(3L, "김갑돌", 30, "zㅋ", 2);

            insert(person1);
            insert(person2);

            final TableName tableName = TableName을_생성함(person1);
            final Columns columns = Columns을_생성함(person1);
            final JoinColumn joinColumn = JoinColumn을_생성함(person1.getClass());


            //when
            List<SelectPerson> personList = jdbcTemplate.query(getSelectAllQuery("findAll", tableName, columns)
                , new ResultMapper<>(SelectPerson.class));

            //then
            assertThat(personList).size().isEqualTo(2);
        }

        @AfterEach
        void after() {
            dropTable(selectPersonClass);
        }
    }

    @Nested
    @DisplayName("delete query")
    class delete {

        Class<SelectPerson> selectPersonClass = SelectPerson.class;

        @BeforeEach
        void init() {
            테이블을_생성함(selectPersonClass);
        }

        @Test
        @DisplayName("id를 가지고 데이터를 삭제합니다")
        void success() {
            //given
            final Long id = 3L;
            final String name = "name";
            final int age = 30;
            final String email = "zz";
            final Integer index = 1;

            final SelectPerson person = new SelectPerson(id, name, age, email, index);

            insert(person);

            Class<SelectPerson> clazz = SelectPerson.class;
            final TableName tableName = TableName을_생성함(clazz);
            final Columns columns = Columns을_생성함(clazz);

            //when
            String q = query.delete(tableName, columns, id);
            jdbcTemplate.execute(q);

            SelectPerson result = jdbcTemplate.queryForObject(getSelectQuery(selectPersonClass, "findById", id)
                    , new ResultMapper<>(SelectPerson.class));

            //then
            assertThat(result).isNull();
        }

        @AfterEach
        void after() {
            dropTable(selectPersonClass);
        }
    }

    @Nested
    @DisplayName("update 쿼리 실행 확인")
    class update {

        Class<SelectPerson> selectPersonClass = SelectPerson.class;

        @BeforeEach
        void init() {
            테이블을_생성함(selectPersonClass);
        }

        @Test
        @DisplayName("update쿼리 실행으로 값이 변경 되었는지 확인")
        void success() {
            //given
            final Long id = 88L;

            final String actualName = "zz";
            final int actualAge = 30;
            final String actualEmail = "xx";
            final SelectPerson actual = new SelectPerson(id, actualName, actualAge, actualEmail, 3);
            insert(actual);

            final String name = "홍길동";
            final SelectPerson expected = new SelectPerson(id, name, actualAge, actualEmail, 3);

            Class<SelectPerson> clazz = SelectPerson.class;
            final TableName tableName = TableName을_생성함(clazz);
            final Columns columns = Columns을_생성함(clazz);
            final JoinColumn joinColumn = JoinColumn을_생성함(clazz);
            final Values values = Values을_생성함(expected);

            //when
            String q = query.update(values, tableName, columns, id);
            jdbcTemplate.execute(q);
            SelectPerson result = jdbcTemplate.queryForObject(getSelectAllQuery("findAll", tableName, columns),
                new ResultMapper<>(clazz));

            //then
            assertSoftly(softAssertions -> {
                softAssertions.assertThat(actual.getId()).isEqualTo(result.getId());
                softAssertions.assertThat(actual.getName()).isNotEqualTo(result.getName());
                softAssertions.assertThat(actual.getAge()).isEqualTo(result.getAge());
                softAssertions.assertThat(actual.getEmail()).isEqualTo(result.getEmail());
            });
        }

        @AfterEach
        void after() {
            dropTable(selectPersonClass);
        }
    }

    @AfterAll
    static void stop() {
        server.stop();
    }

    private String getSelectAllQuery(String methodName, TableName tableName, Columns columns) {
        return query.selectAll(methodName, tableName, columns);
    }

    private <T> String getSelectQuery(Class<T> tClass, String methodName, Object arg) {
        final TableName tableName = TableName을_생성함(tClass);
        final Columns columns = Columns을_생성함(tClass);
        final JoinColumn joinColumn = JoinColumn을_생성함(tClass);

        final EntityMeta entityMeta = EntityMeta.selectMeta(methodName, tableName, columns, joinColumn, arg);

        return query.select(entityMeta);
    }

    private <T> void 테이블을_생성함(Class<T> tClass) {
        final TableName tableName = TableName을_생성함(tClass);
        final Columns columns = Columns을_생성함(tClass);

        jdbcTemplate.execute(dmlQuery.create(tableName, columns));
    }

    private <T> void insert(T t) {
        final TableName tableName = TableName을_생성함(t);
        final Columns columns = Columns을_생성함(t);
        final Values values = Values.of(t);

        final EntityMeta entityMeta = new EntityMeta(tableName, columns);

        jdbcTemplate.execute(query.insert(entityMeta, values));
    }

    private <T> void dropTable(Class<T> tClass) {
        jdbcTemplate.execute(dmlQuery.drop(TableName을_생성함(tClass)));
    }
}
