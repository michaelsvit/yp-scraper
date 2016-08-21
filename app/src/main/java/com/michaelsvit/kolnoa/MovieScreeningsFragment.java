package com.michaelsvit.kolnoa;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class MovieScreeningsFragment extends Fragment {
    private static final String LOG_TAG = MovieScreeningsFragment.class.getSimpleName();

    private Context context;
    private Map<String, List<MovieScreening>> schedule;
    private String dateToday;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieScreeningsFragment() {
    }

    public static MovieScreeningsFragment newInstance() {
        MovieScreeningsFragment fragment = new MovieScreeningsFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MovieScreeningsActivity) {
            MovieScreeningsActivity activity = (MovieScreeningsActivity) context;
            this.schedule = activity.getSchedule();
        } else {
            Log.d(LOG_TAG, "Attached to activity different from MovieScreeningsActivity");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_moviescreening_list, container, false);

        // Date today
        getDate();

        // Set the adapter
        if (view instanceof RecyclerView) {
            this.context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.addItemDecoration(new DividerItemDecoration(context));
            List<MovieScreening> screeningList = new ArrayList<>(schedule.get(dateToday));
            recyclerView.setAdapter(new MovieScreeningRecyclerViewAdapter(screeningList));
        }
        return view;
    }

    private void getDate() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        dateToday = getDateString(day, month, year);
    }

    private String getDateString(int day, int month, int year) {
        final String separator = "/";
        return String.valueOf(day) + separator + (month < 10 ? "0":"")
                + String.valueOf(month) + separator + String.valueOf(year);
    }

    // List divider
    private class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private final int[] ATTRS = new int[]{android.R.attr.listDivider};

        private Drawable mDivider;

        /**
         * Default divider will be used
         */
        public DividerItemDecoration(Context context) {
            final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
            mDivider = styledAttributes.getDrawable(0);
            styledAttributes.recycle();
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }
}
