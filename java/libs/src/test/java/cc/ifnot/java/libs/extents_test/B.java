package cc.ifnot.java.libs.extents_test;

import cc.ifnot.libs.utils.Lg;

/**
 * author: dp
 * created on: 2020/7/17 1:01 PM
 * description:
 */
public class B extends A {

    // just be B
    private static int a = 100;

    static {
        Lg.d("i am B");
    }

    // can't over write, cause it's class
    public static int staticMethod() {
        Lg.d("i am B");
        return a + 'B';
    }

    @Override
    public int method() {
        Lg.d("i am B");
        return super.method() + 1;
    }
}
