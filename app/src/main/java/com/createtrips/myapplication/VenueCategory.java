package com.createtrips.myapplication;

import android.graphics.Bitmap;

import java.io.Serializable;

/*
 * Created by duykhiemhang on 10/10/14.
 */
public class VenueCategory implements Serializable {
    private String id;
    private String name;
    Bitmap icon;
    public String getIconURL() {
        return iconURL;
    }

    private final String iconURL;

    public VenueCategory(String id, String name, String iconURL) {
        this.id = id;
        this.name = name;
        this.iconURL = iconURL;
        prettySearch();
    }

    public String getId() {
        return id;
    }

// --Commented out by Inspection START (27/10/14 16:25):
//    public void setId(String id) {
//        this.id = id;
//    }
// --Commented out by Inspection STOP (27/10/14 16:25)

    public String getName() {
        return name;
    }

    // Define the category with the pretty name
    void prettySearch(){
        if (id.equals(Constants.ART_ID)) {
            name = "Art Venues";
        } else if (id.equals(Constants.EDU_ID)){
            name = "Education";
        } else if (id.equals(Constants.EVE_ID)){
            name = "Event";
        } else if (id.equals(Constants.FOOD_ID)){
            name = "Food & Restaurants";
        } else if (id.equals(Constants.OUTDOOR_ID)){
            name = "Outdoor & Attractions";
        } else if (id.equals(Constants.BUILDING_ID)){
            name = "Professional Venues";
        } else if (id.equals(Constants.SHOP_ID)){
            name = "Shops & Services";
        } else if (id.equals(Constants.TRAVEL_ID)){
            name = "Travel & transport";
        } else if (id.equals(Constants.HOTEL_ID)){
            name = "Hotels";
        } else if (id.equals(Constants.NIGHT_ID)){
            name = "Nightlife Spots";
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
