package cc.ifnot.java.libs;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cc.ifnot.libs.utils.Lg;
import okio.ByteString;

public class OkIoTest {
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
    void test() {
        Lg.d("in");
//        ByteString byteString = new ByteString();

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
