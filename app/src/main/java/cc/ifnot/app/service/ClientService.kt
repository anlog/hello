package cc.ifnot.app.service

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import cc.ifnot.app.ITest

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
        Log.w(TAG, "onCreate")

        bindService(Intent().setClass(applicationContext, a.ss::class.java),
                object : ServiceConnection {
                    override fun onServiceDisconnected(name: ComponentName?) {
                        Log.w(TAG, "onServiceDisconnected")
                    }

                    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                        Log.w(TAG, "onServiceConnected")

                        doTest(ITest.Stub.asInterface(service))

                    }
                }, Context.BIND_AUTO_CREATE)

    }

    private fun doTest(service: ITest) {
        for (i in 1..100) {
            Log.w(TAG, "doTest: $i start")
            service.iTest0(i)
            service.iTest1(i)
            Log.w(TAG, "doTest: iTest2 ${service.iTest2(i)}")
            Log.w(TAG, "doTest: $i end")
        }

    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


}