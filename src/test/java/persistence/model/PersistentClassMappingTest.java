package persistence.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.PersonV0;
import persistence.sql.ddl.PersonV3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PersistentClassMappingTest {

    @DisplayName("저장된 엔티티의 메타 데이터를 가져온다")
    @Test
    public void getMetaData() throws Exception {
        // given
        final Class<PersonV3> entityClass = PersonV3.class;
        PersistentClassMapping.putPersistentClass(entityClass);

        // when
        final PersistentClass<?> persistentClass = PersistentClassMapping.getPersistentClass(entityClass.getName());

        // then
        assertThat(persistentClass).isNotNull();
    }

    @DisplayName("저장된 메타 데이터가 없다면 예외를 반환한다")
    @Test
    public void getNullMetaData() throws Exception {
        // given
        final Class<PersonV0> entityClass = PersonV0.class;

        // when then
        assertThatThrownBy(() -> PersistentClassMapping.getPersistentClass(entityClass.getName()))
                .isInstanceOf(MetaDataModelMappingException.class)
                .hasMessage("entity meta data is not initialized : " + entityClass.getName());
    }

}
