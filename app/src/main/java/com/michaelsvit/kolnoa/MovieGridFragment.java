package com.michaelsvit.kolnoa;

import android.content.Context;
import android.content.res.Configuration;
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
        int columns = getColumns();
        setupRecyclerView(rootView, context, columns);

        return rootView;
    }

    private int getColumns() {
        int orientation = getActivity().getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            return 2;
        } else {
            return 3;
        }
    }

    private void setupRecyclerView(View rootView, Context context, int columns) {
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_movies_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(context, columns));
        adapter = new MovieRecyclerViewAdapter(context, cinema);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

    public void setCinema(Cinema cinema) {
        this.cinema = cinema;
    }
}
