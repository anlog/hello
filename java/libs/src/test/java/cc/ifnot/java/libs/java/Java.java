package cc.ifnot.java.libs.java;

import org.junit.jupiter.api.Test;

import java.util.concurrent.locks.ReentrantLock;

import cc.ifnot.java.libs.extents_test.B;
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
    void testInt() {
        Lg.d("%s - %s", int.class, Integer.class);
    }

    @Test
    void dummy() {

        ReentrantLock lock = new ReentrantLock();
        for (int i = 0; i < 5; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    lock.lock();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }finally {
                        lock.unlock();

                    }
                }
            }).start();
        }
        Lg.d();

        new B(1);
    }
}
