package cc.ifnot.ax.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import cc.ifnot.libs.utils.Lg;

/**
 * author: dp
 * created on: 2020/7/18 8:59 AM
 * description:
 */
public class WindowService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        Lg.d("onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Lg.d("onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Lg.d("onDestroy");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Lg.d("onBind: %s", intent);
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Lg.d("onUnbind: %s", intent);
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Lg.d("onRebind: %s", intent);
        super.onRebind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Lg.d("onTaskRemoved: %s", rootIntent);
        super.onTaskRemoved(rootIntent);
    }
}
