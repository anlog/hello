package cc.ifnot.java.libs;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

import cc.ifnot.libs.utils.Lg;

/**
 * author: dp
 * created on: 2020/6/28 10:17 AM
 * description:
 */
class JavaLocksTest {
    @BeforeAll
    static void setUpAll() {
        Lg.level(Lg.MORE);
        Lg.d("============ all in ============");
    }

    @AfterAll
    static void tailDownAll() {
        Lg.d("============ all out ============");
    }

    @Test
    void testSynchronized() {

        final Thread t1 = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(500);
                    Lg.d("count: %d holdsLock: %s", Inner.getNothing(), Thread.holdsLock(Inner.class));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        t1.start();

        final Thread t2 = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(300);
                    Lg.d("count: %d holdsLock: %s", Inner.getCount(), Thread.holdsLock(Inner.class));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testPackUnpack() {

        ReentrantLock lock = new ReentrantLock();

        final Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                lock.lock();
                Lg.d("get lock");
                LockSupport.park();
                lock.unlock();
            }
        });


        final Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Lg.d("t1 enter");
                lock.lock();
                synchronized (this) {
                    LockSupport.park();
                }
                Lg.d("t1 done");
                lock.unlock();
            }
        });
        t2.start();
        t1.start();

        Lg.d(LockSupport.getBlocker(t1));

        while (true) {
            Thread.yield();
        }

    }

    @Test
    void test() {
        ReentrantLock lock = new ReentrantLock();

        lock.newCondition();

    }

    @Test
    void testWaitNotify() {

        final Object lock = new Object();

        final Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Lg.d("wait called: %s", lock);
//                    try {
//                        lock.wait();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    Lg.d("wait done");
                }
            }
        });
        t1.start();
        synchronized (lock) {
            lock.notify();
        }

        while (true) {
            Thread.yield();
        }

    }

    @Test
    void testSynchronizedWaitNotify() {
        Lg.d("testSynchronizedWaitNotify");

        int threads = 10;

        class Counter {
            private int count = 0;

            int getCount() {
                return count;
            }

            int getAndIncrease() {
                final int c = count;
                count = c + 1;
                return c;
            }

            int getAndDecrease() {
                final int c = count;
                count = c - 1;
                return c;
            }

        }

        final Object lock = new Object();
        final Counter counter = new Counter();

        for (int i = 0; i < threads / 2; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        synchronized (lock) {
                            Lg.d("count p: %s", counter.getCount());
                            while (counter.getCount() == 5) {
                                Lg.d("wait p %d", counter.getCount());
                                try {
                                    lock.wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            Lg.d("count p: %d -> %d", counter.getAndIncrease(),
                                    counter.getCount());
                            lock.notifyAll();
                        }
                    }
                }
            }).start();
        }

        for (int i = 0; i < threads / 2; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        synchronized (lock) {
                            Lg.d("count c: %s", counter.getCount());
                            while (counter.getCount() == 0) {
                                Lg.d("wait c %d", counter.getCount());
                                try {
                                    lock.wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            Lg.d("count c: %d -> %d", counter.getAndDecrease(),
                                    counter.getCount());
                            lock.notifyAll();
                        }
                    }
                }
            }).start();

        }

        while (true) {
            Thread.yield();
        }
    }

    @Test
    void testAtomicInteger() {
        Lg.d("test testAtomicInteger");

        // no lock with CAS (compare and swap)
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(100);
        for (int i = 0; i < 100; i++) {
            final int t = i;
            new Thread(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                latch.countDown();
                Lg.d("%d - %d", t, atomicInteger.getAndIncrement());
            }).start();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @BeforeEach
    void setUp() {
        Lg.d("============ in ============");
    }

    @AfterEach
    void tailDown() {
        Lg.d("============ out ============");
    }

    static class Inner {
        static int count = 0;

        public static synchronized int getCount() {
            Lg.d("here: %d - holdsLock: %s", count--, Thread.holdsLock(Inner.class));
            // wait here
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return count;
        }

        public synchronized static Object getNothing() {
            return null;
        }
    }
}
