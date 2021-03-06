package com.michaelsvit.kolnoa;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Michael on 7/14/2016.
 * Used to parse HTML from YesPlanet website
 */
public class YesPlanetDataParser implements CinemaDataParser {
    private static final String LOG_TAG = YesPlanetDataParser.class.getSimpleName();
    @Override
    public List<Movie> parseMoviesHTML(String html) {
        // List to be returned
        List<Movie> movies = new ArrayList<>();
        Document doc = Jsoup.parse(html);

        Element content = null;
        while (content == null) {
            content = doc.getElementsByClass("catData").first();
        }
        Elements htmlMovies = content.getElementsByTag("li");

        for(Element htmlMovie : htmlMovies){
            Movie movie = getMovie(htmlMovie);
            if (movie != null) {
                movies.add(movie);
            }
        }

        return movies;
    }

    private Movie getMovie(Element htmlMovie) {
        if(!isMovie(htmlMovie)){
            return null;
        }

        final String IN_THEATRE = "cat_0";
        final String COMING_SOON = "cat_1";
        final String DATA_CLASS_NAME = "extended";
        final String ID_ATTR_NAME = "data-distribcode";
        final String TITLE_CLASS_NAME = "featureTitle";
        final String RELEASE_DATE_CLASS_NAME = "releaseDate";
        final String SYNOPSIS_CLASS_NAME = "synopsis";
        final String POSTER_CLASS_NAME = "catPoster";
        final String TRAILER_CLASS_NAME = "featureTrailerLink";

        // Data
        Element dataElem = htmlMovie.getElementsByClass(DATA_CLASS_NAME).first();
        String featureId = dataElem.attr(ID_ATTR_NAME);

        // Title
        Element featureTitleElem = htmlMovie.getElementsByClass(TITLE_CLASS_NAME).first();
        String featureTitle;
        if (featureTitleElem != null) {
            featureTitle = featureTitleElem.ownText();
        } else {
            return null;
        }

        // Release date
        Element releaseDateElem = htmlMovie.getElementsByClass(RELEASE_DATE_CLASS_NAME).first();
        String releaseDate = null;
        if (releaseDateElem != null) {
            releaseDate = releaseDateElem.ownText();
        }

        // Synopsis
        Element synopsisElem = htmlMovie.getElementsByClass(SYNOPSIS_CLASS_NAME).first();
        String synopsis = null;
        if (synopsisElem != null) {
            synopsis = synopsisElem.ownText();
        }

        // Status
        Set<String> classNames = htmlMovie.classNames();
        Movie.MovieStatus status;
        if(classNames.contains(IN_THEATRE)){
            status = Movie.MovieStatus.IN_THEATRE;
        } else if(classNames.contains(COMING_SOON)){
            status = Movie.MovieStatus.COMING_SOON;
        } else {
            return null;
        }

        // Poster
        Element posterURLElem = htmlMovie.getElementsByClass(POSTER_CLASS_NAME).first();
        String posterURL = null;
        if (posterURLElem != null) {
            final String attributeKey = "data-src";
            final String posterRelativePath = posterURLElem.attr(attributeKey);
            posterURL = buildPosterURL(posterRelativePath);
        }

        // Trailer
        Element trailerURLElem = htmlMovie.getElementsByClass(TRAILER_CLASS_NAME).first();
        String trailerURL = null;
        if(trailerURLElem != null) {
            final String attributeKey = "href";
            final String attr = trailerURLElem.attr(attributeKey);
            trailerURL = buildTrailerURL(extractVideoCode(attr));
        }

        return new Movie(featureId, featureTitle, releaseDate, synopsis, status, posterURL, trailerURL);
    }

    private String extractVideoCode(String attr) {
        return attr.substring(attr.indexOf("'")+1, attr.lastIndexOf("'"));
    }

    private String buildPosterURL(String relativePath) {
        final String SCHEME = "http";
        final String AUTHORITY = "www.yesplanet.co.il";

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(AUTHORITY)
                .appendEncodedPath(relativePath);
        return builder.build().toString();
    }

    private String buildTrailerURL(String videoCode) {
        final String SCHEME = "https";
        final String AUTHORITY = "www.youtube.com";
        final String WATCH_PATH = "watch";
        final String VIDEO_CODE_PARAM = "v";

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(WATCH_PATH)
                .appendQueryParameter(VIDEO_CODE_PARAM, videoCode);
        return builder.build().toString();
    }

    private boolean isMovie(Element htmlMovie) {
        final String MOVIE_CLASS = "featureItem";
        return htmlMovie.className().contains(MOVIE_CLASS);
    }

