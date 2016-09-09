package com.michaelsvit.kolnoa;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Michael on 8/31/2016.
 */
public class SpinnerArrayAdapter extends ArrayAdapter<Site> {

    private List<Site> sites;
    private LayoutInflater inflater;
    private int resource;

    public SpinnerArrayAdapter(Context context, int resource, List<Site> sites) {
        super(context, resource, sites);
        this.sites = sites;
        this.resource = resource;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    private View getCustomView(int position, View convertView, ViewGroup parent) {
        convertView = super.getView(position, convertView, parent);
        ((TextView) convertView).setText(sites.get(position).getName());
        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }
}
