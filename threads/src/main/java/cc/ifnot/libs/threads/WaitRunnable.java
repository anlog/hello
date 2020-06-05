package cc.ifnot.libs.threads;

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
        System.out.println("done & notifyAll");
        notifyAll();
    }

    public synchronized void waitUntilDone() {
        System.out.println("waitUntilDone");
        while (!done) {
            try {
                System.out.println("not done & wait");
                wait();
            } catch (InterruptedException ignored) {
            }
        }
    }
}
