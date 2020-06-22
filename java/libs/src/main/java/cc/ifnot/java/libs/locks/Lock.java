package cc.ifnot.java.libs.locks;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import cc.ifnot.libs.utils.Lg;

/**
 * author: dp
 * created on: 2020/6/22 10:14 AM
 * description:
 */
public class Lock {

    static final Counter counter = new Counter();

    static {
        Lg.tag("Lock");
        Lg.showMore(true);
    }

    public static void main(String[] args) {
        // 悲观锁: 认为资源随时可能会被修改, 访问之前加锁, 使用结束后解锁
        //      实现方式:
        //       - synchronized
        //       - Lock
        Thread t1 = new Thread(() -> {
            synchronized (counter) {
                Lg.d("synchronized in: " + counter.count);
                counter.increase();
                Lg.d("synchronized out: " + counter.count);
            }
        });
        t1.start();

        Thread t2 = new Thread(() -> {
            synchronized (counter) {
                Lg.d("synchronized in: " + counter.count);
                counter.decrease();
                Lg.d("synchronized out: " + counter.count);
            }
        });

        t2.start();

        Counter c2 = new Counter();

        ReentrantLock lock = new ReentrantLock();

        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                lock.lock();
                Lg.d("synchronized in: " + c2.count);
                c2.decrease();
                Lg.d("synchronized out: " + c2.count);
                lock.unlock();
            }
        });
        t3.start();

        Thread t4 = new Thread(new Runnable() {
            @Override
            public void run() {
                lock.lock();
                Lg.d("synchronized in: " + c2.count);
                c2.increase();
                Lg.d("synchronized out: " + c2.count);
                lock.unlock();
            }
        });
        t4.start();


        try {
            t1.join();
            t2.join();
            t3.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        //https://tech.meituan.com/2018/11/15/java-lock.html
//        通过调用方式示例，我们可以发现悲观锁基本都是在显式的锁定之后再操作同步资源，而乐观锁则直接去操作同步资源。那么，为何乐观锁能够做到不锁定同步资源也可以正确的实现线程同步呢？我们通过介绍乐观锁的主要实现方式 “CAS” 的技术原理来为大家解惑。
//
//        CAS全称 Compare And Swap（比较与交换），是一种无锁算法。在不使用锁（没有线程被阻塞）的情况下实现多线程之间的变量同步。java.util.concurrent包中的原子类就是通过CAS来实现了乐观锁。
//
//        CAS算法涉及到三个操作数：
//
//        需要读写的内存值 V。
//        进行比较的值 A。
//        要写入的新值 B。
//        当且仅当 V 的值等于 A 时，CAS通过原子方式用新值B来更新V的值（“比较+更新”整体是一个原子操作），否则不会执行任何操作。一般情况下，“更新”是一个不断重试的操作。
//
//        之前提到java.util.concurrent包中的原子类，就是通过CAS来实现了乐观锁，那么我们进入原子类AtomicInteger的源码，看一下AtomicInteger的定义：
// ------------------------- 乐观锁的调用方式 -------------------------
        AtomicInteger atomicInteger = new AtomicInteger();  // 需要保证多个线程使用的是同一个AtomicInteger
        atomicInteger.incrementAndGet(); //执行自增1


        // 自旋锁 VS 适应性自旋锁
        // see this: https://awps-assets.meituan.net/mit-x/blog-images-bundle-2018b/452a3363.png
//        3. 无锁 VS 偏向锁 VS 轻量级锁 VS 重量级锁

//        synchronized通过Monitor来实现线程同步，Monitor是依赖于底层的操作系统的Mutex Lock（互斥锁）来实现的线程同步。

//        new ReentrantLock()

//        可重入锁 VS 非可重入锁
//        6. 独享锁 VS 共享锁

        // java.lang.invoke vs java.lang.reflect
        MethodHandles.Lookup lookup = MethodHandles.lookup();

//        private Map<String, ClassLoader.NativeLibrary> nativeLibraries()
        try {

            Method nativeLibraries = ClassLoader.class.getDeclaredMethod("nativeLibraries");
            nativeLibraries.setAccessible(true);

            MethodType mt = MethodType.methodType(Map.class);
            MethodHandle handle = lookup.findVirtual(ClassLoader.class,
                    "nativeLibraries", mt);

            Map<String, Object> maps =
                    (Map<String, Object>) handle.invoke(ClassLoader.getSystemClassLoader(), handle);

            for(String key: maps.keySet()) {
                Lg.d("key: %s - value: %s", key, maps.get(key));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    static class Counter {
        private int count;

        public int count() {
            return count;
        }

        public void increase() {
            count++;
        }

        public void decrease() {
            count--;
        }
    }
}
