package com.createtrips.myapplication;

import java.io.Serializable;
import java.util.ArrayList;

/*
 * Created by duykhiemhang on 21/10/14. A Wrapper for Arraylist to go through the intent without errors.
 * This is use when SplashScreenActivity call MainActivity
 */


// Implement serializable to pass to intent
public class DataWrapper implements Serializable {

    private ArrayList<VenueCategory> data;

    public DataWrapper(ArrayList<VenueCategory> data) {
        this.data = data;
    }

    public ArrayList<VenueCategory> getVenuesCategories() {
        return this.data;
    }

}

