package com.michaelsvit.kolnoa;

/**
 * Created by Michael on 8/15/2016.
 */
public class YesPlanet extends Cinema {
    public YesPlanet() {
        super("Yes Planet",
                "http://yesplanet.internet-bee.mobi/TREST_YP3/resources/info/merge/0/CATS,FEAT,TIX,PRSNT",
                new YesPlanetDataParser());
    }
}
