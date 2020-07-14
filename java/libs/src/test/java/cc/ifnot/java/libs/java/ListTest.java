package cc.ifnot.java.libs.java;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import cc.ifnot.libs.utils.Lg;

/**
 * author: dp
 * created on: 2020/7/14 12:50 PM
 * description:
 */
class ListTest {

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
        final ArrayList<Integer> l = new ArrayList<>();
        l.add(1);
        l.add(2);
        l.add(2);
        l.add(3);
        l.add(4);
        l.add(5);

        for (int i = 0; i < l.size(); i++) {
            Lg.d("size: %d-%d", l.size(), i);
            if(l.get(i) % 2 == 0) {
                l.remove(i--);
                // when remove one, should i --
            }
        }
        Lg.d(l);

        Lg.d("------------");

        for (Integer j: l) {
            // will throw java.util.ConcurrentModificationException
            if(j % 2 != 0) {
                l.remove(j);
            }
        }
        Lg.d(l);
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
