package itheima;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ThreadLocal 隔离性测试。
 */
class ThreadLocalTest {

    @Test
    @DisplayName("ThreadLocal 在不同线程间相互隔离")
    void testThreadLocalIsolation() throws InterruptedException {
        ThreadLocal<String> threadLocal = new ThreadLocal<>();
        AtomicReference<String> blueValue = new AtomicReference<>();
        AtomicReference<String> greenValue = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(2);

        new Thread(() -> {
            threadLocal.set("萧炎");
            blueValue.set(threadLocal.get());
            latch.countDown();
        }, "蓝色").start();

        new Thread(() -> {
            threadLocal.set("药尘");
            greenValue.set(threadLocal.get());
            latch.countDown();
        }, "绿色").start();

        assertTrue(latch.await(5, TimeUnit.SECONDS), "两个线程应在超时前完成");

        // 每个线程只能读到自己的值，互不干扰
        assertEquals("萧炎", blueValue.get());
        assertEquals("药尘", greenValue.get());
        assertNotEquals(blueValue.get(), greenValue.get(), "不同线程的 ThreadLocal 值不应相同");

        // 主线程从未 set 过，应为 null
        assertEquals(null, threadLocal.get());
    }
}
