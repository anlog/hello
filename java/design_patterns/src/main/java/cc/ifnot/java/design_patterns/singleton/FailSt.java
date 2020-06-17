package cc.ifnot.java.design_patterns.singleton;

public class FailSt {
    private static FailSt mInstance;

    private FailSt() {
    }

    public static FailSt getInstance() {
        if (mInstance == null) {
            mInstance = new FailSt();
        }
        return mInstance;
    }
}
