package com.michaelsvit.kolnoa;

import java.util.List;
import java.util.Map;

/**
 * Created by Michael on 7/14/2016.
 */
public interface CinemaDataParser {
    List<Movie> parseMoviesHTML(String html);
    Map<String, List<MovieScreening>> parseScheduleJSON(String json);
}
