package proxy;

import domain.Person;
import domain.PersonFixture;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;

class SimpleProxyTest {

    @Test
    @DisplayName("프록시 실행")
    void executeProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Person.class);
        enhancer.setCallback((LazyLoader) () -> {
            System.out.println("프록시가 실행됨");
            return PersonFixture.createPerson();
        });

        Person person = (Person) enhancer.create();
        System.out.println(person.getName());
        System.out.println(person.getName());
        assertAll(
                () -> Assertions.assertThat(
                        person.getName()
                ).isEqualTo("고정완"),
                () -> Assertions.assertThat(
                        person.getName()
                ).isEqualTo("고정완")
        );
    }
}
