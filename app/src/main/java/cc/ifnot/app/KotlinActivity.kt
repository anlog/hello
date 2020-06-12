package cc.ifnot.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import cc.ifnot.libs.utils.Lg
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleOnSubscribe
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

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

    init {
        Lg.tag(TAG)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Lg.e("r - dump")

        findViewById<View>(android.R.id.content).setOnClickListener {
            startActivity(Intent(this, a.m::class.java))
        }
//        val it = Intent()
//        it.action = Intent.ACTION_BOOT_COMPLETED
//        val queryBroadcastReceivers = packageManager.queryBroadcastReceivers(it, PackageManager.MATCH_ALL)
//        for (r: ResolveInfo in queryBroadcastReceivers) {
//            r.dump(LogPrinter(Log.ERROR, TAG), " -- ")
//        }


        return


        val arr = arrayOf<String>("echo 'hello world!'")
//        val process = Runtime.getRuntime().exec(arr)

//        execCommand(arr, onOutput = fun(out: List<String>, err: List<String>) : Unit {
//            out.isEmpty() ?: Log.w(TAG, out.toString())
//            err.isEmpty() ?: Log.w(TAG, err.toString());
//        })

        execCommand2(arr)

        return

        execCommand(arr, {
            Log.w(TAG, "out: $it")
        }, {
            Log.w(TAG, "err: $it")
        })

        return

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

    private fun execCommand2(arr: Array<String>) {
        val process = Runtime.getRuntime().exec("sh")
        val outputStream = process.outputStream
        Log.w(TAG, "exec arr: ${arr.joinToString(separator = " ")}")
        outputStream.write(arr.joinToString(separator = " ").toByteArray())
        outputStream.write("\n".toByteArray())
        outputStream.flush()

        val inputStream = process.inputStream
        val errorStream = process.errorStream

        val threadPool = Executors.newCachedThreadPool()
        threadPool.execute {
            try {
                threadPool.submit {
                    val inReader = BufferedReader(InputStreamReader(inputStream))
                    var in_line = inReader.readLine()
                    while (in_line != null) {
                        Log.w(TAG, "inputStream: #${in_line}$")
                        in_line = inReader.readLine()
                    }
                }.get(5, TimeUnit.SECONDS)
            } catch (e: TimeoutException) {
                e.printStackTrace()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

            try {
                threadPool.submit {
                    val errorReader = BufferedReader(InputStreamReader(errorStream))
                    var err_line = errorReader.readLine()
                    while (err_line != null) {
                        Log.w(TAG, "errorStream: ${err_line}")
                        err_line = errorReader.readLine()
                    }
                }.get(5, TimeUnit.SECONDS)
            } catch (e: TimeoutException) {
                e.printStackTrace()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        Log.w(TAG, "done.")
    }

    private fun execCommand(arr: Array<String>, onOut: (out: String) -> Unit, onErr: (err: String) -> Unit) {
        val process = Runtime.getRuntime().exec(arr)
        Log.w(TAG, "exec cmd: $arr")
//        process.outputStream.write(arr.toString().toByteArray())
//        process.outputStream.write("echo hello world".toByteArray())
//        process.outputStream.write("\n".toByteArray())
//        process.outputStream.flush()
        Log.w(TAG, "out -> ${process.outputStream.javaClass.canonicalName}")
        val threadPool = Executors.newCachedThreadPool()
        threadPool.execute {
            Log.w(TAG, "threadPool: start")
            val outStream = BufferedReader(InputStreamReader(process.inputStream))
            while (true) {
                if (outStream.ready()) {
                    val line = outStream.readLine()
                    Log.w(TAG, "threadPool: outStream readline $line")
                    if (line.isNotEmpty()) onOut(line)
                } else {
//                    Log.w(TAG, "threadPool: outStream not ready")
                    if (!process.isAlive) {
                        Log.w(TAG, "threadPool: outStream process died ")
                        outStream.close()
                        break
                    }
                }

            }
        }

        threadPool.execute {
            Log.w(TAG, "threadPool: start")
            val errStream = BufferedReader(InputStreamReader(process.errorStream))
            while (true) {
                if (errStream.ready()) {
                    val line = errStream.readLine()
                    Log.w(TAG, "threadPool: errStream readline $line")
                    if (line.isNotEmpty()) onErr(line)
                } else {
//                    Log.w(TAG, "threadPool: errStream not ready")
                    Log.w(TAG, "errStream alive!")
                    if (!process.isAlive) {
                        errStream.close()
                        break
                    }
                }

            }
        }
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