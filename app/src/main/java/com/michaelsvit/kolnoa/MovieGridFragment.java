package com.michaelsvit.kolnoa;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.michaelsvit.kolnoa.dummy.DummyContent;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A fragment representing a list of Items.
 */
public class MovieGridFragment extends Fragment {

    private static final String ARG_CINEMA = "cinema";

    private Cinema cinema;
    private int columnCount = 2;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieGridFragment() {
    }

    public static MovieGridFragment newInstance(Cinema cinema) {
        MovieGridFragment fragment = new MovieGridFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CINEMA, cinema);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            cinema = getArguments().getParcelable(ARG_CINEMA);
        }

        // TODO: remove
        new FetchMoviesTask().execute(cinema);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movies_grid, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new GridLayoutManager(context, columnCount));
            recyclerView.setAdapter(new MovieRecyclerViewAdapter(DummyContent.ITEMS));
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchMovies();
    }

    private void fetchMovies() {

    }

    private class FetchMoviesTask extends AsyncTask<Cinema, Void, String>{
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected String doInBackground(Cinema... cinemas) {
            // TODO: check internet connection
            if(cinemas.length == 0){
                return null;
            }

            URL url = cinema.getUrl();
            InputStream is = null;
            try {
                is = getInputStream(url);
                char[] buffer = getString(is);
                Log.d(LOG_TAG, new String(buffer));
                return new String(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        private char[] getString(InputStream is) throws IOException {
            Reader reader;
            reader = new InputStreamReader(is, "UTF-8");
            char[] buffer = new char[10000];
            reader.read(buffer);
            return buffer;
        }

        private InputStream getInputStream(URL url) throws IOException {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000); // milisec
            conn.setConnectTimeout(15000); // milisec
            conn.setRequestMethod("GET");
            conn.connect();
            return conn.getInputStream();
        }
    }
}
