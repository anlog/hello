package cc.ifnot.app.service

import a.ss
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import cc.ifnot.app.ITest
import cc.ifnot.libs.utils.Lg
import java.util.*

/**
 * author: dp
 * created on: 2020/5/29 6:17 PM
 * description:
 */

open class ClientService : Service() {

    companion object {
        const val TAG = "ClientService"
    }

    override fun onCreate() {
        super.onCreate()
        Lg.w("onCreate")

        bindService(Intent().setClass(applicationContext, ss::class.java),
                object : ServiceConnection {
                    override fun onServiceDisconnected(name: ComponentName?) {
                        Lg.w("onServiceDisconnected")
                    }

                    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                        Lg.w("onServiceConnected")

                        doTest(ITest.Stub.asInterface(service))

                    }
                }, Context.BIND_AUTO_CREATE)

    }

    private fun doTest(service: ITest) {
        for (i in 1..100) {
            Lg.w("doTest: $i start")
//            service.iTest0(i)
//            service.iTest1(i)
//            Lg.w("doTest: iTest2 ${service.iTest2(i)}")
            service.transact(String.let { UUID.randomUUID().toString() }
                    .map { UUID.nameUUIDFromBytes(byteArrayOf(it.inc().toByte(), it.dec().toByte())).toString() }
                    .toString()
                    .map { UUID.nameUUIDFromBytes(byteArrayOf(it.inc().toByte(), it.dec().toByte())).toString() }
                    .joinToString(separator = "\n") { "\"$it\"" })
            Lg.w("doTest: $i end")
        }

    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


}