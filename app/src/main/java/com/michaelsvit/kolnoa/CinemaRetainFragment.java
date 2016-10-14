package com.michaelsvit.kolnoa;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * A fragment to retain Cinema object on screen rotation
 */
public class CinemaRetainFragment extends Fragment {
    public static final String FRAGMENT_ARG_NAME = "cinema_retain_fragment";

    private Cinema cinema;

    public CinemaRetainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public Cinema getCinema() {
        return cinema;
    }

    public void setCinema(Cinema cinema) {
        this.cinema = cinema;
    }
}
