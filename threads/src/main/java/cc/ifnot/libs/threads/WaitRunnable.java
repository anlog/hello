package cc.ifnot.libs.threads;

import cc.ifnot.libs.utils.Lg;

/**
 * author: dp
 * created on: 2020/6/5 4:29 PM
 * description:
 */
class WaitRunnable implements Runnable {

    private final Runnable r;
    private boolean done = false;

    public WaitRunnable(Runnable r) {
        this.r = r;
    }

    @Override
    public synchronized void run() {
        r.run();
        done = true;
        Lg.d("done & notifyAll");
        notifyAll();
    }

    public synchronized void waitUntilDone() {
        Lg.d("waitUntilDone");
        while (!done) {
            try {
                Lg.d("not done & wait");
                wait();
            } catch (InterruptedException ignored) {
            }
        }
    }
}
