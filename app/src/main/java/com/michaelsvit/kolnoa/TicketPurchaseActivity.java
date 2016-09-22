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

        if(savedInstanceState == null){
            Fragment fragment = new ScreeningSeatMapFragment();

            Bundle args = new Bundle();
            args.putString(MovieScreening.ARG_NAME, screeningId);
            args.putString(Site.TICKETS_URL_ARG_NAME, siteTicketsUrl);
            fragment.setArguments(args);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.ticket_fragment_container, fragment)
                    .commit();
        }
    }

}
