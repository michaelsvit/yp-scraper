package com.michaelsvit.kolnoa;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class MovieGridFragment extends Fragment {

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

        // TODO: remove
        fetchMovies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movies_grid, container, false);

        // Set the adapter
        /*if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new GridLayoutManager(context, columnCount));
            recyclerView.setAdapter(new MovieRecyclerViewAdapter(movies));
        }*/
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
                Log.d("TEST", html);
            }
        }
        final WebView webView = new WebView(getActivity());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JSInterface(), "Android");

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
        webView.loadUrl(cinema.getUrl().toString());

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
