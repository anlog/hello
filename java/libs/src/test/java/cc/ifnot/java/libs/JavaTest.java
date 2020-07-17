package cc.ifnot.java.libs;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import cc.ifnot.java.libs.extents_test.A;
import cc.ifnot.java.libs.extents_test.B;
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
    void testHashMapListSeq() {
        // HashMap Node, linked list insert at last
        class I {
            int mark;

            I(int a) {
                mark = a;
            }

            @Override
            public int hashCode() {
                return 0;
            }
        }

        final HashMap<I, Object> map = new HashMap<>();
        for (int i = 0; i < 7; i++) {
            map.put(new I(i), null);
        }
        Lg.d();

    }

    @Test
    void testStaticExtents() {
        final B b = new B();
        Lg.d("%s - %s", B.staticMethod(), B.class);
        Lg.d("%s - %s", b.method(), b.getClass());

        Lg.d("%s - %s", A.staticMethod(), B.staticMethod());

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
    void testPrimitive() {

        Lg.d(int.class.getName());
        Lg.d(boolean.class.getClasses());
        Lg.d(String.class.getName());
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
