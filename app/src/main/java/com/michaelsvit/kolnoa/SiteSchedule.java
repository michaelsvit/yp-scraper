package com.michaelsvit.kolnoa;

import java.util.List;
import java.util.Map;

/**
 * Entire schedule of all movies in a single site
 */
public class SiteSchedule {
    Map<String, List<MovieScreening>> schedule;

    public SiteSchedule(Map<String, List<MovieScreening>> schedule) {
        this.schedule = schedule;
    }

    public List<MovieScreening> getMovieSchedule(String movieId){
        return schedule.get(movieId);
    }
}
