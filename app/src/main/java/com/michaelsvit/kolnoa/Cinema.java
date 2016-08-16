package com.michaelsvit.kolnoa;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents a single cinema
 */
public abstract class Cinema{
    public static final String SCHEDULE_ARG_NAME = "schedule";

    public enum CinemaName{
        YESPLANET
    }

    private String name;
    private String url;
    private String moviesUrl;
    private String scheduleUrl;
    private CinemaDataParser cinemaDataParser;

    private List<Movie> movies;
    private Map<String, List<MovieScreening>> schedule;

    public Cinema(String name, String url, String moviesUrl, String scheduleUrl, CinemaDataParser cinemaDataParser) {
        this.name = name;
        this.url = url;
        this.moviesUrl = moviesUrl;
        this.scheduleUrl = scheduleUrl;
        this.cinemaDataParser = cinemaDataParser;
        movies = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getMoviesUrl() {
        return moviesUrl;
    }

    public String getScheduleUrl() {
        return scheduleUrl;
    }

    public Movie getMovie(int position){
        return movies.get(position);
    }

    public List<MovieScreening> getMovieSchedule(String movieId){
        return schedule.get(movieId);
    }

    public int getMoviesCount(){
        return movies.size();
    }

    public void updateMovies(String html){
        movies.addAll(cinemaDataParser.parseMoviesHTML(html));
    }

    public void updateSchedule(String json){
        schedule = cinemaDataParser.parseScheduleJSON(json);
    }
}
