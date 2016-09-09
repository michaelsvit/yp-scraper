package com.michaelsvit.kolnoa;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieDetailsFragment extends Fragment {
    private static final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();

    private Context context;
    private Movie movie;
    private List<Site> sites;
    private MovieSchedule schedule;

    private Spinner spinner;

    public MovieDetailsFragment() {
        // Required empty public constructor
    }

    public static MovieDetailsFragment newInstance() {
        MovieDetailsFragment fragment = new MovieDetailsFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MovieDetailsActivity) {
            MovieDetailsActivity activity = (MovieDetailsActivity) context;
            this.movie = activity.getMovie();
            this.sites = activity.getSites();
            this.schedule = activity.getSchedule();
        } else {
            Log.d(LOG_TAG, "Attached to activity different from MovieDetailsActivity");
        }
        this.context = context;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);

        spinner = (Spinner) rootView.findViewById(R.id.sites_spinner);
        ArrayAdapter<Site> spinnerAdapter = new ArrayAdapter<>(context, R.layout.sites_spinner_item, sites);
        spinner.setAdapter(spinnerAdapter);

        populateDetails(rootView);

        Button screeningsButton = (Button) rootView.findViewById(R.id.details_screening_button);
        screeningsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (schedule != null) {
                    Intent intent = new Intent(context, MovieScreeningsActivity.class);
                    Site selectedSite = (Site) spinner.getSelectedItem();
                    int siteId = selectedSite.getId();
                    ArrayList<MovieScreening> movieSchedule = (ArrayList<MovieScreening>) schedule.getMovieScheduleInSite(siteId);
                    if (movieSchedule != null) {
                        intent.putParcelableArrayListExtra(Cinema.SCHEDULE_ARG_NAME, movieSchedule);
                        intent.putExtra(Site.SITE_ARG_NAME, selectedSite);
                        startActivity(intent);
                    } else {
                        makeToast(R.string.no_screenings_in_site);
                    }
                } else {
                    makeToast(R.string.no_screenings_exist);
                }
            }
        });

        Button trailerButton = (Button) rootView.findViewById(R.id.details_trailer_button);
        trailerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String trailerURL = movie.getTrailerURL();
                if (trailerURL != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerURL));
                    if(intent.resolveActivity(context.getPackageManager()) != null){
                        startActivity(intent);
                    } else {
                        makeToast(R.string.unable_to_view_trailer);
                    }
                } else {
                    makeToast(R.string.trailer_does_not_exist);
                }
            }
        });

        return rootView;
    }

    private void makeToast(int textResourceID) {
        Toast toast = new Toast(context);
        toast.setText(textResourceID);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    private void populateDetails(View rootView) {
        TextView synopsisTextView = (TextView) rootView.findViewById(R.id.details_synopsis);
        synopsisTextView.setText(movie.getSynopsis());

        TextView releaseDateTextView = (TextView) rootView.findViewById(R.id.details_release_date);
        releaseDateTextView.setText(movie.getReleaseDate());

        ImageView posterImageView = (ImageView) rootView.findViewById(R.id.details_poster);
        Picasso.with(context)
                .load(movie.getPosterURL())
                .into(posterImageView);
    }
}