    @Override
    public Schedule parseScheduleJSON(String json) {
        final String SITES_ARRAY = "sites";
        final String LANGUAGES_ARRAY = "languages";
        final String SCREENING_TYPE_ARRAY = "venueTypes";
        final String SITE_NAME_FIELD = "sn";
        final String SITE_TICKETS_URL_FIELD = "tu";
        final String SITE_FEATURES_ARRAY = "fe";
        final String SITE_ID_FIELD = "si";
        final String FEATURE_ID_FIELD = "dc";
        final String FEATURE_LANGUAGE_FIELD = "ol";
        final String FEATURE_SCREENINGS_ARRAY = "pr";
        final String SCREENING_DATE_FIELD = "dt";
        final String SCREENING_TIME_FIELD = "tm";
        final String SCREENING_ID_FIELD = "pc";
        final String SCREENING_TYPE_FIELD = "vt";

        List<Site> sites = new ArrayList<>();
        Map<Integer, SiteSchedule> siteScheduleMap = new HashMap<>();
        try {
            JSONObject root = new JSONObject(json);
            JSONArray languages = root.getJSONArray(LANGUAGES_ARRAY);
            JSONArray screeningTypes = root.getJSONArray(SCREENING_TYPE_ARRAY);
            JSONArray sitesArray = root.getJSONArray(SITES_ARRAY);

            for(int k = 0; k < sitesArray.length(); k++){
                JSONObject site = sitesArray.getJSONObject(k);

                int siteId = site.getInt(SITE_ID_FIELD);
                String siteNameString = site.getString(SITE_NAME_FIELD);
                String siteName;
                if (badEncoding(siteNameString)) {
                    siteName = matchSiteNameById(siteId);
                } else {
                    siteName = siteNameString;
                }
                String siteTicketsUrl = null;
                // Some sites have no such field
                if (site.has(SITE_TICKETS_URL_FIELD)) {
                    siteTicketsUrl = site.getString(SITE_TICKETS_URL_FIELD);
                }
                Map<String, List<MovieScreening>> siteSchedule = new HashMap<>();

                JSONArray siteMovies = site.getJSONArray(SITE_FEATURES_ARRAY);
                for(int i = 0; i < siteMovies.length(); i++){
                    JSONObject movie = siteMovies.getJSONObject(i);

                    // Movie details
                    String movieId = movie.getString(FEATURE_ID_FIELD);
                    int movieLanguage = 0;
                    if(movie.has(FEATURE_LANGUAGE_FIELD)) {
                        movieLanguage = movie.getInt(FEATURE_LANGUAGE_FIELD);
                    } else {
                        Log.e(LOG_TAG, "Cannot retrieve language for movie ID: " + movieId);
                    }

                    JSONArray screenings = movie.getJSONArray(FEATURE_SCREENINGS_ARRAY);
                    List<MovieScreening> movieScreenings = new ArrayList<>(screenings.length());
                    for(int j = 0; j < screenings.length(); j++){
                        JSONObject screening = screenings.getJSONObject(j);

                        // Screening details
                        String date = extractDate(screening.getString(SCREENING_DATE_FIELD));
                        String time = screening.getString(SCREENING_TIME_FIELD);
                        String id = screening.getString(SCREENING_ID_FIELD);
                        String type = screeningTypes.getString(screening.getInt(SCREENING_TYPE_FIELD));
                        int hallNumber = extractHallNumber(id);

                        movieScreenings.add(new MovieScreening(date, time, id,
                                type, hallNumber));
                    }

                    siteSchedule.put(movieId, movieScreenings);
                }

                // If site has no tickets url field, try to extract it from movie presentation code
                if(siteTicketsUrl == null && siteSchedule.size() > 0){
                    siteTicketsUrl = constructSiteUrl(siteSchedule);
                }

                sites.add(new Site(siteName, siteId, siteTicketsUrl));
                siteScheduleMap.put(siteId, new SiteSchedule(siteSchedule));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Error parsing JSON");
        }

        return new Schedule(sites, siteScheduleMap);
    }

    private boolean badEncoding(String string) {
        return !string.contains("יס פלאנט");
    }

    private String matchSiteNameById(int id) {
        switch(id){
            case 1010001:
                return "יס פלאנט - אילון";
            case 1010002:
                return "יס פלאנט - חיפה";
            case 1010003:
                return "יס פלאנט - ראשלצ";
            case 1010004:
                return "יס פלאנט - ירושלים";
            case 1010005:
                return "יס פלאנט - באר שבע";
            default:
                Log.e(LOG_TAG, "Error matching site name to id.");
                return null;
        }
    }

    private String constructSiteUrl(Map<String, List<MovieScreening>> siteSchedule) {
        // Get first movie schedule
        List<MovieScreening> someSchedule = siteSchedule.values().iterator().next();
        MovieScreening firstScreening = someSchedule.get(0);
        String screeningId = firstScreening.getId();
        String siteTicketsId = extractSiteTicketsId(screeningId);
        return createUrlWithId(siteTicketsId);
    }

    private String createUrlWithId(String siteTicketsId) {
        final String urlTemplate = "http://tickets.yesplanet.co.il/ypa?key=1025&ec=$PrsntCode$";
        return urlTemplate.replace("key=1025", "key=" + siteTicketsId);
    }

    private String extractSiteTicketsId(String screeningId) {
        // Extract 4 first digits of the screening id.
        // Example id: 102510090316-302891   Returns: 1025
        return screeningId.substring(0,4);
    }

    private int extractHallNumber(String id) {
        String firstPart = id.split("-")[0];
        if(firstPart.length() == 12) {
            return Integer.valueOf(firstPart.substring(4, 6));
        } else if(firstPart.length() == 11) {
            return Character.getNumericValue(firstPart.charAt(4));
        } else {
            Log.e(LOG_TAG, "Hall number extraction failed - length incorrect");
        }
        return 0;
    }

    private String extractDate(String string) {
        String[] arr = string.split(" ");
        for(String substring : arr){
            // If string contains numbers
            if(substring.matches(".*\\d+.*")){
                return substring;
            }
        }
        // Should not reach here
        return null;
    }
}
