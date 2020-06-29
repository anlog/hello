package cc.ifnot.java.libs;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import cc.ifnot.libs.utils.Lg;

/**
 * author: dp
 * created on: 2020/6/28 10:17 AM
 * description:
 */
class JavaLocksTest {
    @BeforeAll
    static void setUpAll() {
        Lg.level(Lg.MORE);
        Lg.d("============ all in ============");
    }

    @AfterAll
    static void tailDownAll() {
        Lg.d("============ all out ============");
    }

    @Test
    void testSynchronized() {
        Lg.d("test synchronized");

        // no lock with CAS (compare and swap)
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        for (int i = 0; i < 100; i++) {
            Lg.d("%d - %d", i, atomicInteger.getAndIncrement());
        }


    }

    @BeforeEach
    void setUp() {
        Lg.d("============ in ============");
    }

    @AfterEach
    void tailDown() {
        Lg.d("============ out ============");
    }
}
