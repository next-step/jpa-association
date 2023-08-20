package persistence.sql.ddl.column;

import fixture.PersonV3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.model.EntityMeta;
import persistence.entity.model.EntityMetaFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class DdlColumnTest {

    private List<DdlColumn> ddlColumns;

    @BeforeEach
    void setUp() {
        EntityMeta entityMeta = EntityMetaFactory.INSTANCE.create(PersonV3.class);
        ddlColumns = DdlColumn.ofList(entityMeta);
    }

    // name
    @Test
    @DisplayName("@Column 의 name 설정이 있는 경우 컬럼 이름으로 반환한다")
    public void columnName() throws NoSuchFieldException {
        DdlColumn basicName = getDdlColumn("email");
        DdlColumn columnName = getDdlColumn("nick_name");

        assertAll(
                () -> assertThat(basicName.name()).isEqualTo("email"),
                () -> assertThat(columnName.name()).isEqualTo("nick_name")
        );
    }

    // type
    @Test
    @DisplayName("변수 타입에 따라 type 정보를 반환한다")
    public void type() throws NoSuchFieldException {
        DdlColumn id = getDdlColumn("id");
        DdlColumn name = getDdlColumn("nick_name");
        DdlColumn age = getDdlColumn("old");

        assertAll(
                () -> assertThat(id.type()).isEqualTo("bigint"),
                () -> assertThat(name.type()).isEqualTo("varchar(255)"),
                () -> assertThat(age.type()).isEqualTo("integer")
        );
    }

    @Test
    @DisplayName("@Column 타입이 String 일 경우 type 에 length 정보를 추가한다")
    public void type_length() throws NoSuchFieldException {
        DdlColumn email = getDdlColumn("email");

        assertThat(email.type()).isEqualTo("varchar(255)");
    }

    // options

    @Test
    @DisplayName("@Id 옵션에 맞는 조건을 반환한다")
    public void id() throws NoSuchFieldException {
        DdlColumn id = getDdlColumn("id");

        assertThat(id.options()).isEqualTo("primary key auto_increment");
    }

    @Test
    @DisplayName("@Column 의 nullable 여부에 따라 조건을 반환한다")
    public void nullable() throws NoSuchFieldException {
        DdlColumn email = getDdlColumn("email");

        assertThat(email.options()).isEqualTo("not null");
    }

    private DdlColumn getDdlColumn(String name) {
        return ddlColumns.stream()
                .filter(it -> it.name().equals(name))
                .findFirst()
                .get();
    }
}