package cc.ifnot.app.hello.shortcuts;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import cc.ifnot.libs.utils.Lg;

/**
 * author: dp
 * created on: 2020/6/9 3:07 PM
 * description:
 */
public class StaticShortcutsActivity extends AppCompatActivity {

    static {
        Lg.tag("StaticShortcutsActivity");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Lg.d("onCreate");
    }
}
