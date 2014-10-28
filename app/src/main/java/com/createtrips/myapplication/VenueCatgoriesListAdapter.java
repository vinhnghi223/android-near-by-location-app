package com.createtrips.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.util.List;

/**
 * Created by nghi on 15/10/14.
 */
@SuppressWarnings("DefaultFileTemplate")
class VenueCatgoriesListAdapter extends ArrayAdapter {
    private final Context context;
    private final List<VenueCategory> venueCategoryList;
    private static final int VENUES_CAT_LIST= 0;
    private final int[] iconArray = new int[]{R.drawable.popular_venues,
                                R.drawable.arts_entertainment,
                                R.drawable.education,
                                R.drawable.event,
                                R.drawable.food,
                                R.drawable.nightlife,
                                R.drawable.outdoor,
                                R.drawable.building,
                                R.drawable.shop,
                                R.drawable.travel,
                                R.drawable.hotel};
    public VenueCatgoriesListAdapter(Context context, List<VenueCategory> venueObjectsList) {
        super(context, R.layout.venues_categories_single_row,venueObjectsList);
        this.context=context;
        this.venueCategoryList =venueObjectsList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView==null){//inflate it to java obj, if convertView = null; optimize to 150%
            LayoutInflater inflater = (LayoutInflater) context.getSystemService((Context.LAYOUT_INFLATER_SERVICE));
            convertView = inflater.inflate(R.layout.venues_categories_single_row,parent,false);

            viewHolder=new ViewHolder(context,convertView,VENUES_CAT_LIST);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder) convertView.getTag();
        }
        final VenueCategory venueCategory = venueCategoryList.get(position);
        viewHolder.categoryName.setText(venueCategory.getName());
        //viewHolder.icon.setImageBitmap(venuesCategories.getIconImage());

        final ViewHolder cachedViewHolder = viewHolder;
        if(venueCategory.icon==null){

            //Uncomment this to to display icon immediately before it was loaded
            Bitmap icon = BitmapFactory.decodeResource(getContext().getResources(), iconArray[position]);
            cachedViewHolder.icon.setImageBitmap(getRoundedCornerBitmap(icon, 60));
            Log.d("icon url",""+ venueCategory.getIconURL());
            Log.w("ViewHolder1", "AsyncImageRequest");
            AsyncRequest.getImageByBitStream(venueCategory.getIconURL(), null, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Bitmap image = BitmapFactory.decodeByteArray(responseBody, 0, responseBody.length);
                    venueCategory.icon = getRoundedCornerBitmap(image, 60);
                    cachedViewHolder.icon.setImageBitmap(venueCategory.icon);
                    Log.w("ViewHolder1", "onSuccess");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                }
            });

        }else{
            viewHolder.icon.setImageBitmap(venueCategory.icon);
        }

        return convertView;
    }

    private static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff788691;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0,0,0,0); //Fill the entire canvas' bitmap (restricted to the current clip) with the specified ARGB color, using srcover porterduff mode.
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
}

