package com.michaelsvit.kolnoa;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.Locale;

public class TicketPurchaseActivity extends AppCompatActivity {
    Fragment seatMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set locale to correspond to Hebrew
        Configuration configuration = getResources().getConfiguration();
        configuration.setLayoutDirection(new Locale("he"));
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());

        // Get screening ID from intent
        Intent intent = getIntent();
        String screeningId = intent.getStringExtra(MovieScreening.ARG_NAME);
        String siteTicketsUrl = intent.getStringExtra(Site.TICKETS_URL_ARG_NAME);

        setContentView(R.layout.activity_ticket_purchase);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            seatMapFragment = new ScreeningSeatMapFragment();

            Bundle args = new Bundle();
            args.putString(MovieScreening.ARG_NAME, screeningId);
            args.putString(Site.TICKETS_URL_ARG_NAME, siteTicketsUrl);
            seatMapFragment.setArguments(args);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.ticket_fragment_container, seatMapFragment)
                    .commit();
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            seatMapFragment = fragmentManager.getFragment(
                    savedInstanceState,
                    ScreeningSeatMapFragment.FRAGMENT_ARG_NAME);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(
                outState,
                ScreeningSeatMapFragment.FRAGMENT_ARG_NAME,
                seatMapFragment);
    }
}
