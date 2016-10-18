package com.michaelsvit.kolnoa;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Simple encapsulation type
 */
public class MovieScheduleInSite {
    private Map<String, List<MovieScreening>> schedule;

    public MovieScheduleInSite(Map<String, List<MovieScreening>> schedule) {
        this.schedule = schedule;
    }

    public List<MovieScreening> getScreeningsList(String date) {
        return schedule.get(date);
    }

    public Set<String> getDatesSet() {
        return schedule.keySet();
    }

}
