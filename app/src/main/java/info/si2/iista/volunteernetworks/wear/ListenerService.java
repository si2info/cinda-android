package info.si2.iista.volunteernetworks.wear;

import android.app.Service;
import android.bluetooth.BluetoothClass;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import info.si2.iista.volunteernetworks.MainActivity;

/**
 * Created by si2soluciones on 22/12/15.
 */
public class ListenerService extends WearableListenerService {

    // Wear
    static final String realtime = "http://virde.dev.si2soluciones.es/cindaAPI/realtime/watchface/?dev=true";
    static final String server = "http://virde.dev.si2soluciones.es/cindaAPI/server/info/";
    static final String WEARABLE_DATA_PATH = "/wearable_data";

    static DataMap dataMap;
    static GoogleApiClient googleClient;
    static Calendar date = new GregorianCalendar();

    @Override
    public void onCreate() {
        super.onCreate();
        googleClient = new GoogleApiClient.Builder(this)
                .addApiIfAvailable(Wearable.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {

                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {

                    }
                })
                .build();

        googleClient.connect();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        if (messageEvent.getPath().equals("/wear")) {
            final String message = new String(messageEvent.getData());
            Log.e("myTag", "Message path received on watch is: " + messageEvent.getPath());
            Log.e("myTag", "Message received on watch is: " + message);

            new GetServerName().execute();

        }
        else {
            super.onMessageReceived(messageEvent);
        }
    }

    // Wear AsyntTask
    private static class SendToDataLayerThread extends Thread {
        String path;
        String message;

        // Constructor to send a message to the data layer
        SendToDataLayerThread(String p, String msg) {
            path = p;
            message = msg;
        }

        public void run() {


            if (googleClient != null) {
                PutDataMapRequest putDMR = PutDataMapRequest.create(path);
                putDMR.getDataMap().putAll(dataMap);
                PutDataRequest request = putDMR.asPutDataRequest();
                DataApi.DataItemResult result = Wearable.DataApi.putDataItem(googleClient, request).await();
                if (result.getStatus().isSuccess()) {
                    Log.v("myTag", "DataMap: " + dataMap + " sent successfully to data layer ");
                } else {
                    // Log an error
                    Log.v("myTag", "ERROR: failed to send DataMap to data layer");

                }
            }
        }
    }

    private static class Realtime extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            OkHttpClient client = new OkHttpClient();
            Response resp = null;
            String result = null;

            Request request = new Request.Builder()
                    .url(realtime)
                    .build();

            try {
                resp = client.newCall(request).execute();
                result = resp.body().string();
            } catch (IOException e) {
                Log.e("Call realtime", e.toString());
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            dataMap.putString("json", s);
            new SendToDataLayerThread(WEARABLE_DATA_PATH, dataMap.toString()).start();
            // new SendToDataLayerThread("/message_path", s).start();
        }
    }

    public static class GetServerName extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            OkHttpClient client = new OkHttpClient();
            Response resp = null;
            String result = null;

            Request request = new Request.Builder()
                    .url(server)
                    .build();

            try {
                resp = client.newCall(request).execute();
                result = resp.body().string();
            } catch (IOException e) {
                Log.e("Call realtime", e.toString());
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject obj = new JSONObject(s);
                dataMap = new DataMap();
                dataMap.putString("serverHour", date.getTime().toString());
                dataMap.putString("serverName", obj.getString("name"));
                new Realtime().execute();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
