package com.michaelsvit.kolnoa;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Michael on 8/15/2016.
 */
public class MovieScreening implements Parcelable {
    private String date;
    private String time;
    private String id;
    private String type;

    public MovieScreening(String date, String time, String id, String type) {
        this.date = date;
        this.time = time;
        this.id = id;
        this.type = type;
    }

    protected MovieScreening(Parcel in) {
        date = in.readString();
        time = in.readString();
        id = in.readString();
        type = in.readString();
    }

    public static final Creator<MovieScreening> CREATOR = new Creator<MovieScreening>() {
        @Override
        public MovieScreening createFromParcel(Parcel in) {
            return new MovieScreening(in);
        }

        @Override
        public MovieScreening[] newArray(int size) {
            return new MovieScreening[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(date);
        parcel.writeString(time);
        parcel.writeString(id);
        parcel.writeString(type);
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }
}
