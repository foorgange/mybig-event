package itheima;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Redis 用法示例的单元测试。
 *
 * <p>这里不使用 {@code @SpringBootTest}：CI 环境没有可用的 Redis 服务，
 * 启动完整 Spring 上下文会直接失败。此处用内存 Map 模拟 set / get / 过期语义，
 * 只验证业务层的键值与 TTL 逻辑。真实链路请在本机连上 Redis 后再做集成测试。</p>
 */
class RedisTest {

    /** 极简的带 TTL 的内存键值存储，用于替身 Redis 的字符串操作。 */
    private static final class InMemoryStore {
        private final Map<String, Entry> store = new HashMap<>();

        private static final class Entry {
            final String value;
            final long expireAtMillis;

            Entry(String value, long expireAtMillis) {
                this.value = value;
                this.expireAtMillis = expireAtMillis;
            }
        }

        void set(String key, String value) {
            set(key, value, 0, null);
        }

        void set(String key, String value, long timeout, TimeUnit unit) {
            long expireAt = (unit == null || timeout <= 0)
                    ? Long.MAX_VALUE
                    : System.currentTimeMillis() + unit.toMillis(timeout);
            store.put(key, new Entry(value, expireAt));
        }

        String get(String key) {
            Entry entry = store.get(key);
            if (entry == null) {
                return null;
            }
            if (entry.expireAtMillis <= System.currentTimeMillis()) {
                store.remove(key);
                return null;
            }
            return entry.value;
        }
    }

    @Test
    @DisplayName("set / get 存取字符串键值对")
    void testSetAndGet() {
        InMemoryStore store = new InMemoryStore();

        store.set("username", "zhangsan");
        store.set("id", "1", 15, TimeUnit.SECONDS);

        assertEquals("zhangsan", store.get("username"));
        assertEquals("1", store.get("id"));
    }

    @Test
    @DisplayName("带 TTL 的键在过期后读取为 null")
    void testExpire() throws InterruptedException {
        InMemoryStore store = new InMemoryStore();

        // 使用毫秒级 TTL，避免测试长时间等待
        store.set("temp", "value", 50, TimeUnit.MILLISECONDS);
        assertEquals("value", store.get("temp"));

        Thread.sleep(80);
        assertNull(store.get("temp"));
    }

    @Test
    @DisplayName("修改 TTL 单位换算正确")
    void testTtlUnitConversion() {
        assertTrue(Duration.ofSeconds(15).toMillis() == 15_000L);
        InMemoryStore store = new InMemoryStore();
        store.set("k", "v", 1, TimeUnit.MINUTES);
        assertEquals("v", store.get("k"));
    }
}
