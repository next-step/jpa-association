package domain;

public class FixturePerson {

    public static FixtureEntity.Person create(final Long id) {
        return new FixtureEntity.Person(id, "min", 30, "jongmin4943@gmail.com");
    }

}
