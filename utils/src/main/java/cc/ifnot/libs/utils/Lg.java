package cc.ifnot.libs.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Formatter;
import java.util.Locale;

public class Lg {
    private static String TAG = "LOG";

    private static boolean isAndroid = false;

    static {
        try {
            Class.forName("android.view.View");
            isAndroid = true;
        } catch (ClassNotFoundException e) {
            isAndroid = false;
        }
    }

    /**
     * Priority constant for the println method; use Log.v.
     */
    private static final int VERBOSE = 2;

    /**
     * Priority constant for the println method; use Log.d.
     */
    private static final int DEBUG = 3;

    /**
     * Priority constant for the println method; use Log.i.
     */
    private static final int INFO = 4;

    /**
     * Priority constant for the println method; use Log.w.
     */
    private static final int WARN = 5;

    /**
     * Priority constant for the println method; use Log.e.
     */
    private static final int ERROR = 6;

    /**
     * Priority constant for the println method.
     */
    private static final int ASSERT = 7;

    public static void o(String msg, int level) {

        if (isAndroid) {
            try {
                Class<?> clz = Class.forName("android.util.Log");
                switch (level) {
                    case VERBOSE:
                        Method v = clz.getDeclaredMethod("v", String.class, String.class);
                        v.invoke(null, TAG, msg);
                        break;
                    case DEBUG:
                        Method d = clz.getDeclaredMethod("d", String.class, String.class);
                        d.invoke(null, TAG, msg);
                        break;
                    case INFO:
                        Method i = clz.getDeclaredMethod("i", String.class, String.class);
                        i.invoke(null, TAG, msg);
                        break;
                    default:
                    case WARN:
                        Method w = clz.getDeclaredMethod("w", String.class, String.class);
                        w.invoke(null, TAG, msg);
                        break;
                    case ERROR:
                        Method e = clz.getDeclaredMethod("e", String.class, String.class);
                        e.invoke(null, TAG, msg);
                        break;
                }
            } catch (ClassNotFoundException | NoSuchMethodException |
                    IllegalAccessException | InvocationTargetException ignored) {
                ignored.printStackTrace();
                f(msg, level);
            }
        } else {
            f(msg, level);
        }
    }

    public static void f(String msg, int level) {
        level = level + 29;
        String console_prefix = level > 0 ?
                String.format(Locale.getDefault(), "\033[%d;3m", level) : "";
        String console_suffix = level > 0 ?
                "\033[0m" : "";
        System.out.println(String.format(Locale.getDefault(),
                "%s%s: %s%s", console_prefix, TAG, msg, console_suffix));
    }

    public static void tag(String tag) {
        TAG = tag;
    }

    public static void i(String format, Object... msg) {
        o(new Formatter().format(format, msg).toString(), INFO);
    }

    public static void v(String format, Object... msg) {
        o(new Formatter().format(format, msg).toString(), VERBOSE);
    }

    public static void d(String format, Object... msg) {
        o(new Formatter().format(format, msg).toString(), DEBUG);
    }

    public static void w(String format, Object... msg) {
        o(new Formatter().format(format, msg).toString(), WARN);
    }

    public static void e(String format, Object... msg) {
        o(new Formatter().format(format, msg).toString(), ERROR);
    }
}
