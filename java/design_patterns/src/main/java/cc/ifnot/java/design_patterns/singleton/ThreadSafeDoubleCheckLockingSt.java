package cc.ifnot.java.design_patterns.singleton;

public class ThreadSafeDoubleCheckLockingSt {
    private static ThreadSafeDoubleCheckLockingSt mInstance;

    private ThreadSafeDoubleCheckLockingSt() {
    }

    public static ThreadSafeDoubleCheckLockingSt getInstance() {
        if (mInstance == null) {
            synchronized (ThreadSafeDoubleCheckLockingSt.class) {
                if (mInstance == null) {
                    mInstance = new ThreadSafeDoubleCheckLockingSt();
                }
            }
        }
        return mInstance;
    }
}
