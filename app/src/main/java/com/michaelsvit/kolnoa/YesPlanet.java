package com.michaelsvit.kolnoa;

/**
 * Created by Michael on 8/15/2016.
 */
public class YesPlanet extends Cinema {
    public YesPlanet() {
        super("Yes Planet",
                "http://www.yesplanet.co.il/",
                "http://www.yesplanet.co.il/loadFunction?layoutId=10&layerId=1&exportCode=movies_filter",
                "http://www.yesplanet.co.il/presentationsJSON",
                new YesPlanetDataParser());
    }

    @Override
    public String getPosterUrl(Movie movie) {
        return null;
    }
}
