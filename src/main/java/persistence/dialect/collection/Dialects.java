package persistence.dialect.collection;

import java.util.List;
import persistence.dialect.Dialect;
import persistence.dialect.H2DbDialect;
import persistence.dialect.MySqlDbDialect;

public class Dialects {
    private final List<Dialect> DIALECTS;

    public Dialects() {
        DIALECTS = List.of(new H2DbDialect(), new MySqlDbDialect());
    }

    public Dialect get(String dbType) {
        return DIALECTS.stream()
                .filter(dialect -> dialect.support(dbType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("not found valid dialect for dbType: " + dbType));
    }
}
