package com.createtrips.myapplication;

import android.app.ListFragment;
import android.content.IntentSender;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by nghi on 02/10/14.
 */
public class VenuesListFragment extends ListFragment implements AdapterView.OnItemClickListener,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener{


    private ListView venuesList;
    private View headerView;
    private ArrayList<Venue> venueListArray;
    // --Commented out by Inspection (27/10/14 16:29):private static final int VENUES_LIST= 1;
    private TextView headerViewTextView;

    //Thread stuff
    private final Handler hideHeaderViewHadler = new android.os.Handler();
    private final Handler loadingVenuesHandler = new android.os.Handler();
    private final HideHeaderViewRunnable hideHeaderViewRunnable=new HideHeaderViewRunnable();
    private long startTime;
    private long endTime;
    private long convertJSONToPOJOTime;
    private static final int FADE_OUT_LOADING_FRAGMENT_TIME = 255;

    //Network stuff
    private LocationClient mLocationClient;
    private FragmentCommunicator fragmentCommunicator;
    private RequestParams params;
    private double currentLatitude;
    private double currentLongitude;

    //JSON to POJO stuff
    private JSONObject responseObject;
    private JSONObject venueObject;
    private JSONObject subCateObject;
    private String subCate;
    private Double lat;
    private Double lng;
    private JSONArray venuesArray;
    private JSONArray subCateArray;

    //Debug
    private final String LOGCAT = "VenuesListFragment";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_venues_list, container,false);
        headerView=inflater.inflate(R.layout.venues_list_header_view, null);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        venueListArray = new ArrayList<Venue>();
        mLocationClient = new LocationClient(this.getActivity(), this, this);
        mLocationClient.connect();
        headerViewTextView = (TextView) headerView.findViewById(R.id.header_view_text);
        venuesList = getListView();
        getListView().setOnItemClickListener(this);
        fragmentCommunicator= (FragmentCommunicator) getActivity();
        getActivity().getActionBar().setTitle("VENUES");
    }


    private void executeRequest(String category){
        Log.d(LOGCAT,"executeRequest");

        //Get current Lag and Long
        currentLatitude = mLocationClient.getLastLocation().getLatitude();
        currentLongitude = mLocationClient.getLastLocation().getLongitude();

        //Request Parameters for 4Square API
        params = new RequestParams();
        params.put("client_id", Constants.FQ_API_CLIENT_ID);
        params.put("client_secret", Constants.FQ_API_CLIENT_SECRET);
        params.put("categoryId", category);
        params.put("intent", "checkin");
        params.put("radius", "1000");
        params.put("ll", currentLatitude + "," + currentLongitude);
        params.put("v", Constants.FQ_API_CLIENT_VERSION);
        params.put("limit", "50");
        params.put("m", Constants.FQ_API_CLIENT_MODE);

        if(new ConnectionCheck(getActivity(),true).isConnected()){
            fragmentCommunicator.showLoadingFragment();
        }

        // Send the Async Request
        AsyncRequest.get("venues/search", params, new JsonHttpResponseHandler() {

            public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {
                Thread loadingVenuesThread = new Thread(new Runnable() {
                    @Override
                    public void run() { //Background Thread
                        startTime = System.currentTimeMillis();
                        try {
                            // JSON Parsing on the go
                            Log.d(LOGCAT, "JSON Parsing on the go");
                            venueListArray.clear();
                            responseObject = response.getJSONObject("response");
                            venuesArray = responseObject.getJSONArray("venues");
                            for (int i = 0; i < venuesArray.length(); i++) {
                                venueObject = venuesArray.getJSONObject(i);
                                lat = venueObject.getJSONObject("location").getDouble("lat");
                                lng = venueObject.getJSONObject("location").getDouble("lng");
                                subCateArray = venueObject.getJSONArray("categories");
                                subCateObject = subCateArray.getJSONObject(0);
                                subCate = subCateObject.getString("name");
                                if (!subCate.equals("Home (private)")) {
                                    venueListArray.add(
                                            new Venue(lat, lng,
                                                    venueObject.getString("name"),
                                                    subCate,
                                                    String.format("%.2f km", distance(lat, lng))));
                                }
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                        endTime   = System.currentTimeMillis();
                        convertJSONToPOJOTime = endTime - startTime;
                        Log.d("Total Time: ","Total Time: "+ convertJSONToPOJOTime);
                        loadingVenuesHandler.post(new Runnable() { //UI Thread
                            @Override
                            public void run() {
                                fragmentCommunicator.hideLoadingFragment(); //hide loading fragment and updateScreen later (below)
                            }
                        });

                        loadingVenuesHandler.postDelayed(new Runnable() { //UI Thread
                            @Override
                            public void run() {
                                if (venueListArray.size() == 0) {
                                    Toast.makeText(getActivity(),
                                            "There is 0 venues near by your current location to show in this category.",
                                            Toast.LENGTH_SHORT).show();
                                }
                                headerViewTextView.setText("Showing " + venueListArray.size() + " most popular venues");
                                headerViewTextView.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "ProximaNovaCond-Regular.otf"));
                                venuesList.addHeaderView(headerView, null, false); //add this to make sure headerview is not selectable
                                venuesList.setAdapter(new VenuesListAdapter(getActivity(), venueListArray));
                                fragmentCommunicator.populateDataOnMap(venueListArray);
                                fragmentCommunicator.showVenueListByCategory();
                                hideHeaderView();
                            }
                        }, FADE_OUT_LOADING_FRAGMENT_TIME + convertJSONToPOJOTime); //this process happens after loading & converting json and hiding loading fragment
                    }
                });
                loadingVenuesThread.setPriority(Thread.MIN_PRIORITY);
                loadingVenuesThread.start();
            }

        });
    }

    public void hideHeaderViewInstantly(){
        venuesList.removeHeaderView(headerView);
        hideHeaderViewHadler.removeCallbacks(hideHeaderViewRunnable);
    }

    void hideHeaderView(){
        hideHeaderViewHadler.postDelayed(hideHeaderViewRunnable,3000);
    }

    class HideHeaderViewRunnable implements Runnable {
        @Override
        public void run() {
            venuesList.removeHeaderView(headerView);
        }
    }

    public void setCategory(String category){
        executeRequest(category);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(venuesList.getHeaderViewsCount()!=0){i--;} //if there is header then i=i-1
        fragmentCommunicator.goToTargetLocation(i);
    }

    @Override
    public void onConnected(Bundle bundle) {
        //Toast.makeText(this.getActivity(), "Connected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisconnected() {
        //Toast.makeText(this.getActivity(), "Disconnected. Please re-connect.",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Google Location API foolproof method
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(
                        this.getActivity(),
                        9000);
            } catch (IntentSender.SendIntentException e) {

            }
        } else {
            //Toast.makeText(this.getActivity(), "Error! Please check your application settings.",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStop() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
    }

    @Override
    public void onPause() {
        mLocationClient.disconnect();
        super.onPause();
        Log.w(LOGCAT, "On pause 1");
    }

    @Override
    public void onResume() {
        super.onResume();
        mLocationClient.connect();
        Log.w(LOGCAT, "On resume 1");
    }

    //Distance calculator
    private double distance(double lat1, double lon1) {
        double theta = lon1 - currentLongitude;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(currentLatitude)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(currentLatitude)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515 * 1.609344;
        return dist;
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0); 
    }

    private double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

}
