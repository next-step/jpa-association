package persistence;

import builder.dml.EntityData;
import entity.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

/*
- 영속성 컨텍스트에 Entity객체를 저장 후 저장되어있는 Entity 객체를 가져온다.
- 영속성 컨텍스트에 저장되어있는 Entity 객체를 제거한다.
- 영속성 컨텍스트에서 스냅샷을 생성한다.
- 영속성 컨텍스트에서 스냅샷을 가져온다.
- 영속성 컨텍스트에서 EntityStatus를 저장 후 저장되어있는 EntityEntry 객체를 가져온다.
*/
class PersistenceContextImplTest {

    @DisplayName("영속성 컨텍스트에 Entity객체를 저장 후 저장되어있는 Entity 객체를 가져온다.")
    @Test
    void insertFindTest() {
        PersistenceContextImpl persistenceContext = new PersistenceContextImpl();
        Person person = createPerson(1);
        EntityKey EntityKey = new EntityKey(1, Person.class);

        EntityData entityData = EntityData.createEntityData(person);

        persistenceContext.insertEntity(EntityKey, entityData);

        assertThat(persistenceContext.findEntity(new EntityKey(1, Person.class)).getEntityInstance())
                .extracting("id", "name", "age", "email")
                .contains(1L, "test1", 29, "test@test.com");
    }

    @DisplayName("영속성 컨텍스트에 저장되어있는 Entity 객체를 제거한다.")
    @Test
    void removeTest() {
        PersistenceContextImpl persistenceContext = new PersistenceContextImpl();
        Person person = createPerson(1);
        EntityData entityData = EntityData.createEntityData(person);
        IntStream.range(1,3).forEach(i -> persistenceContext.insertEntity(new EntityKey(i, Person.class), entityData));

        persistenceContext.deleteEntity(new EntityKey(2, Person.class));

        assertThat(persistenceContext.findEntity(new EntityKey(2, Person.class))).isNull();
    }

    @DisplayName("영속성 컨텍스트에서 스냅샷을 생성한다.")
    @Test
    void addDatabaseSnapshotTest() {
        PersistenceContextImpl persistenceContext = new PersistenceContextImpl();
        Person person = createPerson(1);
        EntityData entityData = EntityData.createEntityData(person);
        IntStream.range(1,3).forEach(i -> persistenceContext.insertDatabaseSnapshot(new EntityKey(i, Person.class), entityData));

        assertThat(persistenceContext.findEntity(new EntityKey(2, Person.class))).isNull();
    }

    @DisplayName("영속성 컨텍스트에서 스냅샷을 가져온다.")
    @Test
    void getDatabaseSnapshotTest() {
        PersistenceContextImpl persistenceContext = new PersistenceContextImpl();
        Person person = createPerson(1);
        EntityData entityData = EntityData.createEntityData(person);
        persistenceContext.insertDatabaseSnapshot(new EntityKey(person.getId(), Person.class), entityData);
        assertThat(persistenceContext.getDatabaseSnapshot(new EntityKey(person.getId(), Person.class)).getEntityInstance())
                .extracting("id", "name", "age", "email")
                .contains(1L, "test1", 29, "test@test.com");
    }

    @DisplayName("영속성 컨텍스트에서 EntityStatus를 저장 후 저장되어있는 EntityEntry 객체를 가져온다.")
    @Test
    void insertEntityEntryMapTest() {
        PersistenceContextImpl persistenceContext = new PersistenceContextImpl();
        EntityKey entityKey = new EntityKey(1, Person.class);

        persistenceContext.insertEntityEntryMap(entityKey, EntityStatus.MANAGED);
        assertThat(persistenceContext.getEntityEntryMap(entityKey))
                .extracting("entityStatus").isEqualTo(EntityStatus.MANAGED);
    }

    private Person createPerson(int i) {
        return new Person((long) i, "test" + i, 29, "test@test.com");
    }

}
