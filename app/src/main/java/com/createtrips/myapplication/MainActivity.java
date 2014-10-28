package com.createtrips.myapplication;

import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity implements FragmentCommunicator{

    //Toggle list / map view related stuff
    private Menu menu;
    private static final int VENUE_CAT_INT = 0, VENUE_INT =1, MAP_INT =2;
    private static final String VENUE_CAT_STRING ="VENUE CATEGORIES",VENUE_STRING ="VENUES",MAP_STRING ="MAP";
    private int previousScreen=-1;
    private int currentScreen= VENUE_CAT_INT; //first starting the app: currentScreen is venues category list, there is no previous screen so it's set to -1
    ViewGroup mainFragmentContainer;//Uncomment for mainActivityGroup transition custom duration

    //is MainActivity loaded?
    public static boolean isLoaded;

    //Fragment related stuff
    private FragmentManager manager;
    private VenuesListFragment venuesListFragment;
    private VenuesMapViewFragment venuesMapViewFragment;
    private VenueCategoriesListFragment venueCategoriesListFragment;
    private LoadingFragment loadingFragment;
    private ViewGroup loadingFragmentView;
    private final Map<Fragment, Integer> fragmentIntegerHashMap = new HashMap<Fragment, Integer>();
    static ArrayList<VenueCategory> preloadedCategories;

// --Commented out by Inspection START (27/10/14 16:20):
//    //Debugging
//    String LOGCAT="MainActivity";
// --Commented out by Inspection STOP (27/10/14 16:20)
    @SuppressWarnings("ConstantConditions")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Get the data from DataWrapper that has been pass through intent
        DataWrapper dw = (DataWrapper) getIntent().getSerializableExtra("VenueBundle");
        preloadedCategories = dw.getVenuesCategories();


        if (preloadedCategories.size() == 0) {
            Toast toast = Toast.makeText(this, "There is currently 0 category to show. Please check back later.", Toast.LENGTH_SHORT);
            toast.show();
        }

        //Fading out animation for loadingFragmentView
        loadingFragmentView = (ViewGroup) findViewById(R.id.loading_fragment_container);
        LayoutTransition lt = new LayoutTransition();
        lt.disableTransitionType(LayoutTransition.APPEARING);
        loadingFragmentView.setLayoutTransition(lt);

        //Animation for mainActivityGroup transition
        // use this to adjust layout transition between fragment transition
        // if use this way, remember to remove android:animateLayoutChanges="true" in activity_main.xml
        mainFragmentContainer=(ViewGroup) findViewById(R.id.main_fragment_container);
        LayoutTransition mainFragmentContainerTransition = new LayoutTransition();
        mainFragmentContainerTransition.setDuration(Constants.TRANSITION_DURATION);
        mainFragmentContainer.setLayoutTransition(mainFragmentContainerTransition);

        venuesListFragment = new VenuesListFragment();
        venuesMapViewFragment = new VenuesMapViewFragment();
        venueCategoriesListFragment = new VenueCategoriesListFragment();
        loadingFragment = new LoadingFragment();

        fragmentIntegerHashMap.put(venuesListFragment, VENUE_INT);
        fragmentIntegerHashMap.put(venuesMapViewFragment, MAP_INT);
        fragmentIntegerHashMap.put(venueCategoriesListFragment, VENUE_CAT_INT);

        manager=getFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        transaction.add(R.id.main_fragment_container, venuesMapViewFragment,"venuesMap");
        transaction.hide(venuesMapViewFragment);
        transaction.add(R.id.main_fragment_container, venuesListFragment, "venuesCategoriesList");
        transaction.hide(venuesListFragment);
        transaction.add(R.id.main_fragment_container, venueCategoriesListFragment, "venuesCategoryListFragment");
        transaction.commit();
        getActionBar().setTitle(VENUE_CAT_STRING);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_toggle_activity_main_fragment:
                switchFragment();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onPause(){super.onPause();}

    @Override
    public void onResume(){
        super.onResume();
    }

    @SuppressWarnings("ConstantConditions")
    void updateScreen(Fragment hideFragment, Fragment showFragment){
        FragmentTransaction transaction=manager.beginTransaction();
        transaction.show(showFragment);
        transaction.hide(hideFragment);
        previousScreen= fragmentIntegerHashMap.get(hideFragment);
        currentScreen= fragmentIntegerHashMap.get(showFragment);

        if(currentScreen== MAP_INT){
            getActionBar().setTitle(MAP_STRING);
            menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_action_view_as_list));
        }else{
            menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_action_map));
            if(currentScreen== VENUE_CAT_INT){
                getActionBar().setTitle(VENUE_CAT_STRING);
            }else{
                getActionBar().setTitle(VENUE_STRING);
            }
        }

        transaction.commit();
    }

    @SuppressWarnings("UnnecessaryReturnStatement")
    private void switchFragment() {

        if(previousScreen== VENUE_CAT_INT && currentScreen== MAP_INT){
            updateScreen(venuesMapViewFragment, venueCategoriesListFragment);
            return;
        }

        if(previousScreen== VENUE_INT && currentScreen== MAP_INT){
            updateScreen(venuesMapViewFragment,venuesListFragment);
            return;
        }

        if(currentScreen!= MAP_INT){
            if(currentScreen== VENUE_CAT_INT){
                updateScreen(venueCategoriesListFragment,venuesMapViewFragment);
            }else{
                updateScreen(venuesListFragment,venuesMapViewFragment);
            }
            return;
        }
    }


    @Override
    public void goToTargetLocation(int i) {
        updateScreen(venuesListFragment, venuesMapViewFragment);
        venuesMapViewFragment.moveCameraToTargetLocation(i);
    }

    @Override
    public void setCategory(String category) {
        if(!loadingFragment.isAdded()) { //when loading fragment appears, do not setCategory
            venuesListFragment.setCategory(category);
        }
    }

    @Override
    public void populateDataOnMap(ArrayList<Venue> venueArrayList) { //when one item on venue cat list is clicked
        venuesMapViewFragment.populateVenuesArrayList(venueArrayList);
        venuesMapViewFragment.setAllMarkersOnMap();
    }

    @Override
    public void showLoadingFragment() {
        if(!loadingFragment.isAdded()) { //when loading fragment appears, do not add loading fragment again
            manager.beginTransaction().add(R.id.loading_fragment_container, loadingFragment, "loadingFragment").commit();
        }
    }

    @Override
    public void hideLoadingFragment() {
        manager.beginTransaction().remove(loadingFragment).commit();
    }

    @Override
    public void showVenueListByCategory(){
        updateScreen(venueCategoriesListFragment,venuesListFragment);
    }

    @Override
    public void onBackPressed(){  //click on back button of Android
        if(currentScreen== MAP_INT){
            if(previousScreen== VENUE_INT){
                updateScreen(venuesMapViewFragment,venuesListFragment);
                return;
            }

            if(previousScreen== VENUE_CAT_INT){
                updateScreen(venuesMapViewFragment, venueCategoriesListFragment);
                return;
            }
        }

        if(currentScreen== VENUE_INT){
            updateScreen(venuesListFragment, venueCategoriesListFragment);
            venuesListFragment.hideHeaderViewInstantly();
            venuesMapViewFragment.clearMap();
            return;
        }
        if (currentScreen == VENUE_CAT_INT){
            finish();
        }
    }



}
