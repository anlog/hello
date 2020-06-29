package cc.ifnot.java.libs;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cc.ifnot.libs.utils.Lg;

/**
 * author: dp
 * created on: 2020/6/28 10:37 AM
 * description:
 */
class JavaTest {
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
    void testString() {
        String a = "abc";
        String c = "abc";
        String b = new String("abc");
        String d = new String("abc");

        Assertions.assertEquals(true, a == c);
        Assertions.assertEquals(false, a == b);
        Assertions.assertEquals(false, b == d);
    }

    @Test
    void testAutoBoxUnBox() {
        // AutoBox UnBox test
        Lg.d("test ");
        Integer i = 10;
        Integer m = 10;
        int j = i;
        boolean x = i == m;
        Assertions.assertEquals(true, i == m);
        Assertions.assertEquals(false, new Integer(200) == (new Integer(200)));
        Assertions.assertEquals(true, 200 == (new Integer(200)));

        Assertions.assertEquals(false, new Float(1) == new Float(1));

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
