package info.si2.iista.volunteernetworks.wear;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Developer: Carlos Campos
 * Date: 12/1/16
 * Project: CINDA
 */
public class WearService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    GoogleApiClient googleClient;

    // Server
    private String HOST = "http://virde.dev.si2soluciones.es/";

    // API
    private String PATH_REALTIME = "cindaAPI/realtime/watchface/?dev=true";
    private String PATH_SERVER_INFO = "cindaAPI/server/info/";

    // URL SERVICES
    private String URL_REALTIME = HOST+PATH_REALTIME;
    private String URL_SERVER_INFO = HOST+PATH_SERVER_INFO;

    // Data will send to wear
    static final String WEARABLE_DATA_PATH = "/wearable_data";
    private DataMap dataMap;

    private Context context;

    public WearService (Context context) {
        this.context = context;
    }

    public void updateData() {
        googleClient = new GoogleApiClient.Builder(context)
                .addApiIfAvailable(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        googleClient.connect();

        new GetServerName().execute();
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Build a new GoogleApiClient for the wearable API
        googleClient.connect();
        new GetServerName().execute();
    }

    @Override
    public void onConnectionSuspended(int i) {    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {    }

    private class SendToDataLayerThread extends Thread {
        String path;
        String message;

        // Constructor to send a message to the data layer
        SendToDataLayerThread(String p, String msg) {
            path = p;
            message = msg;
        }

        public void run() {

            PutDataMapRequest putDMR = PutDataMapRequest.create(path);
            putDMR.getDataMap().putAll(dataMap);
            PutDataRequest request = putDMR.asPutDataRequest();
            DataApi.DataItemResult result = Wearable.DataApi.putDataItem(googleClient, request).await();

            Log.e("result code", String.valueOf(result.getStatus().getStatusCode()));
//            Log.e("result message", result.getStatus().getStatusMessage());

            if (result.getStatus().isSuccess()) {
                Log.v("myTag", "DataMap: " + dataMap.toString() + " sent successfully to data layer ");
            } else {
                // Log an error
                Log.e("SendToDataLayerThread", "ERROR: failed to send DataMap to data layer");
            }

            if (null != googleClient && googleClient.isConnected()) {
                googleClient.disconnect();
            }
        }
    }

    private class GetServerName extends AsyncTask<Void, Void, String> {

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
                    .url(URL_SERVER_INFO)
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
                dataMap.putString("serverName", obj.getString("name"));

                new Realtime().execute();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class Realtime extends AsyncTask<Void, Void, String> {

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
                    .url(URL_REALTIME)
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
        }
    }
}

