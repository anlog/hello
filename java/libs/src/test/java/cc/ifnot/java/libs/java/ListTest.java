package cc.ifnot.java.libs.java;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

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
    void testMap() {
        Lg.d("===========");
        final HashMap<Object, Object> map = new HashMap<>();
        map.put(null, null);
        map.put(1, null);
        map.put("a", null);
        map.put(Integer.valueOf(2), "10010");
        map.put("b", "xyz");

        class Hash {

            private final int mKey;

            public Hash(int key) {
                this.mKey = key;
            }

            @Override
            public int hashCode() {
                return mKey % 10;
            }
        }

        final Random random = new Random();
        for (int i = 0; i < 10000; i++) {
            Hash key = new Hash(random.nextInt() % 100);
            int value = random.nextInt() % 1000 + 1000;
            map.put(key, value);
        }

        Lg.d(map);

    }

    @Test
    void testTreeMap() {
        final TreeMap<String, String> treeMap = new TreeMap<>();
//        Random random = new Random();
//        for (int i = 0; i < 100; i++) {
//            int s = random.nextInt() | i;
//            s = s > 0 ? s : -s;
//            treeMap.put(String.valueOf(s), String.valueOf(s & 0xff ^ s >> 16));
//        }
        for (int i = 0; i < 10; i++) {
            treeMap.put(String.valueOf(i), String.valueOf(i));
        }

        Lg.d(treeMap);
    }

    @Test
    void testArrayDeque() {
        final ArrayDeque<Integer> deque = new ArrayDeque<>();

        for (int i = 0; i < 100; i++) {
            deque.add(i);
        }

        for (int i = 0; i < 20; i++) {
            deque.pop();
        }
        deque.poll();

        Lg.d(deque);
    }

    @Test
    void testRemove() {
        final ArrayList<String> l = new ArrayList<>();
        l.add("hello");
        l.add("world");

        Lg.d(l);
        Integer index = 0;
        l.remove(index);
        l.remove(0);
        Lg.d(l);

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
            if (l.get(i) % 2 == 0) {
                l.remove(i--);
                // when remove one, should i --
            }
        }
        Lg.d(l);


        final Iterator<Integer> iterator = l.iterator();
        while (iterator.hasNext()) {
            final Integer next = iterator.next();
            if (next % 2 != 0) {
                iterator.remove();
            }
        }
        Lg.d(l);

        final List<String> list = Arrays.asList("a", "b");
        // throw UnsupportedOperationException
        list.add("c"); // java.util.Arrays.ArrayList is not overwrite add

        Lg.d("------------");

        for (Integer j : l) {
            // will throw java.util.ConcurrentModificationException
            if (j % 2 != 0) {
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
