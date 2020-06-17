package cc.ifnot.libs.threads;

import cc.ifnot.libs.utils.Lg;

public class MyClass {

    public static void main(String[] args) {

        final WaitRunnable waitRunnable = new WaitRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Lg.d("-- " + Thread.currentThread());
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                waitRunnable.waitUntilDone();
                Lg.d("-- " + Thread.currentThread());
            }
        }).start();
        new Thread(waitRunnable).start();
    }
}