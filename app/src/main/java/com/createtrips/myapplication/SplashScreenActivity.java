package com.createtrips.myapplication;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/*
 * Created by duykhiemhang on 20/10/14.
 */
public class SplashScreenActivity extends Activity {
    // --Commented out by Inspection (27/10/14 16:22):int SPLASH_SCREEN=-2;
    private static final ArrayList<String> PRE_DEFINED_CATE = new ArrayList<String>(Arrays.asList(
            Constants.ART_ID,
            Constants.BUILDING_ID,
            Constants.EDU_ID,
            Constants.FOOD_ID,
            Constants.EVE_ID,
            Constants.SHOP_ID,
            Constants.TRAVEL_ID,
            Constants.NIGHT_ID,
            Constants.HOTEL_ID,
            Constants.OUTDOOR_ID));
    private RequestParams params;
    private final ArrayList<VenueCategory> venueCategoryArrayList = new ArrayList<VenueCategory>();

    private final String LOGCAT="SplashScreen";
    // --Commented out by Inspection (27/10/14 16:22):Handler mhandler = new Handler();

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //overridePendingTransition(R.anim.fadein,R.anim.fadeout); //fadein SplashScreen when start and fadeout when finish
        setContentView(R.layout.activity_splash);
        venueCategoryArrayList.add(new VenueCategory("", "Popular Venues", ""));
        //http request and conversion to POJO process

       //Request Parameters for 4square API

        params = new RequestParams();
        params.put("client_id", Constants.FQ_API_CLIENT_ID);
        params.put("client_secret", Constants.FQ_API_CLIENT_SECRET);
        params.put("v", Constants.FQ_API_CLIENT_VERSION);
        params.put("m", Constants.FQ_API_CLIENT_MODE);

        //New Intent for MainActivity
        final Intent intent = new Intent(this, MainActivity.class);
        new ConnectionCheck(this,false).isConnected();//Check Connection before send HTTPRequest

        AsyncRequest.get("venues/categories", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {
                //noinspection EmptyCatchBlock
                try {
                    Log.w(LOGCAT, "Http Request Success");
                    //First loop variables
                    String id, name, iconSuffix, iconPrefix;
                    JSONObject iconObject;
                    JSONArray subCategories;
                    JSONObject responseObject = response.getJSONObject("response");
                    JSONArray categories = responseObject.getJSONArray("categories");
                    Log.d(LOGCAT, "categories.length(): " + categories.length());

                    //Second loop variables
                    JSONObject subCategoriesObject, subIconObject;
                    String subId, subName, subIconSuffix, subIconPrefix;
                    for (int i = 0; i < categories.length(); i++) {
                        JSONObject categoriesObject = categories.getJSONObject(i);
                        id = categoriesObject.getString("id");

                        if (PRE_DEFINED_CATE.contains(id)) {
                            name = categoriesObject.getString("name");
                            iconObject = categoriesObject.getJSONObject("icon");
                            iconSuffix = iconObject.getString("suffix");
                            iconPrefix = iconObject.getString("prefix");
                            VenueCategory placeholder = new VenueCategory(id, name, iconPrefix + "64" + iconSuffix);
                            venueCategoryArrayList.add(placeholder);
                            Log.w("ICON", iconPrefix + "64" + iconSuffix);

                        }

                        subCategories = categoriesObject.getJSONArray("categories");
                        Log.d("subCategories.length()", "subCategories.length(): " + subCategories.length());
                        for (int j = 0; j < subCategories.length(); j++) {
                            subCategoriesObject = subCategories.getJSONObject(j);
                            subId = subCategoriesObject.getString("id");
                            if (PRE_DEFINED_CATE.contains(subId)) {
                                subName = subCategoriesObject.getString("name");
                                subIconObject = subCategoriesObject.getJSONObject("icon");
                                subIconSuffix = subIconObject.getString("suffix");
                                subIconPrefix = subIconObject.getString("prefix");
                                venueCategoryArrayList.add(new VenueCategory(subId, subName, subIconPrefix + "64" + subIconSuffix));
                                Log.w("ICON", subIconPrefix + "64" + subIconSuffix);
                            }
                        }
                    }
                    intent.putExtra("VenueBundle", new DataWrapper(venueCategoryArrayList));
                    startActivity(intent);
                    MainActivity.isLoaded = true;
                    finish();
                } catch (JSONException e) {
                }//end try-catch
            }
        });
    }
}
