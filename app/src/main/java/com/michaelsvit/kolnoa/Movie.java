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
    private String id;
    private String title;
    private String releaseDate;
    private String synopsis;
    private MovieStatus status;
    private String posterURL;
    private String trailerURL;

    public Movie(String id, String name, String releaseDate, String synopsis,
                 MovieStatus status, String posterURL, String trailerURL) {
        this.id = id;
        this.title = name;
        this.releaseDate = releaseDate;
        this.synopsis = synopsis;
        this.status = status;
        this.posterURL = posterURL;
        this.trailerURL = trailerURL;
    }

    protected Movie(Parcel in) {
        id = in.readString();
        title = in.readString();
        releaseDate = in.readString();
        synopsis = in.readString();
        posterURL = in.readString();
        trailerURL = in.readString();
        status = (MovieStatus) in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(releaseDate);
        dest.writeString(synopsis);
        dest.writeString(posterURL);
        dest.writeString(trailerURL);
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

    public String getId() {
        return id;
    }

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

    public String getTrailerURL() {
        return trailerURL;
    }
}
