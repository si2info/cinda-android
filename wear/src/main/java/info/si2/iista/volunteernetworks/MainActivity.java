package info.si2.iista.volunteernetworks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.GridViewPager;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends WearableActivity implements DataApi.DataListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    // Communicate to mobile
    private static final String ACTION_NEAR_ACTIVITY = "activity";
    private static final String ACTION_NEAR_CONTRIBUTIONS = "contributions";
    private static final long CONNECTION_TIME_OUT_MS = 1000;
    private String nodeId;

    // Data
    private static final String COUNT_KEY = "info.si2.";
    private GoogleApiClient mGoogleApiClient;

    // Views
    private AdapterViewPager adapter;
    private GridViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pager);
        setAmbientEnabled();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // Views
        mPager = (GridViewPager) findViewById(R.id.pager);

        // ViewPager
        adapter = new AdapterViewPager(getFragmentManager(), new ArrayList<String>());
        mPager.setAdapter(adapter);

    }

    public void nextPage (View v) {

        mPager.setCurrentItem(1, 1);

    }

    private void updateDisplay() {
//        if (isAmbient()) {
//
//        } else {
//
//        }
    }

    /** DATA FROM DEVICE **/
    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {

        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();


                if(item.getUri().toString().contains("/MapPetition")) { // Petición de tiles contributions

                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                    Asset assetAlerts = dataMapItem.getDataMap().getAsset("assetContributions");

                    // Paso de Asset a Bitmap
                    Bitmap bitmap = loadBitmapFromAsset(assetAlerts);
                    setImageMap(bitmap);

                }

            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }

    }

    /**
     * Transforma un objeto de tipo Asset a uno de tipo Bitmap
     * @param asset Asset a transformar
     * @return Bitmap obtenido del Asset
     */
    public Bitmap loadBitmapFromAsset(Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }
        ConnectionResult result = mGoogleApiClient.blockingConnect(10000, TimeUnit.MILLISECONDS);
        if (!result.isSuccess()) {
            return null;
        }
        // convert asset into a file descriptor and block until it's ready
        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(mGoogleApiClient, asset).await().getInputStream();

        if (assetInputStream == null) {
            Log.w("CREATE_ASSET", "Requested an unknown Asset.");
            return null;
        }
        // decode the stream into a bitmap
        return BitmapFactory.decodeStream(assetInputStream);
    }

    /** DATA TO DEVICE **/
    private void sendAction(String action) {

        if (nodeId != null && mGoogleApiClient!=null && mGoogleApiClient.isConnected()) {
            Wearable.MessageApi.sendMessage(
                    mGoogleApiClient, nodeId, action, null).setResultCallback (

                    new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {

                            if (!sendMessageResult.getStatus().isSuccess()) {
                                Log.e("TAG", "Failed to send message with status code: "
                                        + sendMessageResult.getStatus().getStatusCode());
                            }
                        }
                    }
            );
        }else{
            Log.e("WEAR", "Device not connected");
        }

    }

    private void retrieveDeviceNode() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mGoogleApiClient.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                NodeApi.GetConnectedNodesResult result =
                        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
                List<Node> nodes = result.getNodes();
                if (nodes.size() > 0) {
                    nodeId = nodes.get(0).getId();
                }
                sendAction(ACTION_NEAR_CONTRIBUTIONS);
            }
        }).start();
    }

    /** GOOGLE API CLIENT **/
    @Override
    public void onConnected(Bundle bundle) {
        Wearable.DataApi.addListener(mGoogleApiClient, this);
        retrieveDeviceNode();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /** AMBIENT **/
    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }

    public void setImageMap (Bitmap bitmap) {

        adapter.setImageMap(bitmap);
        adapter.notifyDataSetChanged();

    }

}
