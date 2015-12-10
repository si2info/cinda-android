package info.si2.iista.volunteernetworks;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import info.si2.iista.volunteernetworks.util.Util;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 2/12/15
 * Project: Shiari
 */
public class Tracking extends AppCompatActivity {

    // Map
    private GoogleMap googleMap;

    // Locations
    private ArrayList<LatLng> locationsRecorded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracking);

        // Init map
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        googleMap = fm.getMap();
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setMyLocationEnabled(true);
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        // Zoom and center map
        googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                float zoom = googleMap.getCameraPosition().zoom;
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
                googleMap.animateCamera(cameraUpdate);
            }
        });

        // Intent from stop button notification
        if (getIntent().getExtras() != null) {
            boolean sendTracking = getIntent().getExtras().getBoolean("actionSend", false);
            if (sendTracking) {

                showMessageOKCancel(this, getString(R.string.dialog_send_tracking),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String fileDir = Util.saveGpxFile(Tracking.this, locationsRecorded);
                            }
                        });

            }
        }

    }

    /**
     * Button OnClick (Start Tracking)
     * @param view View button
     */
    public void startToTracking (View view) {

        // Bind to LocalService
        Intent intent = new Intent(this, TrackingService.class);
        intent.setAction(TrackingService.ACTION_INIT);
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

        TrackingService trackingService = TrackingService.getInstance();
        if (trackingService != null) {
            locationsRecorded = trackingService.getLocationsRecorded();
            drawRoute();
        }

    }

    /** Dialog **/

    /**
     * AlertView constructor
     * @param c Context
     * @param message Message will be show
     * @param okListener listener when user click "ok" button
     */
    private void showMessageOKCancel(Context c, String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(c)
                .setMessage(message)
                .setPositiveButton(c.getString(R.string.now), okListener)
                .setNegativeButton(c.getString(R.string.later), null)
                .create()
                .show();
    }

}