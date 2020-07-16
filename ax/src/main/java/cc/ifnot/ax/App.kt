package cc.ifnot.ax

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.view.View
import android.view.ViewGroup
import cc.ifnot.ax.utils.bypass
import cc.ifnot.ax.utils.getField
import cc.ifnot.ax.utils.hookAMS
import cc.ifnot.ax.utils.setField
import cc.ifnot.libs.utils.Lg
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicLong

class App : Application() {

    private val ViewHook: Int = "view_hook".foldRightIndexed(0,
            { i, c, sum -> sum + c.toInt().shl(i) }).or(0xf.shl(24))

    private val _prefix = "pool"
    private val _executors
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

    init {

        _executors.apply {
//            execute { Debug.startMethodTracing() }
//            execute { Debug.startNativeTracing() }

        }

    }

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
                            Lg.d("view: resumed %s -> hook child %d onClick", activity, childCount)
                            when (childCount) {
                                0 -> Lg.d("view: no child found, improbable")
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
            Lg.d("view: child %s", view)
            when (view) {
                is ViewGroup -> doHookClick(view)
                is View -> if (view.isClickable and view.hasOnClickListeners()) {
                    val f = View::class.java.getDeclaredField("mListenerInfo")
                    f.isAccessible = true
                    val l = f.get(view)
                    f.isAccessible = false
                    val ff = l.javaClass.getDeclaredField("mOnClickListener")
                    val ll = ff.get(l) as View.OnClickListener
                    Lg.d("view: tag: %s", view.getTag(ViewHook))
                    if (view.getTag(ViewHook) == null) { // not hook, do hook
                        Lg.d("view: hooked %s (tag: %s - ll: %s)", view, view.tag, ll)
                        view.setTag(ViewHook, ll) // save original listener to tag, so we can mark them hooked
                        view.setOnClickListener { v ->
                            Lg.d("view: hooked OnClickListener: %s", v)
                            ll.onClick(v)
                        }
                    }
                }
                else -> Lg.d("view: impossible")
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
                bypass()
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
                // https://cs.android.com/android/platform/superproject/+/master:frameworks/base/core/java/android/app/ActivityThread.java;l=225?q=ActivityThread&ss=android%2Fplatform%2Fsuperproject
//                setField(clz, at, " DEBUG_MESSAGES", final = true, t = true)
//                val f = clz.getDeclaredField("DEBUG_MESSAGES")
//                Lg.d("---------- %s", f)

                mH = getField(clz, at, "mH") as Handler
                mH.looper.setMessageLogging { x -> Lg.d("mH: %s", x) }

                setField(Handler::class.java, mH, "mCallback", false, object : Handler.Callback {
                    override fun handleMessage(msg: Message): Boolean {
                        Lg.d("====message====> what: %s msg: %s", msg.what, msg)
                        return false // just hook here, go on Handler.handleMessage
                    }
                })

            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                val mq = Class.forName("android.os.MessageQueue")
                setField(mq, mH.looper.queue, "DEBUG", final = true, t = true)
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
                    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any?>?): Any? {
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
//        _executors.apply { execute { Debug.printLoadedClasses(Debug.SHOW_CLASSLOADER.or(Debug.SHOW_INITIALIZED)) } }
    }

    override fun onTerminate() {
        super.onTerminate()
        unregisterActivityLifecycleCallbacks(lifecycleCallBack)
    }
}
