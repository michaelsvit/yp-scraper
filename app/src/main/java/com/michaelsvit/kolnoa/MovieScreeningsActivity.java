package com.michaelsvit.kolnoa;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MovieScreeningsActivity extends AppCompatActivity {
    private ScheduleDataFragment dataFragment;
    private MovieScreeningsFragment screeningsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set locale to correspond to Hebrew
        Configuration configuration = getResources().getConfiguration();
        configuration.setLayoutDirection(new Locale("he"));
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());

        setContentView(R.layout.activity_movie_screenings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowTitleEnabled(false);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        dataFragment = (ScheduleDataFragment) fragmentManager.findFragmentByTag(ScheduleDataFragment.FRAGMENT_ARG_NAME);
        if (dataFragment == null) {
            dataFragment = new ScheduleDataFragment();
            fragmentManager.beginTransaction()
                    .add(dataFragment, ScheduleDataFragment.FRAGMENT_ARG_NAME)
                    .commit();
            Intent intent = getIntent();
            final List<MovieScreening> list = intent.getParcelableArrayListExtra(Cinema.SCHEDULE_ARG_NAME);
            dataFragment.setSchedule(splitToDays(list));
            dataFragment.setSite((Site) intent.getParcelableExtra(Site.SITE_ARG_NAME));
        }
        
        if (savedInstanceState == null) {
            screeningsFragment = MovieScreeningsFragment.newInstance(dataFragment.getSite(), dataFragment.getSchedule());
            fragmentManager
                    .beginTransaction()
                    .add(R.id.movie_screenings_fragment_container, screeningsFragment)
                    .commit();
        } else {
            screeningsFragment = (MovieScreeningsFragment) fragmentManager
                    .getFragment(savedInstanceState, MovieScreeningsFragment.FRAGMENT_ARG_NAME);
            screeningsFragment.setSite(dataFragment.getSite());
            screeningsFragment.setSchedule(dataFragment.getSchedule());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(
                outState,
                MovieScreeningsFragment.FRAGMENT_ARG_NAME,
                screeningsFragment
        );
    }

    private MovieScheduleInSite splitToDays(List<MovieScreening> list) {
        Map<String, List<MovieScreening>> splitList = new HashMap<>();
        Collections.sort(list, new Comparator<MovieScreening>() {
            @Override
            public int compare(MovieScreening s0, MovieScreening s1) {
                String time0 = s0.getTime();
                String hour0 = time0.split(":")[0];
                String time1 = s1.getTime();
                String hour1 = time1.split(":")[0];
                if (hour0.equals("00") ^ hour1.equals("00")) {
                    return time1.compareTo(time0);
                } else {
                    return time0.compareTo(time1);
                }
            }
        });

        for(MovieScreening movieScreening : list){
            String date = movieScreening.getDate();
            if(splitList.containsKey(date)){
                splitList.get(date).add(movieScreening);
            } else {
                List<MovieScreening> singleDayScreenings = new ArrayList<>();
                singleDayScreenings.add(movieScreening);
                splitList.put(date, singleDayScreenings);
            }
        }

        return new MovieScheduleInSite(splitList);
    }
}
