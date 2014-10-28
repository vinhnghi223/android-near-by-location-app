package com.createtrips.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by nghi on 15/10/14.
 */
class VenuesListAdapter extends ArrayAdapter {
    private final Context context;
    private final List<Venue> venueList;
    private static final int VENUES_LIST= 1;

    public VenuesListAdapter(Context context, List<Venue> venueList) {
        super(context, R.layout.venues_single_row,venueList);
        this.context=context;
        this.venueList=venueList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView==null){//inflate it to java obj, if convertView = null; optimize to 150%
            LayoutInflater inflater = (LayoutInflater) context.getSystemService((Context.LAYOUT_INFLATER_SERVICE));
            convertView = inflater.inflate(R.layout.venues_single_row,parent,false);

            viewHolder=new ViewHolder(context,convertView,VENUES_LIST);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder) convertView.getTag();
        }

        Venue venue = venueList.get(position);
        viewHolder.locationName.setText(venue.getNameLocation());
        viewHolder.subCategory.setText(venue.getSubCategory());
        viewHolder.distance.setText(venue.getDistance());
        return convertView;
    }
}