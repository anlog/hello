package cc.ifnot.libs.threads;

public class MyClass {

    public static void main(String[] args) {

        MyRunnable myRunnable = new MyRunnable(new Runnable() {
            @Override
            public void run() {
                System.out.println(" -- start");
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(" -- end");
            }
        });

        myRunnable.testRun();
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(myRunnable).start();



        if(true) return;

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