package com.michaelsvit.kolnoa;

import java.util.List;

/**
 * Created by Michael on 7/14/2016.
 */
public interface CinemaDataParser {
    List<Movie> parseMoviesHTML(String html);
    Schedule parseScheduleJSON(String json);
}
