import android.app.Application
import android.content.Context
import android.util.Log

/**
 * author: dp
 * created on: 2020/5/7 3:29 PM
 * description:
 */

open class App() : Application() {

    constructor(o: Any): this(){
        Log.w(TAG, "constructor")
    }


    companion object{
        private val TAG = "App"
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        Log.w(TAG, "attachBaseContext")
        
    }


    fun hello(a: String, b:Int) {

    }
}