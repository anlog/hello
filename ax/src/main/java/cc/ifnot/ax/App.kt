package cc.ifnot.ax

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.util.LogPrinter
import android.view.View
import android.view.ViewGroup
import cc.ifnot.ax.utils.getField
import cc.ifnot.ax.utils.hookAMS
import cc.ifnot.libs.utils.Lg
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicLong

class App : Application() {

    private val _binder: IBinder = binder
    private val _proxy: Any = proxy
    private val _mH = mH

    private val lifecycleCallBack
            by lazy {
                object : ActivityLifecycleCallbacks {
                    override fun onActivityPaused(activity: Activity) {
                        Lg.d("onActivityPaused: %s", activity)
                    }

                    override fun onActivityStarted(activity: Activity) {
                        Lg.d("onActivityStarted: %s", activity)
                    }

                    override fun onActivityDestroyed(activity: Activity) {
                        Lg.d("onActivityDestroyed: %s", activity)
                    }

                    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                        Lg.d("onActivitySaveInstanceState: %s", activity)
                    }

                    override fun onActivityStopped(activity: Activity) {
                        Lg.d("onActivityStopped: %s", activity)
                    }

                    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                        Lg.d("onActivityCreated: %s", activity)
                    }

                    override fun onActivityResumed(activity: Activity) {
                        Lg.d("onActivityResumed: %s", activity)

                        activity.window.decorView.findViewById<ViewGroup>(android.R.id.content).apply {
                            Lg.d("resumed %s -> hook child %d onClick", activity, childCount)
                            when (childCount) {
                                0 -> Lg.d("no child found, improbable")
                                else -> {
                                    doHookClick(this)
                                }
                            }

                        }
                    }

                }
            }

    private fun doHookClick(vg: ViewGroup) {
        val childCount = vg.childCount
        for (i in 0 until childCount) {
            val view = vg.getChildAt(i)
            Lg.d("child view: %s", view)
            when (view) {
                is ViewGroup -> doHookClick(view)
                is View -> if (view.isClickable and view.hasOnClickListeners()) {
                    val f = View::class.java.getDeclaredField("mListenerInfo")
                    f.isAccessible = true
                    val l = f.get(view)
                    f.isAccessible = false
                    val ff = l.javaClass.getDeclaredField("mOnClickListener")
                    val ll = ff.get(l) as View.OnClickListener
                    view.setOnClickListener { v ->
                        Lg.d("hook OnClickListener: %s", v)
                        ll.onClick(v)
                    }
                }
                else -> Lg.d("impossible")
            }

        }

    }


    companion object {
        private lateinit var binder: IBinder
        private lateinit var proxy: Any
        private lateinit var mH: Handler

        init {
            Lg.tag("ax")
            Lg.level(Lg.MORE)
        }

        init {
            try {
                Lg.d("hook AMS")
                hookAMS()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        init {
            // ActivityThread hook
            try {
                @SuppressLint("PrivateApi")
                val clz = Class.forName("android.app.ActivityThread")
                val at = getField(clz, null, "sCurrentActivityThread")
                mH = getField(clz, at, "mH") as Handler
                mH.looper.setMessageLogging(LogPrinter(Log.DEBUG, "AT"))
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        init {
            try {
                // dipper:/ # cat /system/etc/preloaded-classes  |grep ActivityManager
                // android.app.ActivityManager
                // preloaded classes; no way to change it's default
                // but will work after this
                @SuppressLint("PrivateApi")
                val clz = Class.forName("android.os.ServiceManager")
                val cachedField = clz.getDeclaredField("sCache")
                cachedField.isAccessible = true
                @Suppress("UNCHECKED_CAST")
                val cached = (cachedField.get(null)
                        as MutableMap<String, IBinder>)
                val entries = cached.entries
                for (e in entries) {
                    Lg.d("service: %s = %s", e.key, e.value)
                    Lg.d(e.value.javaClass.name)
                }
//            val a = IBinder::class.java

                class Handle(private val binder: IBinder) : InvocationHandler {
                    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any?>): Any? {
                        Lg.d("invoke: %s - %s -%s", binder, method?.name, args)
                        return if (args.isNullOrEmpty()) method?.invoke(binder) else method?.invoke(binder, *args)
                    }
                }

                val m = clz.getMethod("getService", String::class.java)
                binder = m.invoke(null, Context.ACTIVITY_SERVICE) as IBinder

                proxy = Proxy.newProxyInstance(clz.classLoader, arrayOf(IBinder::class.java),
                        Handle(binder))
                cached[Context.ACTIVITY_SERVICE] = proxy as IBinder
                cachedField.set(null, cached)
                cachedField.isAccessible = false

                Lg.d("binder: %s", cached[Context.ACTIVITY_SERVICE])
                val iBinder = cached[Context.ACTIVITY_SERVICE] as IBinder
                iBinder.pingBinder()
//            val systemService = getSystemService(Context.ACTIVITY_SERVICE)
                Lg.d("binder: %s -- %s", iBinder, null)
//            cached.set(Context.ACTIVITY_SERVICE, null)
//            cached.set()
            } catch (e: ClassNotFoundException) {
            }
        }
    }

    private val _prefix = "pool"
    private val executors
            by lazy {
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(),
                        ThreadFactory(_prefix))
            }

    inner class ThreadFactory(private val prefix: String) : AtomicLong(),
            java.util.concurrent.ThreadFactory {

        override fun newThread(r: Runnable?): Thread {
            return Thread({ r?.run() },
                    StringBuilder(prefix).append("_").append(andIncrement).toString())
        }

        override fun toByte(): Byte {
            return get().toByte()
        }

        override fun toChar(): Char {
            return get().toChar()
        }

        override fun toShort(): Short {
            return get().toShort()
        }

    }


    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        Lg.d("attachBaseContext: %s", base)
        val systemService = getSystemService(Context.ACTIVITY_SERVICE)
        Lg.d(systemService)
        Lg.d(binder)

        @SuppressLint("PrivateApi")
        val clz = Class.forName("android.os.ServiceManager")
        val cachedField = clz.getDeclaredField("sCache")
        cachedField.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val cached = (cachedField.get(null)
                as MutableMap<String, IBinder>)
        val entries = cached.entries
        for (e in entries) {
            Lg.d("service: %s = %s", e.key, e.value)
            Lg.d(e.value.javaClass.name)
        }

        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningTasks = am.getRunningTasks(100)

        Lg.d(proxy)
    }

    override fun onCreate() {
        super.onCreate()
        Lg.d("onCreate")
        registerActivityLifecycleCallbacks(lifecycleCallBack)
    }

    override fun onTerminate() {
        super.onTerminate()
        unregisterActivityLifecycleCallbacks(lifecycleCallBack)
    }
}
