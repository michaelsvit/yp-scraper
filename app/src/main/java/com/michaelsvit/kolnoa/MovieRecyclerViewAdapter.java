package com.michaelsvit.kolnoa;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MovieRecyclerViewAdapter extends RecyclerView.Adapter<MovieRecyclerViewAdapter.ViewHolder> {

    private final List<Movie> movies;

    public MovieRecyclerViewAdapter(List<Movie> movies) {
        this.movies = movies;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_movies, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.movie = movie;
        holder.view.setText(movie.getTitle());
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView view;
        public Movie movie;

        public ViewHolder(View view) {
            super(view);
            this.view = (TextView) view;
        }
    }
}
