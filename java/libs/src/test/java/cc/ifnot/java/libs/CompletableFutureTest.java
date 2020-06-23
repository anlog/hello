package cc.ifnot.java.libs;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Function;

import cc.ifnot.libs.utils.Lg;

class CompletableFutureTest {

    @BeforeAll
    static void setUpAll() {
        Lg.tag("CompletableFutureTest");
        Lg.showMore(true);
        Lg.d("-----------in-------------");
    }


    CountDownLatch latch;

    @BeforeEach
    void setUp() {
        latch = new CountDownLatch(1);
    }


    @Test
    void testCompletableFuture() throws InterruptedException {


        CompletableFuture.runAsync(() -> {
            Lg.d("runAsync");
            latch.countDown();
        });

        CompletableFuture.runAsync(() -> Lg.d("runAsync with Executor"), Executors.newSingleThreadExecutor());

        CompletableFuture.completedFuture("abc").thenApply((Function<String, Object>) s -> {
            Lg.d("thenApply %s", s);
            return s;
        }).thenAcceptAsync(o -> Lg.d("thenAccept %s", o));


        latch.await();
    }


    @AfterAll
    static void tearDownAll() {
        Lg.d("-----------out-------------");
    }
}
