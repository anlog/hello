package cc.ifnot.java.libs;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import cc.ifnot.libs.utils.Lg;

/**
 * author: dp
 * created on: 2020/6/21 5:06 PM
 * description:
 */
class RxJavaTest {

    @BeforeEach
    void setUp() {
    }

    @BeforeAll
    static void setUpAll() {
        Lg.tag("RxJavaTest");
        Lg.showMore(true);
    }

    @Test
    @DisplayName("java")
    public void test() throws ExecutionException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        String str = "Hello world";
        String s = CompletableFuture.completedFuture(str).get();

        Lg.d("test - %s", s);


    }

    @AfterEach
    void tearDown() {
    }
}