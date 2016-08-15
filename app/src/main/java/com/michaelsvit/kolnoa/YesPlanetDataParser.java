package com.michaelsvit.kolnoa;

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
        final String IN_THEATRE = "cat_0";
        final String COMING_SOON = "cat_1";
        final String DATA_CLASS_NAME = "extended";
        final String ID_ATTR_NAME = "data-feature_code";
        final String TITLE_CLASS_NAME = "featureTitle";
        final String RELEASE_DATE_CLASS_NAME = "releaseDate";
        final String SYNOPSIS_CLASS_NAME = "synopsis";
        final String POSTER_CLASS_NAME = "catPoster";

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
            posterURL = posterURLElem.attr(attributeKey);
        }

        return new Movie(featureId, featureTitle, releaseDate, synopsis, status, posterURL);
    }

    @Override
    public Map<String, List<MovieScreening>> parseScheduleJSON(String json) {
        final String SITES_ARRAY = "sites";
        final String SITE_NAME_FIELD = "sn";
        final String SITE_TICKETS_URL_FIELD = "tu";
        final String SITE_FEATURES_ARRAY = "fe";
        final String SITE_ID = "si";
        final String FEATURE_ID_FIELD = "dc";
        final String FEATURE_LANGUAGE_FIELD = "ol";
        final String FEATURE_SCREENINGS_ARRAY = "pr";
        final String SCREENING_DATE_FIELD = "dt";
        final String SCREENING_TIME_FIELD = "tm";
        final String SCREENING_ID_FIELD = "pc";
        final String SCREENING_TYPE_FIELD = "vt";

        Map<String, List<MovieScreening>> schedule = new HashMap<>();


        return null;
    }
}
