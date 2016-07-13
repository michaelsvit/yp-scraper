package com.michaelsvit.kolnoa;

import android.os.Parcel;
import android.os.Parcelable;
import java.net.URL;

/**
 * Represents a single cinema
 */
public class Cinema implements Parcelable{
    private String name;
    private URL url;

    public Cinema(String name, URL url) {
        this.name = name;
        this.url = url;
    }

    protected Cinema(Parcel in) {
        name = in.readString();
        url = (URL) in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeSerializable(url);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Cinema> CREATOR = new Creator<Cinema>() {
        @Override
        public Cinema createFromParcel(Parcel in) {
            return new Cinema(in);
        }

        @Override
        public Cinema[] newArray(int size) {
            return new Cinema[size];
        }
    };

    public String getName() {
        return name;
    }

    public URL getUrl() {
        return url;
    }
}
