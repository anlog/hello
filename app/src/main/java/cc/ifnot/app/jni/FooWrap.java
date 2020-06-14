package cc.ifnot.app.jni;

import android.os.IBinder;

public class FooWrap {

    static {
        System.loadLibrary("foo");
    }

    public static native int add(int a, int b);

    public static native IBinder binder(String name);
}
