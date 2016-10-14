package com.michaelsvit.kolnoa;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A fragment representing a list of Items.
 */
public class MovieGridFragment extends Fragment {

    public static final String FRAGMENT_ARG_NAME = "movie_grid_fragment";
    private static final String LOG_TAG = MovieGridFragment.class.getSimpleName();
    private static final int columnCount = 2;

    private Cinema cinema;
    private MovieRecyclerViewAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieGridFragment() {
    }

    public static MovieGridFragment newInstance(Cinema cinema) {
        MovieGridFragment fragment = new MovieGridFragment();
        fragment.setCinema(cinema);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies_grid, container, false);

        Context context = rootView.getContext();

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_movies_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(context, columnCount));
        adapter = new MovieRecyclerViewAdapter(context, cinema);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        return rootView;
    }

    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

    public void setCinema(Cinema cinema) {
        this.cinema = cinema;
    }
}
