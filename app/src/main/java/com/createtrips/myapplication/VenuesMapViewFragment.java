package com.createtrips.myapplication;

import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by nghi on 02/10/14.
 */
public class VenuesMapViewFragment extends Fragment implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,GoogleMap.OnMarkerClickListener, com.google.android.gms.location.LocationListener {

    //Google Map
    private MapView mMapView;
    private GoogleMap googleMap; // Might be null if Google Play services APK is not available.
    private Bundle mBundle;
    // --Commented out by Inspection (27/10/14 16:30):private LocationSource.OnLocationChangedListener mListener;

    //Location data
    private ArrayList<Venue> venueArrayList;

    //Markers
    private Marker previousMarker;
    private final HashMap<Integer,Marker> markerHashMap = new HashMap();

    //Camera - these are default values and may be changed the next time this fragment is attached
    private boolean isFirstTimeOpenMap =true;;
    private LocationClient mLocationClient;
    private static final int DEFAULT_ZOOM_LEVEL = 15;

    //Debugging
    private static final String LOGCAT = "MAP STATE TESTING";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOGCAT,"onCreate-------------------------------");
        mBundle = savedInstanceState;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOGCAT,"onCreateView-------------------------------");
        View inflatedView = inflater.inflate(R.layout.fragment_venues_map_view, container, false);
        try {
            MapsInitializer.initialize(getActivity()); //Initializes the Google Maps Android API (like BitmapDescriptorFactory and CameraUpdateFactory) so that its classes are ready for use.
            mMapView = (MapView) inflatedView.findViewById(R.id.map);//get instance of the map view
            mMapView.onCreate(mBundle);
            if(initMap(inflatedView)){
                setUpMap();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inflatedView;
    }

    private boolean initMap(View inflatedView){
        Log.d(LOGCAT,"googleMap state 1: "+ googleMap);
        if(googleMap ==null){
            googleMap = ((MapView) inflatedView.findViewById(R.id.map)).getMap(); //map object
            if(googleMap==null){
                Toast.makeText(getActivity(), "Google Play services APK is not available", Toast.LENGTH_SHORT).show();
            }
        }
        Log.d(LOGCAT,"googleMap state 2: "+ googleMap);
        return (googleMap !=null);
    }

    private void setUpMap() {
        Log.d(LOGCAT,"setUpMap");
        googleMap.setMyLocationEnabled(true); //add my location button to map
        googleMap.getUiSettings().setZoomControlsEnabled(false); //hide zoom controls button

        if(mLocationClient==null){
            mLocationClient=new LocationClient(getActivity(),this, this);
        }
        mLocationClient.connect(); //Connected to location service

        /*googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

            @Override
            public void onMyLocationChange(Location location) {
                if (location == null)
                    return;

                mPositionMarker = googleMap.addMarker(new MarkerOptions()
                        .flat(true)
                        //.icon(BitmapDescriptorFactory
                        //        .fromResource(R.drawable.logop1))
                        .anchor(0.5f, 1f)
                        .position(new LatLng(location.getLatitude(), location.getLongitude())));

            }
        });*/

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(googleMap!=null){
                    googleMap.clear();
                    Log.d(LOGCAT,"googleMap.clear()");
                    previousMarker=null;
                    //googleMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));
                    setAllMarkersOnMap();
                }
            }
        });

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() { //Setup info window
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                Log.d(LOGCAT, "info marker before layout inflater");
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService
                        (Context.LAYOUT_INFLATER_SERVICE);
                View v = inflater.inflate(R.layout.info_window, null);
                if(marker!=null) {
                    ((TextView) v.findViewById(R.id.venue_location_name)).setText(marker.getTitle());
                    if(marker.getSnippet()!=""){
                        ((TextView) v.findViewById(R.id.sub_category_name)).setText(marker.getSnippet());
                    }
                }
                return v;
            }
        });

        //onInfoWindowClick
        /*googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {//Called when the marker's info window is clicked.
            @Override
            public void onInfoWindowClick(Marker marker) {
                String locationName = marker.getTitle();
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.facebook.com/search/more?q=" + locationName)));
            }
        });*/
        googleMap.setOnMarkerClickListener(this);

    }

    //Populate arraylist data
    public void populateVenuesArrayList(ArrayList<Venue> venueArrayList){
        Log.d("communicator", "data landed in populateVenuesArrayList 2");
        this.venueArrayList=venueArrayList;
    }


    //Camera update
    public void moveCameraToTargetLocation(int i){
        if(previousMarker!=null){
            previousMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon));
            //previousMarker.setIcon(BitmapDescriptorFactory.defaultMarker());
        }
        Venue targetVenue=venueArrayList.get(i);
        LatLng newTargetlatLng = new LatLng(targetVenue.getLat(), targetVenue.getLon());
        CameraUpdate update = (isFirstTimeOpenMap) ? CameraUpdateFactory.newLatLngZoom(newTargetlatLng,DEFAULT_ZOOM_LEVEL):CameraUpdateFactory.newLatLng(newTargetlatLng);
        isFirstTimeOpenMap =false;
        googleMap.animateCamera(update);
        previousMarker=markerHashMap.get(i);
        previousMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ct_icon_small));
        previousMarker.setAnchor(0.5f, 1.0f);
        //previousMarker.setIcon(BitmapDescriptorFactory.defaultMarker());
        previousMarker.showInfoWindow();
    }

    //Marker related stuff
    private Marker setMarker(double lat, double lng, String locationName, String subCategoryName){// create marker
        LatLng ll = new LatLng(lat, lng);
        MarkerOptions markerOptions = new MarkerOptions().position(ll)
                        .title(locationName)
                        .snippet(subCategoryName)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon));
                        //.icon(BitmapDescriptorFactory.defaultMarker());
         return googleMap.addMarker(markerOptions);
    }

    public void setAllMarkersOnMap(){
        //googleMap.clear();
        //previousMarker=null;
        if(venueArrayList!=null) {
            Venue venue;
            for (int i = 0; i < venueArrayList.size(); i++) {
                venue= venueArrayList.get(i);
                markerHashMap.put( i, setMarker(venue.getLat(),
                        venue.getLon(),
                        venue.getNameLocation(),
                        venue.getSubCategory()));
            }
        }

    };

    public void clearMap(){
        Log.d(LOGCAT,"Clearing Map ...");
        googleMap.clear();
        previousMarker=null;
    }

    @Override
    public boolean onMarkerClick(Marker marker) { //Called when a marker has been clicked or tapped.
        if(previousMarker!=null){
            previousMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon));
            //previousMarker.setIcon(BitmapDescriptorFactory.defaultMarker());
        }
        if(marker!=null){
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ct_icon_small));
            marker.setAnchor(0.5f, 1.0f);
            //marker.setIcon(BitmapDescriptorFactory.defaultMarker());
            previousMarker=marker;
        }
        return false;
    }

    //MapView cycle
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d(LOGCAT,"Map On Low Memory -------------------------------");
        mMapView.onLowMemory(); //be aware
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOGCAT,"Map On Resume -------------------------------");
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOGCAT,"Map On Pause -------------------------------");
        mMapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(LOGCAT,"Map On Stop -------------------------------");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOGCAT,"Map On Destroy -------------------------------");
        mMapView.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(LOGCAT,"Map On Detach -------------------------------");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(LOGCAT,"Map on Save Instance State");
        mMapView.onSaveInstanceState(outState);
    }

    //Connected to location service
    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest request=LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        request.setInterval(3600000);
        request.setFastestInterval(60000); //redo the request every 1 minute = 60000ms at the fastest, https://developer.android.com/reference/com/google/android/gms/location/LocationRequest.html
        mLocationClient.requestLocationUpdates(request, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(LOGCAT,"onLocationChanged");
        //Go to current location -
        //Drawbacks: in some case this will make the loading slow if users is moving since the current position is periodically updating
        //Also the lat and lon is not as correct as just using static update
        //Location currentLocation = mLocationClient.getLastLocation();
        Location currentLocation =location;
        LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        Log.d(LOGCAT,""+ currentLatLng);
        //googleMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));
        CameraUpdate update= CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM_LEVEL);
        if(isFirstTimeOpenMap){
            googleMap.moveCamera(update);
            Log.d(LOGCAT,""+isFirstTimeOpenMap);
        }
        isFirstTimeOpenMap =false;
        Log.d(LOGCAT,""+isFirstTimeOpenMap);
    }

    @Override
    public void onDisconnected() {
        //Toast.makeText(getActivity(),"Disconnected to location service", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
//        Toast.makeText(getActivity(),"Unable to connected to location service", Toast.LENGTH_SHORT).show();
    }


}
