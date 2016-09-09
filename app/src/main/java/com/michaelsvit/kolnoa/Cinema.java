package com.michaelsvit.kolnoa;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single cinema
 */
public abstract class Cinema{
    public static final String SCHEDULE_ARG_NAME = "schedule";
    public static final String SITES_ARG_NAME = "sites";

    public enum CinemaName{
        YES_PLANET
    }

    private String name;
    private String url;
    private String moviesUrl;
    private String scheduleUrl;
    private CinemaDataParser cinemaDataParser;

    private List<Movie> movies;
    private Schedule schedule;

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

    public MovieSchedule getMovieSchedule(String movieId){
        return new MovieSchedule(schedule.getMovieSchedule(movieId));
    }

    public List<Site> getSites(){
        return schedule.getSites();
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
