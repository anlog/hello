package cc.ifnot.app.jni;

import cc.ifnot.app.BuildConfig;

public final class GoJniWrap {

    static {
        System.loadLibrary("go_jni_" + BuildConfig.VERSION);
    }

    public static native int go_jni_add(int a, int b);

    public static native String go_jni_hello(String hello);
}
