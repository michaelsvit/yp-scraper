package com.michaelsvit.kolnoa;

import android.content.Intent;
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
import java.util.Map;

public class MovieScreeningsActivity extends AppCompatActivity {
    private Map<String, List<MovieScreening>> schedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        final List<MovieScreening> list = intent.getParcelableArrayListExtra(Cinema.SCHEDULE_ARG_NAME);
        schedule = splitToDays(list);

        setContentView(R.layout.activity_movie_screenings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowTitleEnabled(false);
        }

        if(savedInstanceState == null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            MovieScreeningsFragment fragment = MovieScreeningsFragment.newInstance();
            fragmentManager
                    .beginTransaction()
                    .add(R.id.movie_screenings_fragment_container, fragment)
                    .commit();
        }
    }

    private Map<String, List<MovieScreening>> splitToDays(List<MovieScreening> list) {
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

        return splitList;
    }

    public Map<String, List<MovieScreening>> getSchedule() {
        return schedule;
    }
}
