package com.michaelsvit.kolnoa;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
