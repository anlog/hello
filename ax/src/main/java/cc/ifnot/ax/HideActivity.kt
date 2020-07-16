package cc.ifnot.ax

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cc.ifnot.libs.utils.Lg

/**
 * author: dp
 * created on: 2020/7/16 12:27 PM
 * description:
 */

class StubActivity : AppCompatActivity()

class HideActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val v = View(this)
        setContentView(TextView(this).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT)
            text = "Hooked"
            textSize = 100f
            setTextColor(Color.CYAN)
            gravity = Gravity.CENTER
        })
        Lg.d("onCreate")
    }
}