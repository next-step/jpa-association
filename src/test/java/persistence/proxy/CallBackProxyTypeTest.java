package persistence.proxy;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import net.sf.cglib.proxy.Callback;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CallBackProxyTypeTest {

    @Test
    @DisplayName(" CallBackProxyType은 인덱스에 의해 순서가 결정된다. ")
    void getCallbacks() {
        Callback[] values = CallBackProxyType.getCallbacks();

        assertSoftly((it) -> {
            it.assertThat(values[CallBackProxyType.HELLO_TARGET_UPPER_STRING.getIndex()])
                    .isEqualTo(CallBackProxyType.HELLO_TARGET_UPPER_STRING.getMethodInterceptor());

            it.assertThat(values[CallBackProxyType.HELLO_TARGET_BRACE_STRING.getIndex()])
                    .isEqualTo(CallBackProxyType.HELLO_TARGET_BRACE_STRING.getMethodInterceptor());
        });
    }

}
