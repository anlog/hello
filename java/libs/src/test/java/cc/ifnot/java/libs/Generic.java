package cc.ifnot.java.libs;

import org.junit.jupiter.api.Test;

import cc.ifnot.libs.utils.Lg;

/**
 * author: dp
 * created on: 2020/7/20 7:46 PM
 * description:
 */
class Generic<T> {

    T get(T a) {
        return a;
    }

    @Test
    void getTest() {
        final Generic<String> stringGeneric = new Generic<>();
        final String abc = stringGeneric.get("abc");
        Lg.d("%s", abc);

    }
}
