package database.sql.ddl;

import database.dialect.Dialect;
import database.dialect.MySQLDialect;
import entity.Person;
import jakarta.persistence.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CreateTest {
    private final Dialect dialect = MySQLDialect.getInstance();

    @ParameterizedTest
    @CsvSource(value = {
            "database.sql.ddl.OldPerson1:CREATE TABLE OldPerson1 (id BIGINT PRIMARY KEY, name VARCHAR(255) NULL, age INT NULL)",
            "database.sql.ddl.OldPerson2:CREATE TABLE OldPerson2 (id BIGINT AUTO_INCREMENT PRIMARY KEY, nick_name VARCHAR(255) NULL, old INT NULL, email VARCHAR(255) NOT NULL)",
            "database.sql.ddl.OldPerson3:CREATE TABLE users (id BIGINT AUTO_INCREMENT PRIMARY KEY, nick_name VARCHAR(255) NULL, old INT NULL, email VARCHAR(255) NOT NULL)"
    }, delimiter = ':')
    void buildCreateQuery(Class<?> clazz, String expected) {
        assertCreateQuery(clazz, expected);
    }

    @Test
    void buildCreateQueryForAssociatedEntity() {
        assertCreateQuery(Departure.class, "CREATE TABLE Departure (id BIGINT PRIMARY KEY)");
        assertCreateQuery(Employee.class, List.of(Departure.class), "CREATE TABLE Employee (id BIGINT PRIMARY KEY, name VARCHAR(255) NULL, departure_id BIGINT NOT NULL)");
    }

    private void assertCreateQuery(Class<?> clazz, List<Class<?>> entities, String expected) {
        Create create = new Create(clazz, entities, dialect);
        String actual = create.buildQuery();
        assertThat(actual).isEqualTo(expected);
    }

    private void assertCreateQuery(Class<?> clazz, String expected) {
        Create create = new Create(clazz, dialect);
        String actual = create.buildQuery();
        assertThat(actual).isEqualTo(expected);
    }

    @Entity
    static class Departure {
        @Id
        @GeneratedValue
        Long id;

        @OneToMany
        @JoinColumn(name = "departure_id")
        List<Employee> employees;
    }

    @Entity
    static class Employee {
        @Id
        @GeneratedValue
        Long id;

        String name;
    }

    @Test
    void getColumnDefinitions() {
        assertCreateQuery(
                Person.class,
                "CREATE TABLE users (id BIGINT AUTO_INCREMENT PRIMARY KEY, nick_name VARCHAR(255) NULL, old INT NULL, email VARCHAR(255) NOT NULL)"
        );
    }
}
