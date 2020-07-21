package cc.ifnot.java.libs;

import java.util.HashMap;
import java.util.Map;

import cc.ifnot.libs.utils.Lg;

/**
 * author: dp
 * created on: 2020/7/21 9:24 AM
 * description:
 */
class CrashTest {

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
//                getClass().getAnnotatedSuperclass()
                e.printStackTrace();
            }
        });

        new Thread(new Runnable() {
            Map<Integer, Thread> maps = new HashMap<>();
            int i = 0;

            @Override
            public void run() {
                while (true) {
                    maps.put(i++, Thread.currentThread());
                }
            }
        }).start();

        while (true) {
            try {
                Thread.sleep(1000);
                Lg.d("sleep");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Thread.yield();
        }
    }
}
