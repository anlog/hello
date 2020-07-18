package cc.ifnot.ax.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import cc.ifnot.ax.IService;
import cc.ifnot.libs.utils.Lg;

/**
 * author: dp
 * created on: 2020/7/18 8:21 AM
 * description:
 */
public class ClientService extends Service {

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Lg.d("onServiceConnected: %s - %s", name, service);
            final IService ss = IService.Stub.asInterface(service);
            try {
                final String he = ss.hello(789);
                Lg.d("======%s====", he);
                Lg.w(ss.hello(123));
                Lg.w(ss.world());
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Lg.d("onServiceDisconnected: %s", name);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Lg.d("onCreate");
        bindService(new Intent(this, ServerService.class), conn,
                Context.BIND_AUTO_CREATE|Context.BIND_EXTERNAL_SERVICE);
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
