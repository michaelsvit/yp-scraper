package com.michaelsvit.kolnoa;

import android.content.Context;
import android.net.http.SslError;
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
import android.webkit.SslErrorHandler;
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
    private List<Movie> movies;

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

        movies = new ArrayList<>();
        // TODO: remove
        fetchMovies();
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
            recyclerView.setAdapter(new MovieRecyclerViewAdapter(movies));
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //fetchMovies();
    }

    private void fetchMovies() {
        class JSInterface{
            @JavascriptInterface
            public void processHTML(String html){
                populateMovies(html);
            }
        }
        final WebView webView = new WebView(getActivity());
        WebSettings settings = webView.getSettings();
        settings.setDomStorageEnabled(true);
        webView.addJavascriptInterface(new JSInterface(), "Android");

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(webView.getProgress() == 100){
                    webView.loadUrl("javascript:window.Android.processHTML("
                            + "'<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                }
            }
        });
        webView.setWebChromeClient(new WebChromeClient());
        settings.setJavaScriptEnabled(true);
        //webView.clearCache(true);
        //webView.clearHistory();
        webView.loadUrl(cinema.getUrl().toString());

    }

    private void populateMovies(String html) {
        Log.d("TEST", html);
    }

    private class FetchMoviesTask extends AsyncTask<Cinema, Void, String>{
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected String doInBackground(Cinema... cinemas) {
            // TODO: check internet connection

            /*try {
                Document document = Jsoup.connect("http://www.yesplanet.co.il/movies").get();
                Log.d("TEST", document.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            return null;
        }
    }
}
