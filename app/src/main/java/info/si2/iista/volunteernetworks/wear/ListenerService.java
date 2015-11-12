package info.si2.iista.volunteernetworks.wear;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import info.si2.iista.volunteernetworks.apiclient.ItemContribution;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 11/11/15
 * Project: Virde
 */
public class ListenerService extends WearableListenerService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // HOST
    private static final String HOST = "http://virde.dev.si2soluciones.es/";

    // URLs
    private static final String URL_NEAR_ACTIVITY = "API/realtime/nearby-activity/";
    private static final String URL_NEAR_CONTRIBUTIONS = "API/realtime/contributions/";

    // WEAR ACTIONS
    private static final String ACTION_NEAR_ACTIVITY = "activity";
    private static final String ACTION_NEAR_CONTRIBUTIONS = "contributions";

    private GoogleApiClient mGoogleApiClient;

    // Params to get near campaigns and contributions
    private LatLng latLng;
    private static final int RADIUS = 1000;
    private static int count = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        mGoogleApiClient  = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();

        mGoogleApiClient.connect();

        latLng = new LatLng(37.157602, -3.585306);

    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        switch (messageEvent.getPath()) {
            case ACTION_NEAR_ACTIVITY:

                break;

            case ACTION_NEAR_CONTRIBUTIONS:
                new AsyncGetContributions().execute(latLng.latitude, latLng.longitude, (double) RADIUS);
                break;
        }

    }

    /** GOOGLE API CLIENT **/
    @Override
    public void onConnected(Bundle bundle) {
        // TODO revisar posición
//        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
//                mGoogleApiClient);
//
//        if (mLastLocation != null) {
//
//            double lat = mLastLocation.getLatitude();
//            double lng = mLastLocation.getLongitude();
//
//            if (latLng == null)
//                latLng = new LatLng(lat, lng);
//
//        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /** CLIENT **/
    class AsyncGetContributions extends AsyncTask<Double, Void, ArrayList<ItemContribution>> {

        @Override
        protected ArrayList<ItemContribution> doInBackground(Double... params) {
            return getNearContributions(params[0], params[1], params[2]);
        }

        @Override
        protected void onPostExecute(ArrayList<ItemContribution> itemContributions) {

            String imageMap = "http://maps.googleapis.com/maps/api/staticmap?key=" + "AIzaSyAWL5TfqsVQJSD8iKEgEXn3zyGu0lipBmQ" +
                    "&center=" + String.valueOf(latLng.latitude + ","
                    + String.valueOf(latLng.longitude) +
                    "&zoom=14&size=240x240" + "&scale=1&maptype=roadmap" +
                    "&markers=icon:http://app.unidogs.es/images/wear/marker_propio.png|" +
                    String.valueOf(latLng.latitude) + "," + String.valueOf(latLng.longitude));

            // Añadir markers
            for (ItemContribution item : itemContributions) {

                String[] pos = item.getGeopos().split(",");
                imageMap += "&markers=" + pos[0] + "," + pos[1];

            }

            // TODO
            imageMap = "http://maps.googleapis.com/maps/api/staticmap?key=AIzaSyAWL5TfqsVQJSD8iKEgEXn3zyGu0lipBmQ&center=37.157602,-3.585306&zoom=14&size=240x240&scale=1&maptype=roadmap&markers=icon:http://app.unidogs.es/images/wear/marker_propio.png|37.157602,-3.585306";

            // Carga de imagen mediante Picasso y envío a Wear
            Picasso.with(ListenerService.this).load(imageMap).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                    PutDataMapRequest dataMap = PutDataMapRequest.create("/Contributions");
                    dataMap.getDataMap().putAsset("assetContributions", createAssetFromBitmap(bitmap));
                    dataMap.getDataMap().putInt("count", count);
                    count++;
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }

            });



        }

    }

    public ArrayList<ItemContribution> getNearContributions (Double lat, Double lng, Double radio) {

        ArrayList<ItemContribution> result = new ArrayList<>();

        OkHttpClient client = getOkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("geopos", String.valueOf(lat) + "," + String.valueOf(lng))
                .add("radius", String.valueOf(radio))
                .build();

        Request request = new Request.Builder()
                .url(HOST + URL_NEAR_CONTRIBUTIONS)
                .post(formBody)
                .build();

        try {

            Response response = client.newCall(request).execute();
            String respStr  = response.body().string();

            GsonBuilder gsonBuilder = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss");
            Gson gson = gsonBuilder.create();

            if (!respStr.equals("0")) {
                ItemContribution[] items = gson.fromJson(respStr, ItemContribution[].class);
                Collections.addAll(result, items);
            }

            return result;

        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

    }

    private static OkHttpClient getOkHttpClient () {

        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(15, TimeUnit.SECONDS);
        client.setReadTimeout(15, TimeUnit.SECONDS);

        return client;

    }

    private static Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }

}