package cc.ifnot.ax.service;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * author: dp
 * created on: 2020/7/18 1:44 PM
 * description:
 */
public class Req implements Parcelable {
    public static final Creator<Req> CREATOR = new Creator<Req>() {
        @Override
        public Req createFromParcel(Parcel in) {
            return new Req(in);
        }

        @Override
        public Req[] newArray(int size) {
            return new Req[size];
        }
    };

    public int id;
    public int method;
    public String url;
    public String body;

    public Req(int id, int method, String url, String body) {
        this.id = id;
        this.method = method;
        this.url = url;
        this.body = body;
    }

    protected Req(Parcel in) {
        id = in.readInt();
        method = in.readInt();
        url = in.readString();
        body = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(method);
        dest.writeString(url);
        dest.writeString(body);
    }

    @Override
    public String toString() {
        return "Req{" +
                "id=" + id +
                ", method=" + method +
                ", url='" + url + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
