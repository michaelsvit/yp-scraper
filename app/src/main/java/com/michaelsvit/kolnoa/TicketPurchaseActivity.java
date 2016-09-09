package com.michaelsvit.kolnoa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class TicketPurchaseActivity extends AppCompatActivity {

    private String screeningId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get screening ID from intent
        Intent intent = getIntent();
        screeningId = intent.getStringExtra(MovieScreening.ARG_NAME);

        setContentView(R.layout.activity_ticket_purchase);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(savedInstanceState == null){
            Fragment fragment = new ScreeningSeatMapFragment();

            Bundle args = new Bundle();
            args.putString(MovieScreening.ARG_NAME, screeningId);
            fragment.setArguments(args);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.ticket_fragment_container, fragment)
                    .commit();
        }
    }

}
