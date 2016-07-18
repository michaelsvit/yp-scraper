package com.michaelsvit.kolnoa;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Michael on 7/13/2016.
 * Represents a single movie object
 */
public class Movie implements Parcelable {
    public static final String ARG_NAME = "movie";

    public enum MovieStatus{
        IN_THEATRE, COMING_SOON
    }
    private String title;
    private String releaseDate;
    private String synopsis;
    private MovieStatus status;
    private String posterURL;

    public Movie(String name, String releaseDate, String synopsis, MovieStatus status, String posterURL) {
        this.title = name;
        this.releaseDate = releaseDate;
        this.synopsis = synopsis;
        this.status = status;
        this.posterURL = posterURL;
    }

    protected Movie(Parcel in) {
        title = in.readString();
        releaseDate = in.readString();
        synopsis = in.readString();
        posterURL = in.readString();
        status = (MovieStatus) in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(releaseDate);
        dest.writeString(synopsis);
        dest.writeString(posterURL);
        dest.writeSerializable(status);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public MovieStatus getStatus() {
        return status;
    }

    public String getPosterURL() {
        return posterURL;
    }
}
