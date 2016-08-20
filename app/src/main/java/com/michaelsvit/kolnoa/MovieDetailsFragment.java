package com.michaelsvit.kolnoa;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieDetailsFragment extends Fragment {
    private static final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();

    private Context context;
    private Movie movie;
    private List<MovieScreening> schedule;

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
            this.schedule = activity.getSchedule();
        } else {
            Log.d(LOG_TAG, "Attached to activity different from MovieDetailsActivity");
        }
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);

        populateDetails(rootView);

        Button button = (Button) rootView.findViewById(R.id.details_screening_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (schedule != null) {
                    Intent intent = new Intent(context, MovieScreeningsActivity.class);
                    intent.putParcelableArrayListExtra(Cinema.SCHEDULE_ARG_NAME, (ArrayList<MovieScreening>)schedule);
                    startActivity(intent);
                } else {
                    Toast toast = new Toast(context);
                    toast.setText(R.string.no_screenings_exist);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        return rootView;
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
