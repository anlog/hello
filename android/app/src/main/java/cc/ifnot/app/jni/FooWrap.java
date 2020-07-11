package cc.ifnot.app.jni;

import android.os.IBinder;

import cc.ifnot.libs.utils.Lg;

public class FooWrap {

    static {
        System.loadLibrary("foo");
        Lg.d("foo loaded");
    }

    public static native int add(int a, int b);

    public static native IBinder binder(String name);
}
