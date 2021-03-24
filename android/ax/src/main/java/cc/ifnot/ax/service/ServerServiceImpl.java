package cc.ifnot.ax.service;

import android.os.RemoteCallbackList;
import android.os.RemoteException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

import cc.ifnot.ax.IService;
import cc.ifnot.ax.callback.ICallback;
import cc.ifnot.libs.utils.Lg;

/**
 * author: dp
 * created on: 2020/7/18 2:56 PM
 * description:
 */
class ServerServiceImpl extends IService.Stub implements ICallback {

    private final RemoteCallbackList<ICallback> mCallbacks = new RemoteCallbackList<>();

    private ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(),
            new ThreadFactory() {
                AtomicLong id = new AtomicLong();

                @Override
                public Thread newThread(Runnable r) {
                    Lg.d("thread created");
                    return new Thread(r, "ax_" + id.getAndIncrement());
                }
            });

    @Override
    public int get(Req req) throws RemoteException {
        Lg.w("get exec req: %s", req);
        pool.execute(new Task(req, mCallbacks));
        return 1;
    }

    @Override
    public int post(Req req) throws RemoteException {
        return 0;
    }

    @Override
    public Res req(Req req) throws RemoteException {
        return null;
    }

    @Override
    public boolean registerCallback(ICallback callback) throws RemoteException {
        Lg.w("registerCallback");
        if (callback == null) {
            throw new IllegalArgumentException("registerCallback is null");
        }
        synchronized (mCallbacks) {
            return mCallbacks.register(callback);
        }
    }

    @Override
    public boolean unregisterCallback(ICallback callback) throws RemoteException {
        Lg.w("unregisterCallback");
        if (callback == null) {
            throw new IllegalArgumentException("registerCallback is null");
        }
        synchronized (mCallbacks) {
            return mCallbacks.unregister(callback);
        }
    }

    @Override
    public void onSuccess(Res res) throws RemoteException {
        synchronized (mCallbacks) {
            mCallbacks.beginBroadcast();
            try {
                final int count = mCallbacks.getRegisteredCallbackCount();
                for (int i = 0; i < count; i++) {
                    final ICallback c = mCallbacks.getBroadcastItem(i);
                    if (c != null) {
                        c.onSuccess(res);
                    }
                }
            } finally {
                mCallbacks.finishBroadcast();
            }
        }
    }

    @Override
    public void onError(Err err) throws RemoteException {
        synchronized (mCallbacks) {
            mCallbacks.beginBroadcast();
            try {
                final int count = mCallbacks.getRegisteredCallbackCount();
                for (int i = 0; i < count; i++) {
                    final ICallback c = mCallbacks.getBroadcastItem(i);
                    if (c != null) {
                        c.onError(err);
                    }
                }
            } finally {
                mCallbacks.finishBroadcast();
            }
        }
    }
}
