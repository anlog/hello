package cc.ifnot.libs.threads;

/**
 * author: dp
 * created on: 2020/6/5 6:32 PM
 * description:
 */
class MyRunnable implements Runnable {

    private final Runnable r;

    public MyRunnable(Runnable r) {
        this.r = r;
    }

    @Override
    public synchronized void run() {
        r.run();
        System.out.println("run -- " + Thread.currentThread());
    }

    public synchronized void testRun() {
        System.out.println("testRun -- " + Thread.currentThread());
    }
}
