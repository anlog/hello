@file:JvmName("-reflect")

package cc.ifnot.ax.utils

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.*
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import cc.ifnot.libs.utils.Lg
import java.lang.reflect.*

/**
 * author: dp
 * created on: 2020/7/15 4:27 PM
 * description:
 */

@Throws
fun bypass() {
    val forName = Class::class.java.getDeclaredMethod("forName", String::class.java)
    val getDeclaredMethod = Class::class.java.getDeclaredMethod("getDeclaredMethod", String::class.java, arrayOf<Class<*>>()::class.java)

    val vmRuntimeClass = forName.invoke(null, "dalvik.system.VMRuntime") as Class<*>
    val getRuntime = getDeclaredMethod.invoke(vmRuntimeClass, "getRuntime", null) as Method
    val setHiddenApiExemptions = getDeclaredMethod.invoke(vmRuntimeClass, "setHiddenApiExemptions", arrayOf(arrayOf<String>()::class.java)) as Method

    val vmRuntime = getRuntime.invoke(null)

    setHiddenApiExemptions.invoke(vmRuntime, arrayOf("L"))
}

@Throws
fun greyListCompat(clz: Class<*>): Class<*> {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val c = Class::class.java
        setField(c, clz, "classLoader", false, null)
    }
    return clz
}

// static proxy
class ATHandler(private val h: Handler) : Handler() {

    override fun dispatchMessage(msg: Message) {
        Lg.d("ATHandler: msg -> %s", msg)
        super.dispatchMessage(msg)
    }

