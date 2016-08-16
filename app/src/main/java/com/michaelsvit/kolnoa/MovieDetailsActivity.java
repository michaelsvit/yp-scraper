package com.michaelsvit.kolnoa;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieDetailsActivity extends AppCompatActivity {
    private Movie movie;
    private List<MovieScreening> schedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();
        movie = intent.getParcelableExtra(Movie.ARG_NAME);
        schedule = intent.getParcelableArrayListExtra(Cinema.SCHEDULE_ARG_NAME);

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

        populateDetails();
    }

    private void populateDetails() {
        TextView synopsisTextView = (TextView) findViewById(R.id.details_synopsis);
        synopsisTextView.setText(movie.getSynopsis());

        TextView releaseDateTextView = (TextView) findViewById(R.id.details_release_date);
        releaseDateTextView.setText(movie.getReleaseDate());

        ImageView posterImageView = (ImageView) findViewById(R.id.details_poster);
        Picasso.with(this)
                .load(movie.getPosterURL())
                .into(posterImageView);
    }
}
