package com.michaelsvit.kolnoa;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Michael on 8/28/2016.
 */
public class Site implements Parcelable{
    public static final String TICKETS_URL_ARG_NAME = "tickets_url";
    public static final String SITE_ARG_NAME = "siteId";

    private String name;
    private int id;
    private String ticketsUrl;

    public Site(String name, int id, String ticketsUrl) {
        this.name = name;
        this.id = id;
        this.ticketsUrl = ticketsUrl;
    }

    protected Site(Parcel in) {
        name = in.readString();
        id = in.readInt();
        ticketsUrl = in.readString();
    }

    public static final Creator<Site> CREATOR = new Creator<Site>() {
        @Override
        public Site createFromParcel(Parcel in) {
            return new Site(in);
        }

        @Override
        public Site[] newArray(int size) {
            return new Site[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(id);
        parcel.writeString(ticketsUrl);
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getTicketsUrl() {
        return ticketsUrl;
    }

    @Override
    public String toString() {
        return name;
    }
}
