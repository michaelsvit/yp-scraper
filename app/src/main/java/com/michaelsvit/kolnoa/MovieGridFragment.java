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
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * A fragment representing a list of Items.
 */
public class MovieGridFragment extends Fragment {

    private static final String LOG_TAG = MovieGridFragment.class.getSimpleName();
    private static final String ARG_CINEMA = "cinema";
    private static final int columnCount = 2;

    private Cinema cinema;
    private MovieRecyclerViewAdapter adapter;

    private String moviesHTMLResponse;
    private WebView webView;
    private ProgressBar progressBar;

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
                case YES_PLANET:
                    cinema = new YesPlanet();
                    break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies_grid, container, false);

        Context context = rootView.getContext();
        progressBar = (ProgressBar) rootView.findViewById(R.id.fragment_movies_progress_bar);
        webView = (WebView) rootView.findViewById(R.id.fragment_movies_web_view);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_movies_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(context, columnCount));
        adapter = new MovieRecyclerViewAdapter(context, cinema);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        initWebView();
        fetchData();

        return rootView;
    }

    private void fetchData() {
        webView.loadUrl(cinema.getMoviesUrl());
    }

    private void initWebView() {
        class JSInterface{
            @JavascriptInterface
            public void saveMoviesHTML(String moviesHTML){
                if(moviesHTML.length() > 10000){
                    moviesHTMLResponse = moviesHTML;
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
                    new ParseResponse().execute(moviesHTMLResponse, scheduleJSONResponse);
                }
            }
        }

        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JSInterface(), "Android");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (moviesHTMLResponse == null) {
                    webView.loadUrl("javascript:window.Android.saveMoviesHTML("
                            + "'<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                } else {
                    webView.loadUrl("javascript:window.Android.processHTML(document.getElementsByTagName('html')[0].innerText);");
                }
            }
        });
    }

    private class ParseResponse extends AsyncTask<String, Void, Void> {
        private final String LOG_TAG = ParseResponse.class.getSimpleName();

        @Override
        protected void onPostExecute(Void blank) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                }
            });
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
