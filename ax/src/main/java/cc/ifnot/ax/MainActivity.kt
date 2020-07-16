package cc.ifnot.ax

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import cc.ifnot.ax.databinding.ActivityMainBinding
import cc.ifnot.ax.utils.stubAction
import cc.ifnot.libs.utils.Lg
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    init {
        Lg.level(Lg.MORE)
        Lg.tag("MainActivity")
    }

    private lateinit var mBinding: ActivityMainBinding

//    private val leak
//            by lazy {
//                Leak()
//            }


    override fun onCreate(savedInstanceState: Bundle?) {
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
