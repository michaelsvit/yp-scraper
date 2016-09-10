package com.michaelsvit.kolnoa;

/**
 * Created by Michael on 9/10/2016.
 */
public class Seat {
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
}
