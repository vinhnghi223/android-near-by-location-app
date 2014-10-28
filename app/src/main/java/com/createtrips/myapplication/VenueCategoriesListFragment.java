package com.createtrips.myapplication;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by nghi on 06/10/14.
 */
public class VenueCategoriesListFragment extends ListFragment implements AdapterView.OnItemClickListener {
    // String[] venuesTitles;
    private ListView venuesCategoryList;
    private FragmentCommunicator fragmentCommunicator;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_venues_categories_list, container,false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fragmentCommunicator= (FragmentCommunicator) getActivity();
        venuesCategoryList = getListView();
        venuesCategoryList.setOnItemClickListener(this);
        venuesCategoryList.setAdapter(new VenueCatgoriesListAdapter(getActivity(), MainActivity.preloadedCategories));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        VenueCategory thisCategory = (VenueCategory) parent.getItemAtPosition(position);
        Log.d("Loading","on item click, fragmentCommunicator.setCategory(data);");
        fragmentCommunicator.setCategory(thisCategory.getId());
    }

}
