package com.michaelsvit.kolnoa;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * A placeholder fragment containing a simple view.
 */
public class ScreeningSeatMapFragment extends Fragment {
    private static final String LOG_TAG = ScreeningSeatMapFragment.class.getSimpleName();

    private String screeningId;
    private String siteTicketsUrl;

    private WebView webView;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_seat_map, container, false);

        initWebView(rootView);
        fetchData();

        return rootView;
    }

    private void fetchData() {
        webView.loadUrl(getDataUrl(siteTicketsUrl, screeningId));
    }

    private String getDataUrl(String siteTicketsUrl, String screeningId) {
        // Site tickets URL example: http://tickets.yesplanet.co.il/ypa?key=1025&ec=$PrsntCode$
        return siteTicketsUrl.replace("$PrsntCode$", screeningId);
    }

    private void initWebView(View rootView) {
        class JSInterface{
            @JavascriptInterface
            public void processHTML(String html){
                seatMap = new SeatMap(html);
            }
        }

        webView = (WebView) rootView.findViewById(R.id.seat_map_web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JSInterface(), "Android");
        webView.setWebViewClient(new WebViewClient(){
            boolean firstPage = true;

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (firstPage) {
                    simulateTicketNumberSelection(webView);
                    simulateButtonPress(webView);
                    firstPage = false;
                } else {
                    webView.loadUrl("javascript:window.Android.processHTML("
                            + "document.getElementsByTagName('html')[0].innerHTML);");
                }
            }

            private void simulateTicketNumberSelection(WebView webView) {
                String selectionBoxId = "ctl00_CPH1_SelectTicketControl1_rptTicketSelectionGrid_ctl00_TicketsSelection_ctl02_ddQunatity_0";
                int ticketNumber = 1;
                webView.loadUrl("javascript:document.getElementById('"
                        + selectionBoxId + "').value = "
                        + String.valueOf(ticketNumber));
            }

            private void simulateButtonPress(WebView webView) {
                String buttonId = "ctl00_CPH1_lbNext1";
                webView.loadUrl("javascript:document.getElementById('" + buttonId + "').click()");
            }
        });
    }
}
