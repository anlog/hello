// IService.aidl
package cc.ifnot.ax;
import cc.ifnot.ax.data.Req;
import cc.ifnot.ax.data.Res;
import cc.ifnot.ax.callback.ICallback;

// Declare any non-default types here with import statements

interface IService {
    int get(in Req req);
    int post(in Req req);

    Res req(in Req req);

    boolean registerCallback(in ICallback callback);
    boolean unregisterCallback(in ICallback callback);
}
