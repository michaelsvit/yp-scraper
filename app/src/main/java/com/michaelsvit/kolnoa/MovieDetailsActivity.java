package com.michaelsvit.kolnoa;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.List;
import java.util.Locale;

public class MovieDetailsActivity extends AppCompatActivity {
    private static final String LOG_TAG = MovieDetailsActivity.class.getSimpleName();

    MovieDetailsFragment detailsFragment;

    private Movie movie;
    private List<Site> sites;
    private MovieSchedule schedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setLayoutRTL();
        getDataFromIntent(savedInstanceState != null);

        setContentView(R.layout.activity_movie_details);
        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolbarLayout.setTitle(movie.getTitle());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            detailsFragment = MovieDetailsFragment.newInstance(movie, sites, schedule);
            fragmentManager.beginTransaction()
                    .add(R.id.movie_details_fragment_container, detailsFragment)
                    .commit();
        } else {
            detailsFragment = (MovieDetailsFragment) fragmentManager.getFragment(savedInstanceState, MovieDetailsFragment.FRAGMENT_ARG_NAME);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, MovieDetailsFragment.FRAGMENT_ARG_NAME, detailsFragment);
    }

    private void setLayoutRTL() {
        // Set locale to correspond to Hebrew
        Configuration configuration = getResources().getConfiguration();
        configuration.setLayoutDirection(new Locale("he"));
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
    }

    private void getDataFromIntent(boolean restoringState) {
        final Intent intent = getIntent();
        movie = intent.getParcelableExtra(Movie.ARG_NAME);
        if (!restoringState) {
            sites = intent.getParcelableArrayListExtra(Cinema.SITES_ARG_NAME);
            schedule = intent.getParcelableExtra(Cinema.SCHEDULE_ARG_NAME);
        }
    }
}
