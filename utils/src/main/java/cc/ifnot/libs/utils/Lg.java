package cc.ifnot.libs.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

public class Lg {
    private static String TAG = "Lg";
    private static boolean init = false;

    private static boolean isAndroid;
    private static boolean more = false;

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

    private static final int BASE = 29;

    private static Method v;
    private static Method vt;
    private static Method d;
    private static Method dt;
    private static Method i;
    private static Method it;
    private static Method w;
    private static Method wt;
    private static Method e;
    private static Method et;

    static {
        try {
            Class.forName("android.view.View");
            isAndroid = true;

            Class<?> clz = Class.forName("android.util.Log");
            v = clz.getDeclaredMethod("v", String.class, String.class);
            vt = clz.getDeclaredMethod("v", String.class, String.class, Throwable.class);
            d = clz.getDeclaredMethod("d", String.class, String.class);
            dt = clz.getDeclaredMethod("d", String.class, String.class, Throwable.class);
            i = clz.getDeclaredMethod("i", String.class, String.class);
            it = clz.getDeclaredMethod("i", String.class, String.class, Throwable.class);
            w = clz.getDeclaredMethod("w", String.class, String.class);
            wt = clz.getDeclaredMethod("w", String.class, String.class, Throwable.class);
            e = clz.getDeclaredMethod("e", String.class, String.class);
            et = clz.getDeclaredMethod("e", String.class, String.class, Throwable.class);
        } catch (ClassNotFoundException | NoSuchMethodException ignored) {
            if (more) f(ignored, ERROR + BASE);
            isAndroid = false;
        }
    }


    public static void o(Object msg, int level) {
        if (msg instanceof Throwable) {
            if (isAndroid) {
                try {
                    switch (level) {
                        case VERBOSE:
                            vt.invoke(null, TAG, "", msg);
                        case DEBUG:
                            dt.invoke(null, TAG, "", msg);
                            break;
                        case INFO:
                            it.invoke(null, TAG, "", msg);
                            break;
                        default:
                        case WARN:
                            wt.invoke(null, TAG, "", msg);
                            break;
                        case ERROR:
                            et.invoke(null, TAG, "", msg);
                            break;
                    }
                } catch (IllegalAccessException | InvocationTargetException ignored) {
                    if (more) f(ignored, WARN + BASE);
                    f(msg, WARN + BASE);
                }
            } else {
                f(msg, level + BASE);
            }
        } else {
            Thread thread = Thread.currentThread();
            StackTraceElement[] stacks = thread.getStackTrace();
            StackTraceElement stack = stacks[1];
            for (StackTraceElement s : stacks) {
                if (!Thread.class.getName().equals(s.getClassName())
                        && !"dalvik.system.VMStack".equals(s.getClassName())
                        && !Lg.class.getName().equals(s.getClassName())) {
                    stack = s;
                    break;
                }
            }

            msg = more ?
                    String.format(Locale.getDefault(), "%s[%s:%d]: %s",
                            stack.getFileName() == null ? "Anonymous" :
                                    stack.getFileName().replace(".java", ""),
                            stack.getMethodName(), stack.getLineNumber(), msg) :
                    String.format(Locale.getDefault(), "%s: %s", stack.getClassName(), msg);

            if (isAndroid) {
                try {
                    switch (level) {
                        case VERBOSE:
                            v.invoke(null, TAG, msg);
                            break;
                        case DEBUG:
                            d.invoke(null, TAG, msg);
                            break;
                        case INFO:
                            i.invoke(null, TAG, msg);
                            break;
                        default:
                        case WARN:
                            w.invoke(null, TAG, msg);
                            break;
                        case ERROR:
                            e.invoke(null, TAG, msg);
                            break;
                    }
                } catch (IllegalAccessException | InvocationTargetException ignored) {
                    if (more) f(ignored, WARN + BASE);
                    f(msg, level);
                }
            } else {
                f(msg, level);
            }
        }
    }

    public static void f(Object msg, int level) {
        if (msg instanceof Throwable) {
            // todo: impl stack print
            ((Throwable) msg).printStackTrace();
        } else {
            level = level + BASE;
            String console_prefix = level > 0 ?
                    String.format(Locale.getDefault(), "\033[%d;3m", level) : "";
            String console_suffix = level > 0 ?
                    "\033[0m" : "";
            System.out.println(String.format(Locale.getDefault(),
                    "%s%s: %s%s", console_prefix, TAG, msg, console_suffix));
        }
    }

    public synchronized static void tag(String tag) {
        if (init) {
            o("tag is already inited", WARN);
            return;
        }
        TAG = tag;
        init = true;
    }

    public static String tag() {
        return TAG;
    }

    public static boolean showMore() {
        return more;
    }

    public static void showMore(boolean more) {
        Lg.more = more;
    }

    private static void wrap(int level, String format, Object... msg) {
        List<Object> logs = new ArrayList<>();
        Collections.addAll(logs, msg);
        List<Throwable> throwables = new ArrayList<>();
        for (Object o : msg) {
            if (o instanceof Throwable) {
                throwables.add((Throwable) o);
                logs.remove(o);
            }
        }

        o(new Formatter().format(format, logs.toArray()).toString(), INFO);
        for (Throwable t : throwables) {
            o(t, INFO);
        }
    }

    public static void i(String format, Object... msg) {
        wrap(INFO, format, msg);
    }

    public static void v(String format, Object... msg) {
        wrap(VERBOSE, format, msg);
    }

    public static void d(String format, Object... msg) {
        wrap(DEBUG, format, msg);
        o(new Formatter().format(format, msg).toString(), DEBUG);
    }

    public static void w(String format, Object... msg) {
        wrap(WARN, format, msg);
    }

    public static void e(String format, Object... msg) {
        wrap(ERROR, format, msg);
    }
}
