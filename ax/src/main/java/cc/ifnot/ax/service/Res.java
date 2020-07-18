package cc.ifnot.ax.service;

import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;

import java.io.FileDescriptor;

import cc.ifnot.libs.utils.Lg;

/**
 * author: dp
 * created on: 2020/7/18 1:32 PM
 * description:
 */
public class Res implements Parcelable {
    public static final Creator<Res> CREATOR = new Creator<Res>() {
        @Override
        public Res createFromParcel(Parcel in) {
            return new Res(in);
        }

        @Override
        public Res[] newArray(int size) {
            return new Res[size];
        }
    };

    public int id;
    public int size;
    public FileDescriptor fd;

    public Res(int id, int size, FileDescriptor fd) {
        this.id = id;
        this.size = size;
        this.fd = fd;
    }

    public Res(Parcel in) {
        id = in.readInt();
        size = in.readInt();
        final ParcelFileDescriptor parcelFileDescriptor = in.readFileDescriptor();
        Lg.w("res:  %s", parcelFileDescriptor);
        fd = parcelFileDescriptor.getFileDescriptor();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(size);
        dest.writeFileDescriptor(fd);
    }

    @Override
    public String toString() {
        return "Res{" +
                "id=" + id +
                ", size=" + size +
                ", fd=" + fd +
                '}';
    }
}
