package persistence.sql.ddl.builder;

import fixture.PersonV1;
import fixture.PersonV2;
import fixture.PersonV3;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.model.EntityMeta;
import persistence.entity.model.EntityMetaFactory;
import persistence.sql.ddl.column.DdlColumn;

import static org.assertj.core.api.Assertions.assertThat;

class ColumnBuilderTest {

    @Test
    @DisplayName("기본적인 column 쿼리 만들기")
    public void create_column() {
        EntityMeta entityMeta = EntityMetaFactory.INSTANCE.create(PersonV1.class);
        ColumnBuilder columnBuilder = new ColumnBuilder(DdlColumn.ofList(entityMeta));

        assertThat(columnBuilder.build()).isEqualToIgnoringCase(
                "id bigint primary key, name varchar(255), age integer"
        );
    }

    @Test
    @DisplayName("추가된 어노테이션을 반영하여 column 쿼리 만들기 (@GeneratedValue, @Column)")
    public void created_ddl_2() {
        EntityMeta entityMeta = EntityMetaFactory.INSTANCE.create(PersonV2.class);
        ColumnBuilder columnBuilder = new ColumnBuilder(DdlColumn.ofList(entityMeta));

        assertThat(columnBuilder.build()).isEqualToIgnoringCase(
                "id bigint primary key auto_increment, nick_name varchar(255), old integer, email varchar(255) not null");
    }

    @Test
    @DisplayName("추가된 어노테이션을 반영하여 column 쿼리 만들기 (@Transient)")
    public void created_ddl_3() {
        EntityMeta entityMeta = EntityMetaFactory.INSTANCE.create(PersonV3.class);
        ColumnBuilder columnBuilder = new ColumnBuilder(DdlColumn.ofList(entityMeta));

        assertThat(columnBuilder.build()).isEqualToIgnoringCase(
                "id bigint primary key auto_increment, nick_name varchar(255), old integer, email varchar(255) not null"
        );
    }
}