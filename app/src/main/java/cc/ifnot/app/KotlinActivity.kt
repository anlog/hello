package cc.ifnot.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleEmitter
import io.reactivex.rxjava3.core.SingleOnSubscribe
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * author: dp
 * created on: 2020/5/9 5:25 PM
 * description:
 */

@SuppressLint("Registered")
open class KotlinActivity : AppCompatActivity() {

    companion object {
        private val TAG = "KotlinActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        Log.d(TAG, "thread: " + Thread.currentThread(), Exception())
        Single.create(SingleOnSubscribe<Int> { Log.d(TAG, "thread: " + Thread.currentThread(), Exception()) }).subscribeOn(Schedulers.single())
                .ignoreElement().blockingAwait()

        return
        // test
        val list = ArrayList<String>();
        list.add("a=b");
        list.add("aa=bb");
        list.add("aaa=xx");
        list.add("aaaa=dd");
        Log.w("test", list.toMapInternal().toString())


        Log.w("test", list.testRet().toString())
    }
}


private fun List<String>.testRet() = map { it }


private fun List<String>.toMapInternal(): Map<String, String> {

    val map = LinkedHashMap<String, String>()
    for (s in this) {
        val ss = s.split("=", limit = 2)
        if (ss.size == 2) {
            map[ss[0]] = ss[1]
        }
    }


    return map

    return map(fun(it: String): List<String> {
        return it.split("=", limit = 2)
    })
            .filter(fun(it: List<String>): Boolean {
                return it.size == 2
            })
            .map(fun(it: List<String>): Pair<String, String> {
                return Pair<String, String>(first = it[0], second = it[1])
            })
            .toMap()
}