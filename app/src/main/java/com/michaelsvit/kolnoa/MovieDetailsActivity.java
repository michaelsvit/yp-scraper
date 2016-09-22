package com.michaelsvit.kolnoa;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.List;
import java.util.Locale;

public class MovieDetailsActivity extends AppCompatActivity {
    private static final String LOG_TAG = MovieDetailsActivity.class.getSimpleName();

    private Movie movie;
    private List<Site> sites;
    private MovieSchedule schedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set locale to correspond to Hebrew
        Configuration configuration = getResources().getConfiguration();
        configuration.setLayoutDirection(new Locale("he"));
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());

        final Intent intent = getIntent();
        movie = intent.getParcelableExtra(Movie.ARG_NAME);
        sites = intent.getParcelableArrayListExtra(Cinema.SITES_ARG_NAME);
        schedule = intent.getParcelableExtra(Cinema.SCHEDULE_ARG_NAME);

        setContentView(R.layout.activity_movie_details);
        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolbarLayout.setTitle(movie.getTitle());
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
        final ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        if(savedInstanceState == null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            MovieDetailsFragment fragment = MovieDetailsFragment.newInstance();
            fragmentManager
                    .beginTransaction()
                    .add(R.id.movie_details_fragment_container, fragment)
                    .commit();
        }
    }

    public List<Site> getSites() {
        return sites;
    }

    public MovieSchedule getSchedule() {
        return schedule;
    }

    public Movie getMovie() {
        return movie;
    }
}
