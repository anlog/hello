package cc.ifnot.java.libs.extents_test;

import cc.ifnot.libs.utils.Lg;

/**
 * author: dp
 * created on: 2020/7/17 12:58 PM
 * description:
 */
public class A {

    private static int a;

    static {
        Lg.d("i am A");
    }

    private int b;

    public A() {
        Lg.d("A constructor");
    }

    public static int staticMethod() {
        Lg.d("i am A");
        return a + 'A';
    }

    public int method() {
        Lg.d("i am A");
        return b + 'A';
    }
}
