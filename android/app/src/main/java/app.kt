import a.cs
import android.app.Application
import android.content.Context
import android.content.Intent
import cc.ifnot.libs.utils.Lg

/**
 * author: dp
 * created on: 2020/5/7 3:29 PM
 * description:
 */

open class App() : Application() {

    init {
        Lg.tag("App")
        Lg.showMore(true)
    }

    constructor(o: Any) : this() {
        Lg.w("constructor")
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        Lg.w("attachBaseContext")

        Lg.w("mapLibraryName: ${System.mapLibraryName("native-lib")}")
    }

    override fun onCreate() {
        super.onCreate()
    }

    fun hello(a: String, b: Int) {

    }
}