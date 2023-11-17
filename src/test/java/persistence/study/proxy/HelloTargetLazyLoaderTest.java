package persistence.study.proxy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


@DisplayName("LazyLoader CallBack 테스트")
public class HelloTargetLazyLoaderTest {

    @Test
    @DisplayName("Enhancer를 통해 컬렉션 필드를 LazyLoad하는 Proxy를 생성할 수 있다.")
    public void canLazyLoadToUppercaseProxy() {
        final Enhancer enhancer = new Enhancer();

        enhancer.setSuperclass(List.class);
        enhancer.setCallback(new UppercaseLazyLoader());

        final List<Address> lazyLoadedAddress = (List<Address>) enhancer.create();

        final HelloTarget helloTarget = new HelloTarget(lazyLoadedAddress);

        assertThat(helloTarget.getAddress().size()).isEqualTo(2);
    }

    /**
     * LazyLoader
     */
    public static class UppercaseLazyLoader implements LazyLoader {

        @Override
        public Object loadObject() throws Exception {
            return List.of(new Address("서울특별시"), new Address("선릉"));
        }
    }
}
