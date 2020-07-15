@file:JvmName("-reflect")

package cc.ifnot.ax.utils

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import cc.ifnot.libs.utils.Lg
import java.lang.reflect.Field
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

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
        setField(c, clz, "classLoader", null)
    }
    return clz
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
    val ams = amsField.get(am)
    val proxy = Proxy.newProxyInstance(Thread.currentThread().contextClassLoader,
            arrayOf(Class.forName("android.app.IActivityManager")),
            IActivityManagerProxy(ams!!))
    amsField.set(am, proxy)

}

@RequiresApi(Build.VERSION_CODES.O)
fun Method.toPretty(): String {
    val sb = StringBuffer()
    sb.append(returnType.typeName).append(' ')
    sb.append(declaringClass.typeName).append('.')
    sb.append(name)
    return sb.toString()
}

class IActivityManagerProxy(@NonNull private val am: Any) : InvocationHandler {
    override fun invoke(proxy: Any?, method: Method?, args: Array<Any?>?): Any? {
        Lg.d("======proxy======")
        Lg.d("invoked: %s - %s", method?.toPretty(), arrayOf(args))
        Lg.d("====== end ======")

        return when (method) {
            null -> Lg.d("invoked method is null")
            else -> {
                when (method.name) {
                    "startActivity" -> if (!args.isNullOrEmpty()) {
                        var i = 0
                        while (args[i] !is Intent) {
                            if (++i > args.size - 1) break
                        }
                        val it = args[i] as Intent
                        args[i] = Intent("cc.ifnot.ax.AMS_HOOK")
                                .putExtra("cc.ifnot.ax.AMS_HOOK", it)
                    } else if (args.isNullOrEmpty()) method.invoke(am) else method.invoke(am, *args)
                    else -> if (args.isNullOrEmpty()) method.invoke(am) else method.invoke(am, *args)
                }
            }
        }
    }

}

@Throws
fun invoke(@NonNull clz: Class<*>, @Nullable target: Any?, @NonNull m: String, @Nullable args: Array<*>?): Any? {
    Lg.d("reflect: invoke %s - %s - %s", clz, target, m)
    val mm = if (args != null && args.isNotEmpty()) {
        val types = ArrayList<Class<*>>()
        for (i in args) {
            types.add(i!!::class.java)
        }
        @Suppress("UNCHECKED_CAST")
        clz.getDeclaredMethod(m, *types.toArray() as Array<out Class<*>>)
    } else clz.getDeclaredMethod(m)
    val accessible = mm.isAccessible
    if (!accessible) {
        mm.isAccessible = true
    }
    val ret =
            if (args == null) mm.invoke(target) else mm.invoke(target, args)

    mm.isAccessible = accessible
    return ret
}

fun getField(@NonNull clz: Class<*>, @NonNull f: String): Field {
    Lg.d("reflect: %s - %s", clz, f)
    val ff = clz.getDeclaredField(f)
    val accessible = ff.isAccessible
    if (!accessible) {
        ff.isAccessible = true
    }
    ff.isAccessible = accessible
    return ff
}

fun getField(@NonNull clz: Class<*>, @Nullable target: Any?, @NonNull f: String): Any? {
    Lg.d("reflect: %s - %s - %s", clz, target, f)
    val ff = clz.getDeclaredField(f)
    val accessible = ff.isAccessible
    if (!accessible) {
        ff.isAccessible = true
    }
    val ret = ff.get(target)
    ff.isAccessible = accessible
    return ret
}

fun setField(@NonNull clz: Class<*>, @Nullable target: Any?, @NonNull f: String, @Nullable t: Any?) {
    Lg.d("reflect: %s - %s - %s - %s", clz, target, f, t)
    val ff = clz.getDeclaredField(f)
    val accessible = ff.isAccessible
    if (!accessible) {
        ff.isAccessible = true
    }
    ff.set(target, t)
    ff.isAccessible = accessible
}