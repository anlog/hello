package cc.ifnot.ax

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import cc.ifnot.ax.databinding.ActivityMainBinding
import cc.ifnot.ax.service.ClientService
import cc.ifnot.ax.service.WindowService
import cc.ifnot.ax.utils.stubAction
import cc.ifnot.libs.utils.Lg
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    init {
        Lg.level(Lg.MORE)
        Lg.tag("MainActivity")
    }

    private lateinit var ax_client: IBinder

    private val conn: ServiceConnection
            by lazy {
                object : ServiceConnection {
                    override fun onServiceDisconnected(name: ComponentName?) {
                        Lg.w("onServiceDisconnected: %s", name)
                        mBinding.btTest.isEnabled = false
                        mBinding.btTest.isClickable = false
                        mBinding.btTest.text = "test off"
                    }

                    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                        Lg.w("onServiceConnected: %s - %s", name, service)
                        if (service != null) {
                            ax_client = service
                            mBinding.btTest.isEnabled = true
                            mBinding.btTest.isClickable = true
                            mBinding.btTest.text = "test me"
                        }
                    }

                }
            }
    private lateinit var mBinding: ActivityMainBinding

//    private val leak
//            by lazy {
//                Leak()
//            }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Lg.d("onConfigurationChanged: %s", newConfig)
        recreate()
//        if (delegate.localNightMode != newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
//            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
//        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Lg.d("onCreate")
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        hello.text = "hello" // kotlin-android-extensions binding
        mBinding.btn.stateListAnimator = null
        mBinding.btn.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                Lg.d("replace")
                replace(R.id.fg, MyFragment(MyFragmentLifecycleCallbacks(mBinding)))
                commit()
            }

            mBinding.hello.text = "binding"
        }

        mBinding.btn1.setOnClickListener {}
        mBinding.btn2.setOnClickListener {}
        mBinding.btn3.apply {
            text = "startActivity"
            setOnClickListener {
                startActivity(Intent(this@MainActivity, HideActivity::class.java)
                        .apply { action = stubAction })
            }
        }

        mBinding.btWindow.setOnClickListener { startService(Intent(this, WindowService::class.java)) }

        mBinding.btServer.setOnClickListener { bindService(Intent(this, ClientService::class.java), conn, Context.BIND_AUTO_CREATE) }

        mBinding.btClient.setOnClickListener { startService(Intent(this, ClientService::class.java)) }


        mBinding.btTest.setOnClickListener {  }
    }

    class MyFragmentLifecycleCallbacks(binding: ActivityMainBinding) : FragmentManager.FragmentLifecycleCallbacks() {

        private val binding = binding

        override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
            super.onFragmentViewCreated(fm, f, v, savedInstanceState)
            Lg.d("fragment: %s view created", f)
            binding.hello.setTextColor(Color.RED)
        }
    }

}
