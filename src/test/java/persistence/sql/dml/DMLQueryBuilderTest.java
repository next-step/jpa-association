package persistence.sql.dml;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.core.EntityMetaManager;
import persistence.entity.Order;
import persistence.entity.Person;
import persistence.entity.metadata.EntityMetadata;
import persistence.entity.metadata.RelationEntityTable;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DMLQueryBuilderTest {

    private Person person;
    private DMLQueryBuilder dmlQueryBuilder = DMLQueryBuilder.getInstance();
    private EntityMetaManager entityMetaManager = EntityMetaManager.getInstance();

    final String name = "kassy";
    final int age = 30;
    final String email = "test@gmail.com";

    @BeforeEach
    public void setUp() {
        person = new Person();
        person.setName(name);
        person.setAge(age);
        person.setEmail(email);
    }

    @Test
    @DisplayName("insert 쿼리 생성")
    public void insertQueryTest() {
        EntityMetadata entityMetadata = entityMetaManager.getEntityMetadata(person.getClass());
        String query = dmlQueryBuilder.insertSql(entityMetadata.getTableName(), entityMetadata.getColumns(), entityMetadata.getColumnValues(person));

        assertEquals("INSERT INTO users (nick_name, old, email) VALUES ('" + name + "', " + age + ", '" + email + "')", query);
    }

    @Test
    @DisplayName("selectAll 쿼리 생성")
    public void selectAllQueryTest() {
        EntityMetadata entityMetadata = entityMetaManager.getEntityMetadata(person.getClass());
        String query = dmlQueryBuilder.selectAllSql(entityMetadata.getTableName(), entityMetadata.getColumns());

        assertEquals("SELECT id, nick_name, old, email FROM users", query);
    }

    @Test
    @DisplayName("selectById 쿼리 생성")
    void selectByIdSqlTest() {
        EntityMetadata entityMetadata = entityMetaManager.getEntityMetadata(person.getClass());
        String query = dmlQueryBuilder.selectByIdQuery(entityMetadata.getTableName(), entityMetadata.getColumns(), 1L);

        assertEquals("SELECT users.id, users.nick_name, users.old, users.email FROM users WHERE users.id = 1", query);
    }

    @Test
    @DisplayName("delete 쿼리 생성")
    void deleteSqlTest() {
        person.setId(1L);
        EntityMetadata entityMetadata = entityMetaManager.getEntityMetadata(person.getClass());
        String query = dmlQueryBuilder.deleteSql(entityMetadata.getTableName(), entityMetadata.getColumns(), entityMetadata.getColumnValues(person));

        assertEquals("DELETE FROM users WHERE id = 1", query);
    }

    @Test
    @DisplayName("update 쿼리 생성")
    void updateSqlTest() {
        person.setId(1L);
        EntityMetadata entityMetadata = entityMetaManager.getEntityMetadata(person.getClass());
        String query = dmlQueryBuilder.updateSql(entityMetadata.getTableName(), entityMetadata.getColumns(), entityMetadata.getColumnValues(person));

        assertEquals("UPDATE users SET nick_name = '" + name + "', old = " + age + ", email = '" + email + "' WHERE id = 1", query);
    }

    @Test
    @DisplayName("join query 생성")
    public void selectJoinQueryTest() {
        EntityMetadata entityMetadata = entityMetaManager.getEntityMetadata(Order.class);
        RelationEntityTable joinTable = entityMetadata.getRelationEntityTables().get(0);
        EntityMetadata joinEntityMeta = entityMetaManager.getEntityMetadata(joinTable.getEntity());

        String query = dmlQueryBuilder.selectJoinQuery(entityMetadata, joinEntityMeta, joinTable.getJoinColumnName(), 1L);

        assertEquals("SELECT order_items.id, order_items.product, order_items.quantity, order_items.order_id FROM orders LEFT JOIN order_items ON orders.id = order_items.order_id WHERE orders.id = 1", query);
    }

}
