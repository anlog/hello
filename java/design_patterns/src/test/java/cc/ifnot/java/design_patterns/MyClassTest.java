package cc.ifnot.java.design_patterns;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import cc.ifnot.java.design_patterns.singleton.EnumSt;
import cc.ifnot.libs.utils.Lg;

class MyClassTest {

    private static ExecutorService threadPool;
    private static AtomicLong counter = new AtomicLong();
    private static ExecutorService executorService;

    @BeforeAll
    static void initAll() {
        Lg.tag("Test");
        Lg.showMore(true);
        executorService = Executors.newCachedThreadPool(
                runnable -> new Thread(runnable, "junit-" + counter.incrementAndGet()));
    }

    @BeforeEach
    void init() {
    }

    @Test
    @Timeout(50)
    @DisplayName("testEnumSt")
    void testEnumSt() throws InterruptedException {
//        Assertions.assertEquals(cd.getCount(), 0);
        Lg.d("enter lg");

        CountDownLatch latch = new CountDownLatch(1000);

        for (int i = 0; i < 1000; i++) {
            int ii = i;
            executorService.execute(() -> {
                Lg.d("count: %d - actual: %s = expected: %s", ii,
                        EnumSt.INSTANCE, EnumSt.INSTANCE);
                latch.countDown();
            });
        }
        latch.await();

    }

    @AfterEach
    void tearDown() {

    }

    @AfterAll
    static void tearDownAll() {

    }
}