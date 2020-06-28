package cc.ifnot.java.libs;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import cc.ifnot.libs.utils.Lg;

class CompletableFutureTest {

    CountDownLatch latch;

    @BeforeAll
    static void setUpAll() {
        Lg.tag("CompletableFutureTest");
        Lg.level(Lg.MORE);
        Lg.d("-----------in-------------");
    }

    @AfterAll
    static void tearDownAll() {
        Lg.d("-----------out-------------");
    }

    @BeforeEach
    void setUp() {
        latch = new CountDownLatch(1);
    }

    @Test
    void testCompletableFutureRun() {
        Lg.d("testCompletableFutureRun in");
        CompletableFuture.runAsync(() -> Lg.d("run"));

        CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                Lg.d("supplyAsync");
                return "null";
            }
        }).whenCompleteAsync(new BiConsumer<String, Throwable>() {
            @Override
            public void accept(String s, Throwable throwable) {
                Lg.d("async done");
            }
        }).
                whenComplete(new BiConsumer<String, Throwable>() {
            @Override
            public void accept(String s, Throwable throwable) {
                Lg.d("sync done");
            }
        });
        Lg.d("testCompletableFutureRun out");
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
}
