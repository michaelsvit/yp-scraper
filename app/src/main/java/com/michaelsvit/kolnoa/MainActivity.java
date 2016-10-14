package com.michaelsvit.kolnoa;

import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private MovieGridFragment movieGridFragment;
    private CinemaRetainFragment cinemaFragment;

    private ProgressBar progressBar;

    private Cinema cinema;
    private String moviesHTMLResponse;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayoutRTL();


        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        progressBar = (ProgressBar) findViewById(R.id.fragment_movies_progress_bar);
        webView = (WebView) findViewById(R.id.fragment_movies_web_view);

        FragmentManager manager = getSupportFragmentManager();
        cinemaFragment = (CinemaRetainFragment) manager.findFragmentByTag(CinemaRetainFragment.FRAGMENT_ARG_NAME);
        if (cinemaFragment == null) {
            cinemaFragment = new CinemaRetainFragment();
            manager.beginTransaction().add(cinemaFragment, CinemaRetainFragment.FRAGMENT_ARG_NAME).commit();
            cinema = new YesPlanet();
            cinemaFragment.setCinema(cinema);
        } else {
            cinema = cinemaFragment.getCinema();
        }

        // TODO: move this to drawer logic
        if (savedInstanceState == null) {
            initWebView();
            fetchData();

            movieGridFragment = MovieGridFragment.newInstance(cinema);
            manager.beginTransaction()
                    .add(R.id.main_container, movieGridFragment)
                    .commit();
        } else {
            movieGridFragment = (MovieGridFragment) manager.getFragment(savedInstanceState, MovieGridFragment.FRAGMENT_ARG_NAME);
            movieGridFragment.setCinema(cinema);
            hideProgressBar();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        FragmentManager manager = getSupportFragmentManager();
        manager.putFragment(outState, MovieGridFragment.FRAGMENT_ARG_NAME, movieGridFragment);
    }

    private void setLayoutRTL() {
        // Set locale to correspond to Hebrew
        Configuration configuration = getResources().getConfiguration();
        configuration.setLayoutDirection(new Locale("he"));
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        /*if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideProgressBar();
                }
            });
            movieGridFragment.notifyDataSetChanged();
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

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }
}
