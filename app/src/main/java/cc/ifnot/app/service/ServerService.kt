package cc.ifnot.app.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.SystemClock
import cc.ifnot.app.ITest
import cc.ifnot.libs.utils.Lg

/**
 * author: dp
 * created on: 2020/5/29 6:16 PM
 * description:
 */
open class ServerService : Service() {

    companion object {
        const val TAG = "ServerService"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return ServerServiceProxy()
    }

    class ServerServiceProxy : ITest.Stub() {
        override fun iTest0(a: Int) {
            Lg.w("iTest0 start")
            SystemClock.sleep(1000)
            Lg.w("iTest0 end")
        }

        override fun iTest1(a: Int) {
            Lg.w("iTest1 start")
            SystemClock.sleep(1000)
            Lg.w("iTest1 end")

        }

        override fun iTest2(a: Int): Int {
            Lg.w("iTest2 start")
            SystemClock.sleep(1000)
            Lg.w("iTest2 end")
            return a * a
        }

        override fun transact(msg: String?) {
            // do nothing here, but delay
            Lg.w("transact start - %s", msg?.length)
            SystemClock.sleep(1000);
            Lg.w("transact end")
        }

    }
}