package com.michaelsvit.kolnoa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Entire schedule of all movies in all sites
 */
public class Schedule {
    private List<Site> sites;
    private Map<Integer, SiteSchedule> siteScheduleMap;

    public Schedule(List<Site> sites, Map<Integer, SiteSchedule> siteScheduleMap) {
        this.sites = sites;
        this.siteScheduleMap = siteScheduleMap;
    }

    public List<Site> getSites() {
        return sites;
    }

    public Map<Integer, List<MovieScreening>> getMovieSchedule(String movieId){
        Map<Integer, List<MovieScreening>> schedule = new HashMap<>();

        for(Site site : sites){
            int siteId = site.getId();
            schedule.put(siteId, siteScheduleMap.get(siteId).getMovieSchedule(movieId));
        }

        return schedule;
    }
}
