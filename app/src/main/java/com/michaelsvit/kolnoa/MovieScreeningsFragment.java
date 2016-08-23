package com.michaelsvit.kolnoa;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class MovieScreeningsFragment extends Fragment {
    private static final String LOG_TAG = MovieScreeningsFragment.class.getSimpleName();

    private Context context;
    private Map<String, List<MovieScreening>> schedule;
    private List<MovieScreening> screeningList;
    private MovieScreeningRecyclerViewAdapter adapter;

    // String representing date today
    private String dateToday;
    // Date today split
    private int dayToday;
    // Starts from 0
    private int monthToday;
    private int yearToday;

    // Currently picked date
    private int dayPicked;
    // Starts from 0
    private int monthPicked;
    private int yearPicked;

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
            this.context = context;
            MovieScreeningsActivity activity = (MovieScreeningsActivity) context;
            this.schedule = activity.getSchedule();

            // Date today
            getDateAndSetTitle(activity);
        } else {
            Log.d(LOG_TAG, "Attached to activity different from MovieScreeningsActivity");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_movie_screenings, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_pick_date){
            DatePickerDialog datePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int pickerYear, int pickerMonth, int pickerDay) {
                    String dateString = getDateString(pickerDay, pickerMonth, pickerYear);

                    // Update list contents
                    screeningList.clear();
                    screeningList.addAll(schedule.get(dateString));
                    adapter.notifyDataSetChanged();

                    yearPicked = pickerYear;
                    monthPicked = pickerMonth;
                    dayPicked = pickerDay;

                    setToolbarTitle(pickerDay, pickerMonth, pickerYear);
                }
            }, yearPicked, monthPicked, dayPicked);

            datePicker.show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setToolbarTitle(int day, int month, int year) {
        String dateString = getDateString(day, month, year);
        String title = "";
        if(dateString.equals(dateToday)){
            title = getString(R.string.today) + " " + dateString;
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            switch(calendar.get(Calendar.DAY_OF_WEEK)){
                case 1:
                    title = getString(R.string.sunday) + " " + dateString;
                    break;
                case 2:
                    title = getString(R.string.monday) + " " + dateString;
                    break;
                case 3:
                    title = getString(R.string.tuesday) + " " + dateString;
                    break;
                case 4:
                    title = getString(R.string.wednesday) + " " + dateString;
                    break;
                case 5:
                    title = getString(R.string.thursday) + " " + dateString;
                    break;
                case 6:
                    title = getString(R.string.friday) + " " + dateString;
                    break;
                case 7:
                    title = getString(R.string.saturday) + " " + dateString;
                    break;
            }
        }
        TextView titleText = (TextView) ((MovieScreeningsActivity) context).findViewById(R.id.screenings_toolbar_title);
        titleText.setText(title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_moviescreening_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.addItemDecoration(new DividerItemDecoration(context));
            List<MovieScreening> screenings = schedule.get(dateToday);
            if (screenings != null) {
                screeningList = new ArrayList<>(screenings);
            } else {
                screeningList = new ArrayList<>();
            }
            adapter = new MovieScreeningRecyclerViewAdapter(screeningList);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

    private void getDateAndSetTitle(Activity activity) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        dayToday = calendar.get(Calendar.DAY_OF_MONTH);
        monthToday = calendar.get(Calendar.MONTH);
        yearToday = calendar.get(Calendar.YEAR);
        dateToday = getDateString(dayToday, monthToday, yearToday);
        setToolbarTitle(dayToday, monthToday, yearToday);

        // Set currently picked date to today
        dayPicked = dayToday;
        monthPicked = monthToday;
        yearPicked = yearToday;
    }

    private String getDateString(int day, int month, int year) {
        final String separator = "/";
        return String.valueOf(day) + separator + (month+1 < 10 ? "0":"")
                + String.valueOf(month+1) + separator + String.valueOf(year);
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
