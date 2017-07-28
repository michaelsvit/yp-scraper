package com.michaelsvit.kolnoa;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by Michael on 9/10/2016.
 */
public class SeatMap implements Parcelable {
    public static final String ARG_NAME = "seat_map";
    private static final String LOG_TAG = SeatMap.class.getSimpleName();

    private Seat[][] map;
    private int rows;
    private int cols;

    public SeatMap(String html){
        Document doc = Jsoup.parse(html);

        final String SEAT_MAP_CLASS = "VenueSectionSeatsPanel";
        final String SEAT_CLASS = "seat";

        Element seatMapElement = doc.getElementsByClass(SEAT_MAP_CLASS).first();
        Elements seatElements = seatMapElement.getElementsByClass(SEAT_CLASS);

        getMapDimensions(seatElements);
        map = new Seat[rows][cols];

        for(Element seatElem : seatElements){
            Seat seat = getSeatFromElem(seatElem);
            map[seat.getRow()][seat.getColumn()] = seat;
        }
    }

    protected SeatMap(Parcel in) {
        rows = in.readInt();
        cols = in.readInt();
        map = new Seat[rows][cols];
        for(int row = 0; row < rows; row++){
            for(int col = 0; col < cols; col++) {
                map[row][col] = in.readParcelable(Seat.class.getClassLoader());
            }
        }
    }

    public static final Creator<SeatMap> CREATOR = new Creator<SeatMap>() {
        @Override
        public SeatMap createFromParcel(Parcel in) {
            return new SeatMap(in);
        }

        @Override
        public SeatMap[] newArray(int size) {
            return new SeatMap[size];
        }
    };

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public Seat getSeat(int row, int col){
        return map[row][col];
    }

    private void getMapDimensions(Elements seats) {
        rows = 0;
        cols = 0;

        for(Element seat : seats){
            String seatId = seat.id();
            String[] splitId = seatId.split("_");
            int row = Integer.valueOf(splitId[2]);
            int col = Integer.valueOf(splitId[3]);
            rows = Math.max(rows, row);
            cols = Math.max(cols, col);
        }
    }

    private Seat getSeatFromElem(Element seatElem) {
        // Id example: _Seat_1_5
        // Convert to start from 0 instead of 1
        String seatId = seatElem.id();
        String[] splitId = seatId.split("_");
        int row = Integer.valueOf(splitId[2]) - 1;
        int col = Integer.valueOf(splitId[3]) - 1;
        Seat.Status status = getSeatStatusFromElem(seatElem);
        boolean isHandicap = checkIfHandicap(seatElem);

        return new Seat(row, col, status, isHandicap);
    }

    private boolean checkIfHandicap(Element seatElem) {
        return seatElem.attr("style").contains("SeatAttrId=10");
    }

    private Seat.Status getSeatStatusFromElem(Element seatElem) {
        String styleAttr = seatElem.attr("style");
        // Style attribute example: top:20px;left:131px;background-image:url(GetSeatImage.aspx?W=26&H=26&Status=1)
        int beginIndex = styleAttr.indexOf("SeatStatus=") + 11;
        int seatStatus = Integer.valueOf(styleAttr.substring(beginIndex, beginIndex+1));

        switch (seatStatus){
            case 1:
                return Seat.Status.UNOCCUPIED;
            case 2:
                return Seat.Status.OCCUPIED;
            default:
                Log.e(LOG_TAG, "Bad seat status");
        }
        return null;
    }

    public boolean isRowEmpty(int row){
        for(Seat seat : map[row]){
            if(seat != null){
                return false;
            }
        }
        return true;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(rows);
        parcel.writeInt(cols);
        for(int row = 0; row < rows; row++){
            for(int col = 0; col < cols; col++){
                parcel.writeParcelable(map[row][col], 0);
            }
        }
    }
}
