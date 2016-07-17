package com.michaelsvit.kolnoa;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Represents a single cinema
 */
public class Cinema implements Parcelable{
    private String name;
    private String url;
    private HTMLParser htmlParser;

    public Cinema(String name, String url, HTMLParser htmlParser) {
        this.name = name;
        this.url = url;
        this.htmlParser = htmlParser;
    }

    protected Cinema(Parcel in) {
        name = in.readString();
        url = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(url);
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
    public String getUrl() {
        final String moviesPath = "movies";
        return url + moviesPath;
    }

    public String getPosterUrl(Movie movie) {
        return url + movie.getPosterURL();
    }

    public List<Movie> getMoviesFromHTML(String html) {
        return htmlParser.parse(html);
    }
}
