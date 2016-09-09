package com.michaelsvit.kolnoa;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieRecyclerViewAdapter extends RecyclerView.Adapter<MovieRecyclerViewAdapter.ViewHolder> {
    private final Cinema cinema;
    private final Context context;
    private final DisplayMetrics displayMetrics;
    private final Picasso picasso;

    public MovieRecyclerViewAdapter(Context context, Cinema cinema) {
        this.context = context;
        this.cinema = cinema;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);

        // Set up Picasso with okHttp3
        okhttp3.OkHttpClient okHttp3Client = new okhttp3.OkHttpClient();
        OkHttp3Downloader okHttp3Downloader = new OkHttp3Downloader(okHttp3Client);
        picasso = new Picasso.Builder(context)
                .downloader(okHttp3Downloader)
                .build();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_movies, parent, false);
        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Movie movie = cinema.getMovie(position);
        int imgViewWidth = displayMetrics.widthPixels / 2;
        int imgViewHeight = (int)(imgViewWidth * 1.4);
        picasso.with(context)
                .load(movie.getPosterURL())
                .resize(imgViewWidth, imgViewHeight)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return cinema.getMoviesCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Movie movie = cinema.getMovie(position);
                    Intent intent = new Intent(context, MovieDetailsActivity.class);
                    intent.putExtra(Movie.ARG_NAME, movie);
                    intent.putParcelableArrayListExtra(Cinema.SITES_ARG_NAME,
                            (ArrayList<Site>) cinema.getSites());
                    intent.putExtra(Cinema.SCHEDULE_ARG_NAME, cinema.getMovieSchedule(movie.getId()));
                    context.startActivity(intent);
                }
            });
        }
    }
}
