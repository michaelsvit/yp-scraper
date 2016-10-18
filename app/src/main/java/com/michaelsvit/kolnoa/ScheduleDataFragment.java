package com.michaelsvit.kolnoa;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Retained fragment to keep movie schedule in specific site on orientation change
 */
public class ScheduleDataFragment extends Fragment {
    public static final String FRAGMENT_ARG_NAME = "schedule_data_fragment";

    private Site site;
    private MovieScheduleInSite schedule;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public MovieScheduleInSite getSchedule() {
        return schedule;
    }

    public void setSchedule(MovieScheduleInSite schedule) {
        this.schedule = schedule;
    }
}
