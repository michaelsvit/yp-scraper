package com.michaelsvit.kolnoa;

/**
 * Created by Michael on 7/13/2016.
 */
public class Movie {
    public enum MovieStatus{
        IN_THEATRE, COMING_SOON
    }
    private String title;
    private String releaseDate;
    private String synopsis;
    private MovieStatus status;

    public Movie(String name, String releaseDate, String synopsis, MovieStatus status) {
        this.title = name;
        this.releaseDate = releaseDate;
        this.synopsis = synopsis;
        this.status = status;
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
}
