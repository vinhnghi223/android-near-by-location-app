package com.createtrips.myapplication;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.client.params.ClientPNames;

/**
 *Created by duykhiemhang on 10/10/14.
 * This is a model for LoopJ AsyncHTTPRequest Library, made for foursquare API
 */
public class AsyncRequest {

    private static final String BASE_URL = "http://api.foursquare.com/v2/";

    private static final AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {

        //Call to avoid circular redirects error, which have no other solution work.
        client.getHttpClient().getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

// --Commented out by Inspection START (27/10/14 16:10):
//    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
//
//        //Call to avoid circular redirects error, which have no other solution work.
//        client.getHttpClient().getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
//        client.post(getAbsoluteUrl(url), params, responseHandler);
//    }
// --Commented out by Inspection STOP (27/10/14 16:10)

    public static void getImageByBitStream(String url, RequestParams params, AsyncHttpResponseHandler responseHandler){

        client.getHttpClient().getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);

        client.get(url, params, responseHandler);
    }

    //Get URL
    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }


}
