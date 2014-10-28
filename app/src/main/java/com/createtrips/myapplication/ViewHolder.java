package com.createtrips.myapplication;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by nghi on 15/10/14.
 */
class ViewHolder{
    private static final int VENUES_CAT_LIST= 0 /*VENUES_LIST=1*/;
    private static final String SEMI_BOLD= "ProximaNovaCond-Semibold.otf", REGULAR="ProximaNovaCond-Regular.otf";
    ImageView icon;
    TextView categoryName;
    TextView locationName;
    TextView subCategory;
    TextView distance;

    ViewHolder(Context context, View v, int listType){
        if(listType==VENUES_CAT_LIST){
            icon= (ImageView) v.findViewById(R.id.category_icon);
            categoryName= (TextView) v.findViewById(R.id.category_name);
            categoryName.setTypeface(Typeface.createFromAsset(context.getAssets(), SEMI_BOLD));
        }else{
            locationName= (TextView) v.findViewById(R.id.venue_location_name);
            locationName.setTypeface(Typeface.createFromAsset(context.getAssets(), SEMI_BOLD));
            subCategory = (TextView) v.findViewById(R.id.sub_category_name);
            subCategory.setTypeface(Typeface.createFromAsset(context.getAssets(), REGULAR));
            distance= (TextView) v.findViewById(R.id.distance);
            distance.setTypeface(Typeface.createFromAsset(context.getAssets(), REGULAR));
        }
    }
}