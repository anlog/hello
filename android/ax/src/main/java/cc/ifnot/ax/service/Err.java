package cc.ifnot.ax.service;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

/**
 * author: dp
 * created on: 2020/7/18 1:45 PM
 * description:
 */
public class Err implements Parcelable {
    public static final Creator<Err> CREATOR = new Creator<Err>() {
        @Override
        public Err createFromParcel(Parcel in) {
            return new Err(in);
        }

        @Override
        public Err[] newArray(int size) {
            return new Err[size];
        }
    };

    public String[] errs;

    public Err(String[] errs) {
        this.errs = errs;
    }

    public Err(Parcel in) {
        in.readStringArray(errs);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(errs);
    }

    @Override
    public String toString() {
        return "Err{" +
                "errs=" + Arrays.toString(errs) +
                '}';
    }
}
