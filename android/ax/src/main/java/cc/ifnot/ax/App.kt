package cc.ifnot.ax

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.*
import android.view.Choreographer
import android.view.View
import android.view.ViewGroup
import cc.ifnot.ax.utils.*
import cc.ifnot.libs.utils.Lg
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

    //    private val _binder: IBinder = binder
//    private val _proxy: Any = proxy
    private val _mH = mH

    private val _frameCallBack
            by lazy {
                object : Choreographer.FrameCallback {
                    private var _frameCount = 0;
                    private var _frameTimeNanos = System.nanoTime()
                    override fun doFrame(frameTimeNanos: Long) {
                        val diff = frameTimeNanos - _frameTimeNanos
//                        Lg.d("_frameCallBack: %sms fps: %s",
//                                diff / 1000_000L, 1000_000_000L / diff)
                        _frameTimeNanos = frameTimeNanos
                        Choreographer.getInstance().postFrameCallback(this)
                    }
                }
            }

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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    hookATMS()
                }
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
                Lg.d("mH: codeToString=== %s", getMethod(mH::class.java, "codeToString", arrayOf(Int::class.java)))
                Lg.d("mH: codeToString test: %s", invoke(mH::class.java, mH, "codeToString", arrayOf(159)))

//                for (m in mH::class.java.declaredMethods) Lg.d("==== %s", m)
                mH.looper.setMessageLogging { x -> Lg.d("mH: %s", x) }

                setField(Handler::class.java, mH, "mCallback", false, object : Handler.Callback {
                    private val EXECUTE_TRANSACTION = 159

                    @SuppressLint("PrivateApi")
                    override fun handleMessage(msg: Message): Boolean {
                        Lg.d("====message====> what: %s msg: %s",
                                invoke(mH::class.java, mH, "codeToString", arrayOf(msg.what)), msg)

                        when (msg.what) {
                            EXECUTE_TRANSACTION -> {

                                val ClientTransactionClz = Class.forName("android.app.servertransaction.ClientTransaction")
                                val LaunchActivityItemClz = Class.forName("android.app.servertransaction.LaunchActivityItem")

                                val mActivityCallbacksField = ClientTransactionClz.getDeclaredField("mActivityCallbacks") //ClientTransaction的成员

                                mActivityCallbacksField.isAccessible = true
                                //类型判定，好习惯
                                //类型判定，好习惯
                                if (!ClientTransactionClz.isInstance(msg.obj)) return true
                                val mActivityCallbacksObj: Any? = mActivityCallbacksField.get(msg.obj) //根据源码，在这个分支里面,msg.obj就是 ClientTransaction类型,所以，直接用

                                //拿到了ClientTransaction的List<ClientTransactionItem> mActivityCallbacks;
                                //拿到了ClientTransaction的List<ClientTransactionItem> mActivityCallbacks;
                                val list = mActivityCallbacksObj as List<*>

                                if (list.size == 0) return false
                                val LaunchActivityItemObj = list[0]!! //所以这里直接就拿到第一个就好了


                                if (!LaunchActivityItemClz.isInstance(LaunchActivityItemObj)) return true
                                //这里必须判定 LaunchActivityItemClz，
                                // 因为 最初的ActivityResultItem传进去之后都被转化成了这LaunchActivityItemClz的实例

                                //这里必须判定 LaunchActivityItemClz，
                                // 因为 最初的ActivityResultItem传进去之后都被转化成了这LaunchActivityItemClz的实例
                                val mIntentField = LaunchActivityItemClz.getDeclaredField("mIntent")
                                mIntentField.setAccessible(true)

                                val extras = (mIntentField.get(LaunchActivityItemObj) as Intent).extras
                                if (extras != null) {
                                    val oriIntent = extras.getParcelable(stub) as Intent?
                                    //那么现在有了最原始的intent，应该怎么处理呢？
                                    if (oriIntent != null) {
                                        Lg.d("revert it: $oriIntent")
                                        mIntentField.set(LaunchActivityItemObj, oriIntent)
                                    }
                                }
                            }
                            else -> {
                                return false
                            }
                        }
                        return false // just hook here, go on Handler.handleMessage
                    }
                })
//                  2020-07-16 21:39:31.166 14001-14001/?
//                  W/System.err: java.lang.IllegalArgumentException:
//                  field android.app.ActivityThread.mH has type android.app.ActivityThread$H, got cc.ifnot.ax.utils.ATHandler
//                getField(clz, "mH").set(at, ATHandler(mH))

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
//            try {
            // dipper:/ # cat /system/etc/preloaded-classes  |grep ActivityManager
            // android.app.ActivityManager
            // preloaded classes; no way to change it's default
            // but will work after this
//                @SuppressLint("PrivateApi")
//                val clz = Class.forName("android.os.ServiceManager")
//                val cachedField = clz.getDeclaredField("sCache")
//                cachedField.isAccessible = true
//                @Suppress("UNCHECKED_CAST")
//                val cached = (cachedField.get(null)
//                        as MutableMap<String, IBinder>)
//                val entries = cached.entries
//                for (e in entries) {
//                    Lg.d("service: %s = %s", e.key, e.value)
//                    Lg.d(e.value.javaClass.name)
//                }
////            val a = IBinder::class.java
//
//                class Handle(private val binder: IBinder) : InvocationHandler {
//                    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any?>?): Any? {
//                        Lg.d("invoke: %s - %s -%s", binder, method?.name, args)
//                        return if (args.isNullOrEmpty()) method?.invoke(binder, args) else method?.invoke(binder, *args)
//                    }
//                }
//
//                val m = clz.getMethod("getService", String::class.java)
//                binder = m.invoke(null, Context.ACTIVITY_SERVICE) as IBinder
//
//                proxy = Proxy.newProxyInstance(clz.classLoader, arrayOf(IBinder::class.java),
//                        Handle(binder))
//                cached[Context.ACTIVITY_SERVICE] = proxy as IBinder
//                cachedField.set(null, cached)
//                cachedField.isAccessible = false
//
//                Lg.d("binder: %s", cached[Context.ACTIVITY_SERVICE])
//                val iBinder = cached[Context.ACTIVITY_SERVICE] as IBinder
//                iBinder.pingBinder()
//            val systemService = getSystemService(Context.ACTIVITY_SERVICE)
//                Lg.d("binder: %s -- %s", iBinder, null)
//            cached.set(Context.ACTIVITY_SERVICE, null)
//            cached.set()
//            } catch (e: ClassNotFoundException) {
//            }
        }
    }


    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
//        Choreographer.getInstance().postFrameCallback(_frameCallBack)
        Lg.d("attachBaseContext: %s", base)

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(Intent(this, WindowService::class.java))
//            startForegroundService(Intent(this, ClientService::class.java))
//        } else {
//            startService(Intent(this, WindowService::class.java))
//            startService(Intent(this, ClientService::class.java))
//        }

        val systemService = getSystemService(Context.ACTIVITY_SERVICE)
        Lg.d(systemService)

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

//        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
//        val runningTasks = am.getRunningTasks(100)

    }

    override fun onCreate() {
        super.onCreate()
        Lg.d("onCreate")
        registerActivityLifecycleCallbacks(lifecycleCallBack)
//        _executors.apply { execute { Debug.printLoadedClasses(Debug.SHOW_CLASSLOADER.or(Debug.SHOW_INITIALIZED)) } }
    }

    override fun onTerminate() {
        super.onTerminate()
        Choreographer.getInstance().removeFrameCallback(_frameCallBack)
        unregisterActivityLifecycleCallbacks(lifecycleCallBack)
    }
}
