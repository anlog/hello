package cc.ifnot.java.libs.java;

import org.junit.jupiter.api.Test;

import cc.ifnot.libs.utils.Lg;

/**
 * author: dp
 * created on: 2020/7/19 12:03 PM
 * description:
 */
class Java {

    private static int a;
    private static final  int b;

    static {
        Lg.d("static block first init");
    }

    static {
        Lg.d("static b init");
        b = 'b';
    }

    static {
        Lg.d("static block last init");
    }

    private int c;
    private final int d;

    public Java() {
        Lg.d("Constructor");
    }

    {
        Lg.d("object block first");
    }

    {
        Lg.d("object block d init");
        d = 'd';
    }

    {
        Lg.d("object block last");
    }

    @Test
    void dummy() {

    }
}
