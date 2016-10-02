package com.michaelsvit.kolnoa;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Michael on 9/10/2016.
 */
public class Seat implements Parcelable {

    public enum Status {
        UNOCCUPIED, OCCUPIED, SELECTED
    }

    private int row;
    private int column;
    private Status status;
    private boolean isHandicap;

    public Seat(int row, int column, Status status, boolean isHandicap) {
        this.row = row;
        this.column = column;
        this.status = status;
        this.isHandicap = isHandicap;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public Status getStatus() {
        return status;
    }

    public boolean isHandicap() {
        return isHandicap;
    }

    protected Seat(Parcel in) {
        row = in.readInt();
        column = in.readInt();
        status = (Status) in.readSerializable();
        isHandicap = in.readByte() != 0;
    }

    public static final Creator<Seat> CREATOR = new Creator<Seat>() {
        @Override
        public Seat createFromParcel(Parcel in) {
            return new Seat(in);
        }

        @Override
        public Seat[] newArray(int size) {
            return new Seat[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(row);
        parcel.writeInt(column);
        parcel.writeSerializable(status);
        parcel.writeByte((byte) (isHandicap ? 1 : 0));
    }
}
