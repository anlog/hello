package cc.ifnot.ax.service;

import android.annotation.SuppressLint;
import android.os.MemoryFile;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SharedMemory;
import android.system.ErrnoException;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import cc.ifnot.ax.callback.ICallback;
import cc.ifnot.libs.utils.Lg;
import cc.ifnot.libs.utils.MD5;

/**
 * author: dp
 * created on: 2020/7/18 3:15 PM
 * description:
 */
public class Task implements Runnable {

    public static final int GET = 1;
    public static final int POST = 1 << 1;

    private final Req req;
    private final RemoteCallbackList<ICallback> callback;

    Task(Req req, RemoteCallbackList<ICallback> callback) {
        Lg.w("task created: %s", req);
        this.req = req;
        this.callback = callback;
    }

    @Override
    public void run() {
        switch (req.method) {
            case GET:

                InputStream is = null;
                try {
                    final URLConnection connection = new URL(req.url).openConnection();
                    connection.setDoOutput(false);
                    is = connection.getInputStream();

                    Lg.w("conn: len : %s", connection.getContentLength());

//                    final MemoryFile file = new MemoryFile(req.url, size);
//                    file.writeBytes(sb.toString().getBytes(), 0, 0, size);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O_MR1) {
                        SharedMemory sm = SharedMemory.create(req.url, connection.getContentLength());
                        final ByteBuffer byteBuffer = sm.mapReadWrite();
                        byte[] buf = new byte[1024 * 1024];
                        int size = 0;
                        int read = 0;
                        final MessageDigest md5 = MessageDigest.getInstance("md5");
                        while ((read = is.read(buf)) != -1) {
                            Lg.w("readed: %s, size: %s, -- %s", read, size, byteBuffer.position());
//                            byteBuffer.position(size);
                            byteBuffer.put(buf, 0, read);
                            size += read;
                            md5.update(buf, 0, read);
                        }
                        Lg.w("done: %s - %s", size, MD5.toHexString((
                                md5.digest())));
//                        byteBuffer.put(sb.toString().getBytes());
                        final Parcel parcel = Parcel.obtain();
                        sm.writeToParcel(parcel, 0);
                        parcel.setDataPosition(0);
                        final ParcelFileDescriptor parcelFileDescriptor = parcel.readFileDescriptor();
                        Lg.d("ParcelFileDescriptor: %s", parcelFileDescriptor.getFd());
//                        final ParcelFileDescriptor dup = parcelFileDescriptor.dup();
//                        Lg.w("SharedMemory: %s - %s", parcelFileDescriptor.getFd(), dup.getFd());
                        Lg.w("parcel: %s", parcel);
                        if (callback != null) {
                            try {
                                synchronized (callback) {
                                    callback.beginBroadcast();
                                    final int count = callback.getRegisteredCallbackCount();
                                    for (int i = 0; i < count; i++) {
                                        final ICallback c = callback.getBroadcastItem(i);
                                        c.onSuccess(new Res(req.id, size, parcelFileDescriptor.getFileDescriptor()));
                                    }
                                }
                            } finally {
                                callback.finishBroadcast();
                            }

                        }
                        sm.close();

                    } else {
                        MemoryFile mf = new MemoryFile(req.url, connection.getContentLength());

                        byte[] buf = new byte[1024 * 1024];
                        int size = 0;
                        int read = 0;
                        final MessageDigest md5 = MessageDigest.getInstance("md5");
                        while ((read = is.read(buf)) != -1) {
                            Lg.w("readed: %s - %s", read, size);
                            mf.writeBytes(buf, 0, size, read);
                            size += read;
                            md5.update(buf, 0, read);
                        }
                        Lg.w("done: %s - %s", size, MD5.toHexString((
                                md5.digest())));

//                        mf.writeBytes(sb.toString().getBytes(), 0, 0, size);
                        @SuppressLint("DiscouragedPrivateApi") final Method fileDescriptorField = mf.getClass().getDeclaredMethod("getFileDescriptor");
                        final FileDescriptor fd = (FileDescriptor) fileDescriptorField.invoke(mf);
                        assert fd != null;
                        final Field descriptor = fd.getClass().getDeclaredField("descriptor");
                        descriptor.setAccessible(true);
                        final int d = (int) descriptor.get(fd);
                        Lg.w("MemoryFile: %s - %s - %s", d, fd, mf);

                        if (callback != null) {
                            try {
                                synchronized (callback) {
                                    callback.beginBroadcast();
                                    final int count = callback.getRegisteredCallbackCount();
                                    for (int i = 0; i < count; i++) {
                                        final ICallback c = callback.getBroadcastItem(i);
                                        c.onSuccess(new Res(req.id, size, fd));
                                    }
                                }
                            } finally {
                                callback.finishBroadcast();
                            }
                        }
                        mf.close();
                    }
//                    final Socket socket = new Socket();
//                    socket.connect(new InetSocketAddress("pan.baidu.com", 80));

                } catch (IOException | ErrnoException | RemoteException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchAlgorithmException | NoSuchFieldException e) {
                    e.printStackTrace();
                    if (callback != null) {
                        List<String> errs = new ArrayList<>();
                        Throwable t = e;
                        do {
                            errs.add(t.getMessage());
                        } while ((t = t.getCause()) != null);
                        try {
                            try {
                                synchronized (callback) {
                                    callback.beginBroadcast();
                                    final int count = callback.getRegisteredCallbackCount();
                                    for (int i = 0; i < count; i++) {
                                        final ICallback c = callback.getBroadcastItem(i);
                                        c.onError(new Err(errs.toArray(new String[0])));
                                    }
                                }
                            } finally {
                                callback.finishBroadcast();
                            }
                        } catch (RemoteException ex) {
                            ex.printStackTrace();
                        }
                    }
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case POST:
                try {
                    final URLConnection connection = new URL(req.url).openConnection();
                    connection.setDoOutput(true);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
}
