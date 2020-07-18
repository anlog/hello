package cc.ifnot.ax.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import cc.ifnot.ax.IService;
import cc.ifnot.ax.callback.ICallback;
import cc.ifnot.libs.utils.Lg;
import cc.ifnot.libs.utils.MD5;

/**
 * author: dp
 * created on: 2020/7/18 8:21 AM
 * description:
 */
public class ClientService extends Service {

    private final int TEST_MEMORYFILE = IBinder.FIRST_CALL_TRANSACTION + 1;
    private final int TEST_SHAREMEMORY = IBinder.FIRST_CALL_TRANSACTION + 2;
    private final int TEST_SERVER = IBinder.FIRST_CALL_TRANSACTION + 3;
    AtomicInteger al = new AtomicInteger();
    private ExecutorService es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Lg.w("onServiceConnected: %s - %s", name, service);
            final IService ss = IService.Stub.asInterface(service);

            try {
                final Req req = new Req(al.getAndIncrement(), Task.GET,
                        "https://ucan.25pp.com/Wandoujia_web_seo_google_homepage.apk", null);
                final int ret = ss.get(req);
                Lg.w("get req: %s - %s", ret, req);
                ss.registerCallback(new ICallback.Stub() {
                    @Override
                    public void onSuccess(final Res res) throws RemoteException {
                        Lg.w("onSuccess: %s - %s - %s", res.id, res.size, res.fd);

                        es.execute(new Runnable() {
                            @Override
                            public void run() {
                                try (InputStream is = new FileInputStream(res.fd)) {
                                    byte[] buf = new byte[1024*100];
                                    final MessageDigest md5 = MessageDigest.getInstance("md5");
                                    int size = 0;
                                    int read = 0;
                                    int count = 0;
                                    while ((read = is.read(buf)) > 0) {
                                        Lg.w("onSuccess: ==>%s, %s, %s", read, size, count++);
                                        md5.update(buf, 0, read);
                                        size += read;
                                    }
                                    Lg.w("done: %s - %s", size, MD5.toHexString(md5.digest()));

                                } catch (IOException | NoSuchAlgorithmException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(Err err) throws RemoteException {
                        Lg.w("onError: %s", Arrays.toString(err.errs));
                    }
                });

            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Lg.w("onServiceDisconnected: %s", name);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Lg.w("onCreate");
        bindService(new Intent(this, ServerService.class), conn,
                Context.BIND_AUTO_CREATE); // | Context.BIND_EXTERNAL_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Lg.w("onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Lg.w("onDestroy");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Lg.w("onBind: %s", intent);
        return new Binder() {
            @Override
            protected boolean onTransact(int code, @NonNull Parcel data, @Nullable Parcel reply, int flags) throws RemoteException {
                switch (code) {
                    case TEST_MEMORYFILE:
                        return super.onTransact(code, data, reply, flags);

                    case TEST_SHAREMEMORY:
                        return super.onTransact(code, data, reply, flags);

                    case TEST_SERVER:
                        return super.onTransact(code, data, reply, flags);

                    default:
                        return super.onTransact(code, data, reply, flags);
                }
            }
        };
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Lg.w("onUnbind: %s", intent);
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Lg.w("onRebind: %s", intent);
        super.onRebind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Lg.w("onTaskRemoved: %s", rootIntent);
        super.onTaskRemoved(rootIntent);
    }
}
