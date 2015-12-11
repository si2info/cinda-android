package info.si2.iista.volunteernetworks;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Date;

import info.si2.iista.volunteernetworks.apiclient.ItemGpx;
import info.si2.iista.volunteernetworks.util.Util;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 2/12/15
 * Project: Shiari
 */
public class Tracking extends AppCompatActivity {

    // Map
    private GoogleMap googleMap;
    private boolean firstZoom;

    // Locations
    private ArrayList<LatLng> locationsRecorded;

    // Tipe menu
    public static final int MENU_PLAY = 1;
    public static final int MENU_PAUSE_STOP = 2;
    public static final int MENU_CONTINUE_STOP = 3;
    public static int menuType = MENU_PLAY; // 1 = Play /-/ 2 = Pause | Stop /-/ 3 = Continue | Stop

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracking);

        // Init map
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        googleMap = fm.getMap();
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setMyLocationEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        // Zoom and center map
        googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {

                if (!firstZoom) {
                    float zoom = googleMap.getCameraPosition().zoom;
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
                    googleMap.animateCamera(cameraUpdate);
                    firstZoom = true;
                }

            }
        });

    }

    /** Action menu items **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (menuType == 1)
            getMenuInflater().inflate(R.menu.menu_traking_play, menu);
        else if (menuType == 2)
            getMenuInflater().inflate(R.menu.menu_tracking_pause_stop, menu);
        else if (menuType == 3)
            getMenuInflater().inflate(R.menu.menu_tracking_continue_stop, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_tracking_init:
                menuType = MENU_PAUSE_STOP;
                invalidateOptionsMenu();
                startToTracking();
                return true;

            case R.id.action_tracking_pause:
                menuType = MENU_CONTINUE_STOP;
                invalidateOptionsMenu();
                pauseTracking();
                return true;

            case R.id.action_tracking_stop:
                menuType = MENU_PLAY;
                invalidateOptionsMenu();
                stopTracking();
                return true;

            case R.id.action_tracking_continue:
                menuType = MENU_PAUSE_STOP;
                invalidateOptionsMenu();
                continueTracking();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Start to tracking a route
     */
    public void startToTracking () {

        // Bind to LocalService
        Intent intent = new Intent(this, TrackingService.class);
        intent.setAction(TrackingService.ACTION_INIT);
        startService(intent);

    }

    /**
     * Pause user tracking
     */
    public void pauseTracking () {

        Intent intent = new Intent(this, TrackingService.class);
        intent.setAction(TrackingService.ACTION_PAUSE);
        startService(intent);

    }

    /**
     * Continue user tracking
     */
    public void continueTracking () {

        Intent intent = new Intent(this, TrackingService.class);
        intent.setAction(TrackingService.ACTION_CONTINUE);
        startService(intent);

    }

    /**
     * Stop user tracking and send/save contribution
     */
    public void stopTracking () {

        Intent intent = new Intent(this, TrackingService.class);
        intent.setAction(TrackingService.ACTION_STOP);
        startService(intent);

    }

    /**
     * Draw a route in map with locations tracked
     */
    private void drawRoute () {

        if (locationsRecorded != null) {
            PolylineOptions options = new PolylineOptions().width(10).color(Color.BLUE).geodesic(true);
            for (LatLng latLng : locationsRecorded) {
                options.add(latLng);
            }

            googleMap.addPolyline(options);
        }

    }

    /** Class **/

    @Override
    public void onResume() {
        super.onResume();

        // Reload actions toolbar
        invalidateOptionsMenu();

        TrackingService trackingService = TrackingService.getInstance();
        if (trackingService != null) {
            locationsRecorded = trackingService.getLocationsRecorded();
            drawRoute();
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {

        checkStopTracking(intent);

        super.onNewIntent(intent);
    }

    /**
     * Check if user stop tracking to send contribution
     * @param intent Intent
     */
    public void checkStopTracking (Intent intent) {

        // Intent from stop button notification
        if (intent.getExtras() != null) {
            boolean sendTracking = intent.getExtras().getBoolean("actionSend", false);
            if (sendTracking) {

                DialogInterface.OnClickListener nowListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String fileDir = Util.saveGpxFile(Tracking.this, locationsRecorded);
                        locationsRecorded.clear();
                        googleMap.clear();



                    }
                };

                DialogInterface.OnClickListener laterListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String fileDir = Util.saveGpxFile(Tracking.this, locationsRecorded);
                        locationsRecorded.clear();
                        googleMap.clear();
                        // TODO save to db
                        finish();
                    }
                };

                showMessageOKCancel(this, getString(R.string.dialog_send_tracking), nowListener, laterListener);

            }
        }

    }

    public ItemGpx makeItemGpx (String dir) {

        Date now = new Date();

        ItemGpx gpx = new ItemGpx();
        gpx.setId("XXX" + Util.parseDateToString("yyyyMMddHHmmss", now));
        gpx.setDir(dir);
        gpx.setDate(now);
        gpx.setIdServer(0);
        gpx.setIdCampaign(0);
        gpx.setSync(false);

        return gpx;

    }

    /** Dialog **/

    /**
     * AlertView constructor
     * @param c Context
     * @param message Message will be show
     * @param nowListener listener when user click "ok" button
     * @param laterListener listener when user click "ok" button
     */
    private void showMessageOKCancel(Context c, String message, DialogInterface.OnClickListener nowListener,
                                     DialogInterface.OnClickListener laterListener) {
        new AlertDialog.Builder(c)
                .setMessage(message)
                .setPositiveButton(c.getString(R.string.now), nowListener)
                .setNegativeButton(c.getString(R.string.later), laterListener)
                .create()
                .show();
    }

}