    override fun handleMessage(msg: Message) {
        Lg.d("====message(mH)====> what: %s msg: %s",
                invoke(h::class.java, h, "codeToString", arrayOf(msg.what)), msg)
        when (msg.what) {
            100 -> {
                val r = msg.obj
                try {
                    val it = getField(r::class.java, r, "intent") as Intent
                    if (it.action == stubAction) {
                        val original = it.getParcelableExtra<Intent>(stub) as Intent
                        it.component = original.component
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                h.handleMessage(msg)
            }
            else -> h.handleMessage(msg)
        }

    }
}

@SuppressLint("PrivateApi")
@Throws()
fun hookATMS() {
    val clz = Class.forName("android.app.ActivityTaskManager")
    val atm = getField(clz, null, "IActivityTaskManagerSingleton")
    val singleton = Class.forName("android.util.Singleton")
    val atmsField = getField(singleton, "mInstance")
    atmsField.isAccessible = true
    var ibinder: IBinder? = null
    var atms: Any?
    try {
        atms = atmsField.get(null)
    } catch (_: NullPointerException) {
        @SuppressLint("PrivateApi")
        val cl = Class.forName("android.os.ServiceManager")
        val cachedField = cl.getDeclaredField("sCache")
        cachedField.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val cached = (cachedField.get(null)
                as MutableMap<String, IBinder>)
        val ACTIVITY_TASK_SERVICE = "activity_task"
        ibinder = cached[ACTIVITY_TASK_SERVICE]
        if (ibinder == null) {
            // must not be null here
            val m = cl.getMethod("getService", String::class.java)
            ibinder = m.invoke(null, ACTIVITY_TASK_SERVICE) as IBinder
        }

        val iatm = Class.forName("android.app.IActivityTaskManager\$Stub")
        atms = invoke(iatm, null, "asInterface", arrayOf(ibinder))
    }

    val proxy = Proxy.newProxyInstance(Thread.currentThread().contextClassLoader,
            arrayOf(Class.forName("android.app.IActivityTaskManager")),
            IActivityTaskManagerProxy(atms!!))
    atmsField.set(atm, proxy)

    val ibField = getField(atms::class.java, "mRemote")
    ibField.isAccessible = true
    if (ibinder == null) {
        ibinder = ibField.get(atms) as IBinder
    }

//    val ib = getField(ams::class.java, ams, "mRemote") as IBinder
    val ibProxy = Proxy.newProxyInstance(Thread.currentThread().contextClassLoader,
            arrayOf(IBinder::class.java),
            IBinderProxy("ATMS", ibinder))
    ibField.set(atms, ibProxy)
}

class IActivityTaskManagerProxy(@NonNull private val atm: Any) : InvocationHandler {
    override fun invoke(proxy: Any?, method: Method?, args: Array<Any?>?): Any? {
        Lg.d("======proxy======")
        Lg.d("IActivityManagerTaskProxy: %s -> %s", method?.toPretty(), arrayOf(args))
        Lg.d("====== end ======")

        return if (method == null) throw IllegalStateException("method must not be null")
        else {
            when (method.name) {
                "startActivity" -> if (!args.isNullOrEmpty()) {
                    var i = 0
                    while (args[i] !is Intent) {
                        if (++i > args.size - 1) break
                    }
                    val it = args[i] as Intent
                    Lg.d("IActivityManagerTaskProxy: startActivity %s", it)
                    if (stubAction == it.action) {
                        args[i] = Intent().apply {
                            component = ComponentName("cc.ifnot.ax", "cc.ifnot.ax.StubActivity")
                            putExtra(stub, it)
                        }
                        Lg.d("IActivityManagerTaskProxy: hook to -> %s", args[i])
                    }
                    method.invoke(atm, *args)
                } else method.invoke(atm, *(args ?: arrayOfNulls(0)))
                else -> method.invoke(atm, *(args ?: arrayOfNulls(0)))

            }
        }
    }
}

@SuppressLint("PrivateApi")
@Throws()
fun hookAMS() {
    val am = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val clz = Class.forName("android.app.ActivityManager")
        getField(clz, null, "IActivityManagerSingleton")
    } else {
        val clz = Class.forName("android.app.ActivityManagerProxy")
        getField(clz, null, "gDefault")
    }

    val singleton = Class.forName("android.util.Singleton")
    val amsField = getField(singleton, "mInstance")
    amsField.isAccessible = true
    var ams: Any?
    var ibinder: IBinder? = null
    try {
        ams = amsField.get(null)
    } catch (_: NullPointerException) {
        @SuppressLint("PrivateApi")
        val cl = Class.forName("android.os.ServiceManager")
        val cachedField = cl.getDeclaredField("sCache")
        cachedField.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val cached = (cachedField.get(null)
                as MutableMap<String, IBinder>)
        ibinder = cached[Context.ACTIVITY_SERVICE]
        if (ibinder == null) {
            val m = cl.getMethod("getService", String::class.java)
            ibinder = m.invoke(null, Context.ACTIVITY_SERVICE) as IBinder
        }
        val iatm = Class.forName("android.app.IActivityManager\$Stub")
        ams = invoke(iatm, null, "asInterface", arrayOf(ibinder))
    }

    val proxy = Proxy.newProxyInstance(Thread.currentThread().contextClassLoader,
            arrayOf(Class.forName("android.app.IActivityManager")),
            IActivityManagerProxy(ams!!))
    amsField.set(am, proxy)

    val ibField = getField(ams::class.java, "mRemote")
    ibField.isAccessible = true
    if (ibinder == null) {
        ibinder = ibField.get(ams) as IBinder
    }

//    val ib = getField(ams::class.java, ams, "mRemote") as IBinder
    val ibProxy = Proxy.newProxyInstance(Thread.currentThread().contextClassLoader,
            arrayOf(IBinder::class.java),
            IBinderProxy("AMS", ibinder))
    ibField.set(ams, ibProxy)

}

class IBinderProxy(private val tag: String, private val ib: IBinder) : InvocationHandler {
    override fun invoke(proxy: Any?, method: Method?, args: Array<Any?>?): Any? {
        Lg.d("======proxy======")
        Lg.d("%s: %s -> %s %s %s %s", tag, method?.toPretty(), args?.get(0),
                args?.get(1), args?.get(2), args?.get(3))
        Lg.d("====== end ======")

        return method!!.invoke(ib, *(args ?: arrayOfNulls(0)))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun Method.toPretty(): String {
    val sb = StringBuffer()
    sb.append(returnType.typeName).append(' ')
    sb.append(declaringClass.typeName).append('.')
    sb.append(name)
    return sb.toString()
}

val stubAction = "cc.ifnot.ax.Stub"
val stub = "cc.ifnot.ax.Stub_"

class IActivityManagerProxy(@NonNull private val am: Any) : InvocationHandler {
    override fun invoke(proxy: Any?, method: Method?, args: Array<Any?>?): Any? {
        Lg.d("======proxy======")
        Lg.d("IActivityManagerProxy: %s -> %s", method?.toPretty(), arrayOf(args))
        Lg.d("====== end ======")

        return when (method) {
            null -> throw IllegalStateException("method must not be null")
            else -> {
                when (method.name) {
                    "startActivity" -> if (!args.isNullOrEmpty()) {
                        Lg.d("IActivityManagerProxy: startActivity called")
                        var i = 0
                        while (args[i] !is Intent) {
                            if (++i > args.size - 1) break
                        }
                        val it = args[i] as Intent
                        Lg.d("IActivityManagerTaskProxy: startActivity %s", it)
                        if (stub == it.action) {
                            args[i] = Intent(Intent.ACTION_VIEW)
                                    .putExtra(stub, it)
                        }
                        method.invoke(am, *args)
                    } else if (args.isNullOrEmpty()) method.invoke(am, args) else method.invoke(am, *args)
                    else -> if (args.isNullOrEmpty()) method.invoke(am, args) else method.invoke(am, *args)
                }
            }
        }
    }

}

fun getMethod(@NonNull clz: Class<*>, @NonNull m: String, @Nullable args: Array<Class<*>?>?): Method {
    Lg.d("reflect: getMethod -> %s - %s - %s", clz, m, args)

    return clz.getDeclaredMethod(m, *(args ?: arrayOfNulls(0)))
}

@Throws
fun invoke(@NonNull clz: Class<*>, @Nullable target: Any?, @NonNull m: String, @Nullable args: Array<Any?>?): Any? {
    Lg.d("reflect: invoke -> %s - %s - %s - %s", clz, target, m, args)
    val mm = if (args == null) {
        getMethod(clz, m, args)
    } else {
        getMethod(clz, m, args.map { o ->
            if (o == null) o else {
                when (o) {
                    //2020-07-16 16:09:03.408 20886-20886/cc.ifnot.ax
                    // W/System.err: java.lang.NoSuchMethodException:
                    // android.app.IActivityTaskManager$Stub.asInterface [class android.os.BinderProxy]
                    is IBinder -> IBinder::class.java
                    is Int -> Int::class.java
                    is Long -> Long::class.java
                    is Byte -> Byte::class.java
                    is Char -> Char::class.java
                    is Float -> Float::class.java
                    is Double -> Double::class.java
                    is Short -> Short::class.java
                    is Boolean -> Boolean::class.java
                    else -> o::class.java
                }
            }
        }.toTypedArray())
    }

    val accessible = mm.isAccessible
    if (!accessible) {
        mm.isAccessible = true
    }
    val ret = mm.invoke(target, *(args ?: arrayOfNulls(0)))

    mm.isAccessible = accessible
    return ret
}

fun getField(@NonNull clz: Class<*>, @NonNull f: String): Field {
    Lg.d("reflect: getField -> %s - %s", clz, f)
    val ff = clz.getDeclaredField(f)
    val accessible = ff.isAccessible
    if (!accessible) {
        ff.isAccessible = true
    }
    ff.isAccessible = accessible
    return ff
}

fun getField(@NonNull clz: Class<*>, @Nullable target: Any?, @NonNull f: String): Any? {
    Lg.d("reflect: getField -> %s - %s - %s", clz, target, f)
    val ff = clz.getDeclaredField(f)
    val accessible = ff.isAccessible
    if (!accessible) {
        ff.isAccessible = true
    }
    val ret = ff.get(target)
    ff.isAccessible = accessible
    return ret
}

fun setField(@NonNull clz: Class<*>, @Nullable target: Any?, @NonNull f: String, final: Boolean, @Nullable t: Any?) {
    Lg.d("reflect: setField -> %s - %s - %s(%s) - %s", clz, target, f, final, t)
    val ff = clz.getDeclaredField(f)
    val accessible = ff.isAccessible
    if (!accessible) {
        ff.isAccessible = true
    }
    if (final) {
        // android: accessFlags
        val modField = getField(Field::class.java, "accessFlags")
        modField.isAccessible = true
        modField.set(ff, modField.getInt(ff).and(Modifier.FINAL.inv()))
    }

    ff.set(target, t)
    if (final) {
        val modField = getField(Field::class.java, "accessFlags")
        modField.isAccessible = true
        modField.set(ff, modField.getInt(ff).and(Modifier.FINAL))
    }
    ff.isAccessible = accessible
}