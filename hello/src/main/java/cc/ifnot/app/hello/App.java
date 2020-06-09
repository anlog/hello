package cc.ifnot.app.hello;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;

import java.util.Arrays;

import cc.ifnot.libs.utils.Lg;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableEmitter;
import io.reactivex.rxjava3.core.CompletableOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class App extends Application {


    static {
        Lg.tag("App");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        initShortCuts(base);

        ComponentName app1 = new ComponentName(this, MainActivity.class);
        ComponentName app2 = new ComponentName(this, MainActivity2.class);
        PackageManager packageManager = getPackageManager();
        Lg.d("app1: %s -- app2: %s", packageManager.getComponentEnabledSetting(app1),
                packageManager.getComponentEnabledSetting(app2));
        packageManager.setComponentEnabledSetting(app2, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
        Lg.d("set app1: %s -- app2: %s", packageManager.getComponentEnabledSetting(app1),
                packageManager.getComponentEnabledSetting(app2));
    }

    private void initShortCuts(final Context base) {
        Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull CompletableEmitter emitter) throws Throwable {
                ShortcutManager shortcutManager = (ShortcutManager) getSystemService(Context.SHORTCUT_SERVICE);
                Lg.d("manager.isRequestPinShortcutSupported() : %s", shortcutManager.isRequestPinShortcutSupported());

                Lg.d("initShortCuts pinned: %s max: %s", shortcutManager.getPinnedShortcuts().size(),
                        shortcutManager.getMaxShortcutCountPerActivity());

                Lg.d("initShortCuts : %s", Thread.currentThread());

                ShortcutInfo info = new ShortcutInfo.Builder(base, "shortcuts_dynamic")
                        .setShortLabel(getString(R.string.short_label) + "_dynamic")
                        .setLongLabel(getString(R.string.long_label) + "_dynamic")
                        .setDisabledMessage(getString(R.string.disabled) + "_dynamic")
                        .setIcon(Icon.createWithResource(base, R.mipmap.ic_launcher_round))
                        .setIntent(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://www.baidu.com")))
                        .build();

                shortcutManager.addDynamicShortcuts(Arrays.asList(info));
            }
        }).subscribeOn(Schedulers.single()).subscribe();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
