package cc.ifnot.app.libs;

import android.app.Application;

import com.didichuxing.doraemonkit.DoraemonKit;
import com.didichuxing.doraemonkit.kit.AbstractKit;

import java.util.Collections;

/**
 * author: dp
 * created on: 2020/7/6 3:43 PM
 * description:
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DoraemonKit.install(this, Collections.<AbstractKit>emptyList(), "pId");
    }
}
