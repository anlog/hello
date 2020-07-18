// ICallback.aidl
package cc.ifnot.ax.callback;
import cc.ifnot.ax.data.Res;
import cc.ifnot.ax.data.Err;

// Declare any non-default types here with import statements

interface ICallback {
    void onSuccess(in Res res);
    void onError(in Err err);
}
