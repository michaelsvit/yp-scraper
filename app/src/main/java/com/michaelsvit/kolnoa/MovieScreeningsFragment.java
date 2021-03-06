package com.michaelsvit.kolnoa;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MovieScreeningsFragment extends Fragment {
    public static final String FRAGMENT_ARG_NAME = "movie_screenings_fragment";
    private static final String LOG_TAG = MovieScreeningsFragment.class.getSimpleName();

    private Context context;
    private Site site;
    private MovieScheduleInSite schedule;
    private List<MovieScreening> screeningList;
    private MovieScreeningRecyclerViewAdapter adapter;

    private RecyclerView recyclerView;
    // Shows when the recycler is empty
    private TextView textView;

    // String representing date today
    private static final String DATE_TODAY_ARG = "date_today";
    private String dateToday;

    // Currently picked date
    private static final String DAY_PICKED_ARG = "day_picked";
    private static final String MONTH_PICKED_ARG = "month_picked";
    private static final String YEAR_PICKED_ARG = "year_picked";
    private int dayPicked;
    // Starts from 0
    private int monthPicked;
    private int yearPicked;

    // Max and min date for picker
    private Date maxDate;
    private Date minDate;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieScreeningsFragment() {
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public void setSchedule(MovieScheduleInSite schedule) {
        this.schedule = schedule;
    }

    public static MovieScreeningsFragment newInstance(Site site, MovieScheduleInSite schedule) {
        MovieScreeningsFragment fragment = new MovieScreeningsFragment();
        fragment.setSite(site);
        fragment.setSchedule(schedule);
        return fragment;
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(DATE_TODAY_ARG, dateToday);
        outState.putInt(DAY_PICKED_ARG, dayPicked);
        outState.putInt(MONTH_PICKED_ARG, monthPicked);
        outState.putInt(YEAR_PICKED_ARG, yearPicked);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_pick_date) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int pickerYear, int pickerMonth, int pickerDay) {
                    String dateString = getDateString(pickerDay, pickerMonth, pickerYear);

                    // Update list contents
                    screeningList.clear();
                    screeningList.addAll(schedule.getScreeningsList(dateString));
                    showTextIfEmpty(screeningList);
                    adapter.notifyDataSetChanged();

                    yearPicked = pickerYear;
                    monthPicked = pickerMonth;
                    dayPicked = pickerDay;

                    setToolbarTitle(pickerDay, pickerMonth, pickerYear);
                }
            }, yearPicked, monthPicked, dayPicked);
            DatePicker datePicker = datePickerDialog.getDatePicker();
            getMaxAndMinDate();
            datePicker.setMinDate(minDate.getTime());
            datePicker.setMaxDate(maxDate.getTime());
            datePickerDialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void getMaxAndMinDate() {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        List<String> dates = new ArrayList<>(schedule.getDatesSet());
        sortListByDate(dates, dateFormat);
        if (maxDate == null) {
            try {
                maxDate = dateFormat.parse(dates.get(dates.size()-1));
            } catch (ParseException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, "Error parsing maxDate");
            }
        }
        if (minDate == null) {
            try {
                minDate = dateFormat.parse(dates.get(0));
            } catch (ParseException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, "Error parsing minDate");
            }
        }
    }

    private void sortListByDate(List<String> dates, final SimpleDateFormat dateFormat) {
        Collections.sort(dates, new Comparator<String>() {
            @Override
            public int compare(String dateString0, String dateString1) {
                Date date0 = null;
                Date date1 = null;
                try {
                    date0 = dateFormat.parse(dateString0);
                    date1 = dateFormat.parse(dateString1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (date0 != null && date1 != null) {
                    return date0.compareTo(date1);
                } else {
                    Log.e(LOG_TAG, "Error parsing dates when comparing");
                    return 0;
                }
            }
        });
    }

    private void setToolbarTitle(int day, int month, int year) {
        String dateString = getDateString(day, month, year);
        String title = "";
        if (dateString.equals(dateToday)) {
            title = getString(R.string.today) + " " + dateString;
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            switch (calendar.get(Calendar.DAY_OF_WEEK)) {
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

        context = getActivity();
        getDateAndSetTitle(savedInstanceState);

        recyclerView = (RecyclerView) view.findViewById(R.id.screenings_recycler);
        textView = (TextView) view.findViewById(R.id.movie_screenings_empty_text);

        // Set the adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context));
        List<MovieScreening> screenings = schedule.getScreeningsList(getDateString(dayPicked, monthPicked, yearPicked));
        showTextIfEmpty(screenings);
        adapter = new MovieScreeningRecyclerViewAdapter(context, screeningList, site.getTicketsUrl());
        recyclerView.setAdapter(adapter);
        return view;
    }

    private void showTextIfEmpty(List<MovieScreening> screenings) {
        if (screenings != null) {
            screeningList = new ArrayList<>(screenings);
            recyclerView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);
        } else {
            screeningList = new ArrayList<>();
            recyclerView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
        }
    }

    private void getDateAndSetTitle(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
            int dayToday = calendar.get(Calendar.DAY_OF_MONTH);
            // Starts from 0
            int monthToday = calendar.get(Calendar.MONTH);
            int yearToday = calendar.get(Calendar.YEAR);
            dateToday = getDateString(dayToday, monthToday, yearToday);

            // Set currently picked date to today
            dayPicked = dayToday;
            monthPicked = monthToday;
            yearPicked = yearToday;
        } else {
            dateToday = savedInstanceState.getString(DATE_TODAY_ARG);
            dayPicked = savedInstanceState.getInt(DAY_PICKED_ARG);
            monthPicked = savedInstanceState.getInt(MONTH_PICKED_ARG);
            yearPicked = savedInstanceState.getInt(YEAR_PICKED_ARG);
        }
        setToolbarTitle(dayPicked, monthPicked, yearPicked);
    }

    private String getDateString(int day, int month, int year) {
        final String separator = "/";
        return (day < 10 ? "0" : "")
                + String.valueOf(day) + separator
                + (month + 1 < 10 ? "0" : "")
                + String.valueOf(month + 1) + separator
                + String.valueOf(year);
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
