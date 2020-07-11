package cc.ifnot.app.jni;

import cc.ifnot.app.BuildConfig;
import cc.ifnot.libs.utils.Lg;

public final class GoJniWrap {

    static {
        System.loadLibrary("go_jni_" + BuildConfig.VERSION);
        Lg.d("go_jni_%s loaded", BuildConfig.VERSION);
    }

    public static native int go_jni_add(int a, int b);

    public static native String go_jni_hello(String hello);
}
