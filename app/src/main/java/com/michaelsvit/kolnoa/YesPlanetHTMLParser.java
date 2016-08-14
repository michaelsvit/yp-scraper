package com.michaelsvit.kolnoa;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Michael on 7/14/2016.
 * Used to parse HTML from YesPlanet website
 */
public class YesPlanetHTMLParser implements HTMLParser{
    private static final String LOG_TAG = YesPlanetHTMLParser.class.getSimpleName();
    @Override
    public List<Movie> parse(String html) {
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
        final String TITLE_CLASS_NAME = "featureTitle";
        final String RELEASE_DATE_CLASS_NAME = "releaseDate";
        final String SYNOPSIS_CLASS_NAME = "synopsis";
        final String POSTER_CLASS_NAME = "catPoster";

        // Title
        Element featureTitle = htmlMovie.getElementsByClass(TITLE_CLASS_NAME).first();
        String titleElem;
        if (featureTitle != null) {
            titleElem = featureTitle.ownText();
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

        return new Movie(titleElem, releaseDate, synopsis, status, posterURL);
    }
}
