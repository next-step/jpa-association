package persistence.entity.manager;

import mock.MockPersistenceEnvironment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.Application;
import persistence.core.EntityMetadataProvider;
import persistence.core.EntityScanner;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleEntityManagerFactoryTest {

    @Test
    @DisplayName("EntityManagerFactory 를 이용해 EntityManager 를 생성할 수 있다.")
    void test() {
        final EntityScanner entityScanner = new EntityScanner(Application.class);
        final EntityMetadataProvider entityMetadataProvider = EntityMetadataProvider.getInstance();
        final EntityManagerFactory entityManagerFactory = new SimpleEntityManagerFactory(entityMetadataProvider, entityScanner, new MockPersistenceEnvironment());

        final EntityManager entityManager = entityManagerFactory.createEntityManager();

        assertThat(entityManager).isNotNull();
    }
}
