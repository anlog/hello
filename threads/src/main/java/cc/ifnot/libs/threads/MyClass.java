package cc.ifnot.libs.threads;

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
                System.out.println("-- " + Thread.currentThread());
            }
        });

        waitRunnable.waitUntilDone();
        new Thread(waitRunnable).start();
        System.out.println("-- " + Thread.currentThread());

        new Thread(new Runnable() {
            @Override
            public void run() {
                waitRunnable.waitUntilDone();
            }
        }).start();

        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}