package cc.ifnot.ax

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.IBinder
import cc.ifnot.libs.utils.Lg
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicLong

class App : Application() {

    private val _binder: IBinder = binder
    private val _proxy: Any = proxy


    companion object {
        private lateinit var binder: IBinder
        private lateinit var proxy: Any

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
                    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any? {
                        Lg.d("invoke: %s - %s -%s", binder, method?.name, args)
                        return if (args != null) method?.invoke(binder, args) else method?.invoke(binder)
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

    init {
        Lg.tag("ax")
        Lg.level(Lg.MORE)
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
}
