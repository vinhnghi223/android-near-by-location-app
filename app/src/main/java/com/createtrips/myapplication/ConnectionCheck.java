package com.createtrips.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

/*
 * Created by duykhiemhang on 22/10/14. This class is created to check the connection and enable Wifi connection.
 */
public class ConnectionCheck {

    private final Context context;
    private final Activity activity;
    private final boolean cancelOnBackPress;

    public ConnectionCheck(Activity activity, boolean cancelOnBackPress){
        this.context = activity;
        this.activity = activity;
        this.cancelOnBackPress=cancelOnBackPress;
    }


    //The actual code to check connection
    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiInfo != null && wifiInfo.isConnected()) || (mobileInfo != null && mobileInfo.isConnected())) {
            return true;
        } else {
            //If there's no connection, show Dialog
            showDialog(cancelOnBackPress);
            return false;
        }
    }

    void showDialog(boolean cancelOnBackPress){
        // Show dialog to quit or connect to Wifi
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        if(!cancelOnBackPress){
           builder.setMessage("Sorry! Nearby app can not be run without internet, please connect to the internet.");
        }else{
            builder.setMessage("Please connect to the Internet.");
        }
        builder.setTitle("No Internet Connection Available.")
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        activity.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        activity.finish();
                    }
                })
                .setCancelable(cancelOnBackPress);

        AlertDialog alert = builder.create();
        alert.show();

    }
}
