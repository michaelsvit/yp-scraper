package com.michaelsvit.kolnoa;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MovieScreeningRecyclerViewAdapter extends RecyclerView.Adapter<MovieScreeningRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<MovieScreening> schedule;
    private String siteTicketsUrl;

    public MovieScreeningRecyclerViewAdapter(Context context, List<MovieScreening> schedule, String siteTicketsUrl) {
        this.context = context;
        this.schedule = schedule;
        this.siteTicketsUrl = siteTicketsUrl;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_moviescreening, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        MovieScreening screening = schedule.get(position);
        holder.dateView.setText(formatScreeningInfo(screening));
    }

    private String formatScreeningInfo(MovieScreening screening) {
        String time = screening.getTime();
        int hallNumber = screening.getHallNumber();
        String type = screening.getType();
        String hallString = "אולם";
        String typeString;
        if (!type.equals("null")) {
            typeString = " " + type;
        } else {
            typeString = "";
        }
        return time + " " + hallString + " " + String.valueOf(hallNumber) + typeString;
    }

    @Override
    public int getItemCount() {
        return schedule.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView dateView;

        public ViewHolder(View view) {
            super(view);
            dateView = (TextView) view.findViewById(R.id.movie_screening_date);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    MovieScreening screening = schedule.get(position);
                    Intent intent = new Intent(context, TicketPurchaseActivity.class);
                    intent.putExtra(MovieScreening.ARG_NAME, screening.getId());
                    intent.putExtra(Site.TICKETS_URL_ARG_NAME, siteTicketsUrl);
                    context.startActivity(intent);
                }
            });
        }
    }
}
