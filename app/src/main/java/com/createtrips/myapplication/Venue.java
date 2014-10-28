package com.createtrips.myapplication;

/*
 * Created by nghi on 05/10/14.
 */

//#1
public class Venue {
    private double lat;
    private double lon;

    public String getDistance() {
        return distance;
    }

// --Commented out by Inspection START (27/10/14 16:23):
//    public void setDistance(String distance) {
//        this.distance = distance;
//    }
// --Commented out by Inspection STOP (27/10/14 16:23)

    private String distance;
    private String nameLocation;
    private String subCategory;

    public double getLat() {
        return lat;
    }

// --Commented out by Inspection START (27/10/14 16:23):
//    public void setLat(double lat) {
//        this.lat = lat;
//    }
// --Commented out by Inspection STOP (27/10/14 16:23)

    public double getLon() {
        return lon;
    }

// --Commented out by Inspection START (27/10/14 16:23):
//    public void setLon(double lon) {
//        this.lon = lon;
//    }
// --Commented out by Inspection STOP (27/10/14 16:23)

    public String getNameLocation() {
        return nameLocation;
    }

// --Commented out by Inspection START (27/10/14 16:23):
//    public void setNameLocation(String nameLocation) {
//        this.nameLocation = nameLocation;
//    }
// --Commented out by Inspection STOP (27/10/14 16:23)

    public String getSubCategory() {
        return subCategory;
    }

// --Commented out by Inspection START (27/10/14 16:23):
//    public void setSubCategory(String subCategory) {
//        this.subCategory = subCategory;
//    }
// --Commented out by Inspection STOP (27/10/14 16:23)

    public Venue(double lat, double lon, String nameLocation,String subCategory, String distance){
        this.lat=lat;
        this.lon=lon;
        this.nameLocation=nameLocation;
        this.subCategory=subCategory;
        this.distance=distance;
    }

    @Override
    public String toString(){
        return nameLocation;
    }

}
