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
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class MovieGridFragment extends Fragment {

    private static final String LOG_TAG = MovieGridFragment.class.getSimpleName();
    private static final String ARG_CINEMA = "cinema";

    private int columnCount = 2;
    private Cinema cinema;

    ArrayList<Movie> movies;
    MovieRecyclerViewAdapter adapter;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movies_grid, container, false);

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fragment_movies_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(context, columnCount));
        movies = new ArrayList<>();
        adapter = new MovieRecyclerViewAdapter(movies);
        recyclerView.setAdapter(adapter);

        fetchMovies(view);

        return view;
    }

    private void fetchMovies(View view) {
        class JSInterface{
            @JavascriptInterface
            public void processHTML(String html){
                if(html.length() > 10000){
                    new ParseHTMLResponse().execute(html);
                }
            }
        }
        final WebView webView = (WebView) view.findViewById(R.id.fragment_movies_web_view);
        WebSettings settings = webView.getSettings();
        settings.setDomStorageEnabled(true);
        webView.addJavascriptInterface(new JSInterface(), "Android");
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(webView.getProgress() == 100){
                    webView.loadUrl("javascript:window.Android.processHTML("
                            + "'<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                }
            }
        });

        settings.setJavaScriptEnabled(true);
        webView.loadUrl(cinema.getUrl().toString());
    }

    private class ParseHTMLResponse extends AsyncTask<String, Void, List<Movie>> {
        private final String LOG_TAG = ParseHTMLResponse.class.getSimpleName();

        @Override
        protected void onPostExecute(List<Movie> movies) {
            MovieGridFragment.this.movies.addAll(movies);
            adapter.notifyItemRangeInserted(0, movies.size());
        }

        @Override
        protected List<Movie> doInBackground(String... params) {
            if(params.length > 0) {
                return cinema.getMoviesFromHTML(params[0]);
            } else {
                Log.e(LOG_TAG, "Error parsing HTML.");
                return null;
            }
        }
    }
}
