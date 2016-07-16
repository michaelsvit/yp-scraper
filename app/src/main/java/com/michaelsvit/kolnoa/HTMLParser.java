package com.michaelsvit.kolnoa;

import java.util.List;

/**
 * Created by Michael on 7/14/2016.
 */
public interface HTMLParser {
    List<Movie> parse(String html);
}
