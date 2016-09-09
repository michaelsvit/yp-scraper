package com.michaelsvit.kolnoa;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Schedule for a single movie in all sites
 */
public class MovieSchedule implements Parcelable {
    private Map<Integer, List<MovieScreening>> movieSchedule;

    public MovieSchedule(Map<Integer, List<MovieScreening>> movieSchedule) {
        this.movieSchedule = movieSchedule;
    }

    protected MovieSchedule(Parcel in) {
        Map<Integer, List<MovieScreening>> movieSchedule = new HashMap<>();
        int mapSize = in.readInt();
        for(int i = 0; i < mapSize; i++){
            Integer key = in.readInt();
            List<MovieScreening> val = new ArrayList<>();
            in.readList(val, MovieScreening.class.getClassLoader());
            movieSchedule.put(key, val);
        }
        this.movieSchedule = movieSchedule;
    }

    public static final Creator<MovieSchedule> CREATOR = new Creator<MovieSchedule>() {
        @Override
        public MovieSchedule createFromParcel(Parcel in) {
            return new MovieSchedule(in);
        }

        @Override
        public MovieSchedule[] newArray(int size) {
            return new MovieSchedule[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeInt(movieSchedule.size());
        for(Map.Entry<Integer, List<MovieScreening>> entry : movieSchedule.entrySet()){
            out.writeInt(entry.getKey());
            out.writeList(entry.getValue());
        }
    }

    public List<MovieScreening> getMovieScheduleInSite(int siteId){
        return movieSchedule.get(siteId);
    }
}
