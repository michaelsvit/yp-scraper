package com.michaelsvit.kolnoa;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieRecyclerViewAdapter extends RecyclerView.Adapter<MovieRecyclerViewAdapter.ViewHolder> {
    private final Cinema cinema;
    private final List<Movie> movies;
    private final Context context;
    private final DisplayMetrics displayMetrics;

    public MovieRecyclerViewAdapter(Context context, Cinema cinema, List<Movie> movies) {
        this.context = context;
        this.cinema = cinema;
        this.movies = movies;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
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
        int imgViewWidth = displayMetrics.widthPixels / 2;
        Picasso.with(context)
                .load(cinema.getPosterUrl(movie.getPosterURL()))
                .resize(imgViewWidth, 0)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view;
        }
    }
}
