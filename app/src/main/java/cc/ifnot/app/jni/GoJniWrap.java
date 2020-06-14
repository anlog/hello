package cc.ifnot.app.jni;

public final class GoJniWrap {

    public static native int go_jni_add(int a, int b);

    public static native String go_jni_hello(String hello);
}
