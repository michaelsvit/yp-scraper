package com.michaelsvit.kolnoa;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * A fragment representing a list of Items.
 */
public class MovieGridFragment extends Fragment {

    private static final String LOG_TAG = MovieGridFragment.class.getSimpleName();
    private static final String ARG_CINEMA = "cinema";
    private static final int columnCount = 2;

    private Cinema cinema;
    private MovieRecyclerViewAdapter adapter;

    private String moviesHTMLRespone;
    private WebView webView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieGridFragment() {
    }

    public static MovieGridFragment newInstance(Cinema.CinemaName cinema) {
        MovieGridFragment fragment = new MovieGridFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CINEMA, cinema);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Cinema.CinemaName cinemaName = (Cinema.CinemaName) getArguments().getSerializable(ARG_CINEMA);
            switch(cinemaName){
                case YESPLANET:
                    cinema = new YesPlanet();
                    break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movies_grid, container, false);

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fragment_movies_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(context, columnCount));
        adapter = new MovieRecyclerViewAdapter(context, cinema);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        initWebView(view);
        fetchData();

        return view;
    }

    private void fetchData() {
        webView.loadUrl(cinema.getMoviesUrl());
    }

    @NonNull
    private void initWebView(View view) {
        class JSInterface{
            @JavascriptInterface
            public void saveMoviesHTML(String moviesHTML){
                if(moviesHTML.length() > 10000){
                    moviesHTMLRespone = moviesHTML;
                    webView.post(new Runnable(){
                        @Override
                        public void run() {
                            webView.loadUrl(cinema.getScheduleUrl());
                        }
                    });
                }
            }

            @JavascriptInterface
            public void processHTML(String scheduleJSONResponse){
                if(scheduleJSONResponse.length() > 10000){
                    new ParseResponse().execute(moviesHTMLRespone, scheduleJSONResponse);
                }
            }
        }
        webView = (WebView) view.findViewById(R.id.fragment_movies_web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JSInterface(), "Android");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (moviesHTMLRespone == null) {
                    webView.loadUrl("javascript:window.Android.saveMoviesHTML("
                            + "'<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                } else {
                    webView.loadUrl("javascript:window.Android.processHTML(document.getElementsByTagName('html')[0].textContent);");
                }
            }
        });
    }

    private class ParseResponse extends AsyncTask<String, Void, Void> {
        private final String LOG_TAG = ParseResponse.class.getSimpleName();

        @Override
        protected void onPostExecute(Void blank) {
            adapter.notifyDataSetChanged();
        }

        @Override
        protected Void doInBackground(String... params) {
            if(params.length > 1) {
                cinema.updateMovies(params[0]);
                cinema.updateSchedule(params[1]);
                return null;
            } else {
                Log.e(LOG_TAG, "Error parsing response.");
                return null;
            }
        }
    }
}
