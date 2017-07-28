package com.michaelsvit.kolnoa;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class ScreeningSeatMapFragment extends Fragment {
    public static final String FRAGMENT_ARG_NAME = "seat_map_fragment";
    private static final String LOG_TAG = ScreeningSeatMapFragment.class.getSimpleName();

    private String screeningId;
    private String siteTicketsUrl;

    private Context context;
    private WebView webView;
    private RelativeLayout containerLayout;
    private TableLayout tableLayout;
    private TextView emptyMapText;
    private ProgressBar progressBar;

    private SeatMap seatMap;

    public ScreeningSeatMapFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        screeningId = arguments.getString(MovieScreening.ARG_NAME);
        siteTicketsUrl = arguments.getString(Site.TICKETS_URL_ARG_NAME);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (seatMap != null) {
            outState.putParcelable(SeatMap.ARG_NAME, seatMap);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_seat_map, container, false);
        context = rootView.getContext();
        containerLayout = (RelativeLayout) rootView.findViewById(R.id.seat_map_container);
        tableLayout = (TableLayout) rootView.findViewById(R.id.seat_map_table);
        emptyMapText = (TextView) rootView.findViewById(R.id.seat_map_empty_text);
        progressBar = (ProgressBar) rootView.findViewById(R.id.seat_map_progress_bar);

        if (savedInstanceState == null) {
            initWebView(rootView);
            fetchData();
        } else {
            seatMap = savedInstanceState.getParcelable(SeatMap.ARG_NAME);
            displaySeatMap();
        }

        return rootView;
    }

    private void fetchData() {
        webView.loadUrl(getDataUrl(siteTicketsUrl, screeningId));
    }

    private String getDataUrl(String siteTicketsUrl, String screeningId) {
        // Site tickets URL example: http://tickets.yesplanet.co.il/ypa?key=1025&ec=$PrsntCode$
        return siteTicketsUrl.replace("$PrsntCode$", screeningId);
    }

    private void displaySeatMap() {
        int rows = seatMap.getRows();
        int rowCounter = 1;
        for(int i = 0; i < rows; i++){
            TableRow tableRow = new TableRow(context);
            int cols = seatMap.getCols();

            boolean rowEmpty = seatMap.isRowEmpty(i);
            if(!rowEmpty){
                tableRow.addView(createRowNumText(rowCounter));
            }

            for(int j = 0; j < cols; j++){
                Seat seat = seatMap.getSeat(i,j);
                TextView view = new TextView(context);
                TableRow.LayoutParams params = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
                params.setMargins(3,3,3,3);
                view.setLayoutParams(params);
                if (seat != null) {
                    Seat.Status seatStatus = seat.getStatus();
                    if(seatStatus == Seat.Status.UNOCCUPIED && !seat.isHandicap()) {
                        view.setBackgroundColor(Color.LTGRAY);
                    } else if( seatStatus == Seat.Status.UNOCCUPIED && seat.isHandicap()){
                        view.setBackgroundColor(Color.parseColor("#2196f3"));
                    } else {
                        view.setBackgroundColor(Color.GRAY);
                    }
                }
                tableRow.addView(view);
            }

            if(!rowEmpty){
                tableRow.addView(createRowNumText(rowCounter));
                rowCounter++;
            }

            tableLayout.addView(tableRow);
        }

        // Hide progress bar and display table
        progressBar.setVisibility(View.GONE);
        containerLayout.setVisibility(View.VISIBLE);
    }

    private TextView createRowNumText(int rowNumber) {
        TextView textView = new TextView(context);
        TableRow.LayoutParams params = new TableRow.LayoutParams(0,TableRow.LayoutParams.WRAP_CONTENT, 2f);
        params.setMargins(3,3,3,3);
        textView.setLayoutParams(params);
        textView.setGravity(Gravity.CENTER);
        textView.setSingleLine();
        textView.setText(String.valueOf(rowNumber));
        textView.setTextSize(12f);
        return textView;
    }

    private void initWebView(final View rootView) {
        class JSInterface{
            @JavascriptInterface
            public void processHTML(String html){
                seatMap = new SeatMap(html);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displaySeatMap();
                    }
                });
            }

            @JavascriptInterface
            public void errorOccurred(){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        emptyMapText.setVisibility(View.VISIBLE);
                    }
                });
            }
        }

        webView = (WebView) rootView.findViewById(R.id.seat_map_web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JSInterface(), "Android");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                final String formId = "aspnetForm";
                final String errorFormName = "SystemErrorPage";
                final String ticketSelectionFormName = "SelectTicketsPage";
                final String seatSelectionFormName = "SelectSeatPage";
                final String selectionBoxId = "ctl00_CPH1_SelectTicketControl1_rptTicketSelectionGrid_ctl00_TicketsSelection_ctl02_ddQunatity_0";
                int ticketCount = 1;
                final String buttonId = "ctl00_CPH1_lbNext1";

                final String simulateTicketCountSelection = "document.getElementById('" + selectionBoxId + "').value = " + String.valueOf(ticketCount) + ";";
                final String simulateButtonPress = "document.getElementById('" + buttonId + "').click();";
                final String extractHtml = "window.Android.processHTML(document.getElementsByTagName('html')[0].innerHTML);";

                webView.loadUrl("javascript:var formName = document.getElementById('" + formId + "').getAttribute('action');" +
                        "if (formName.includes('" + errorFormName + "')){" +
                        "window.Android.errorOccurred();" +
                        "} else if (formName.includes('" + ticketSelectionFormName + "')){" +
                        simulateTicketCountSelection +
                        simulateButtonPress +
                        "} else if (formName.includes('" + seatSelectionFormName + "')) {" +
                        extractHtml +
                        "}");
            }
        });
    }
}
