package com.createtrips.myapplication;

import java.util.ArrayList;

/*
 * Created by nghi on 05/10/14.
 */
public interface FragmentCommunicator {
    public void goToTargetLocation(int index);
    public void setCategory(String category);
    public void populateDataOnMap(ArrayList<Venue> venueArrayList);
    public void showLoadingFragment();
    public void hideLoadingFragment();
    public void showVenueListByCategory();
}
