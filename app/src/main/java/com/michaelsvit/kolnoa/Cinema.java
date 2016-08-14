package com.michaelsvit.kolnoa;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Represents a single cinema
 */
public class Cinema implements Parcelable{
    public static final String ARG_NAME = "cinema";

    private String name;
    private String moviesUrl;
    private String scheduleUrl;
    private HTMLParser htmlParser;

    public Cinema(String name, String moviesUrl, String scheduleUrl, HTMLParser htmlParser) {
        this.name = name;
        this.moviesUrl = moviesUrl;
        this.scheduleUrl = scheduleUrl;
        this.htmlParser = htmlParser;
    }

    protected Cinema(Parcel in) {
        name = in.readString();
        moviesUrl = in.readString();
        scheduleUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(moviesUrl);
        dest.writeString(scheduleUrl);
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

    // Movies source
    public String getMoviesUrl() {
        final String moviesPath = "movies";
        return moviesUrl + moviesPath;
    }

    public String getPosterUrl(Movie movie) {
        return moviesUrl + movie.getPosterURL();
    }

    public List<Movie> getMoviesFromHTML(String html) {
        final List<Movie> movies = htmlParser.parse(html);

        return movies;
    }
}
