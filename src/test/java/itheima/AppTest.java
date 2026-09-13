package itheima;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 应用入口的基础测试。
 */
class AppTest {

    @Test
    @DisplayName("应用入口类可被类加载器正常加载")
    void testApplicationClassLoadable() {
        assertNotNull(BigEventApplication.class);
    }

    @Test
    @DisplayName("冒烟测试：断言恒为真")
    void testApp() {
        org.junit.jupiter.api.Assertions.assertTrue(true);
    }
}
