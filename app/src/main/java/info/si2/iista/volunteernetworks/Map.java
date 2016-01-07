package info.si2.iista.volunteernetworks;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 18/9/15
 * Project: Virde
 */
public class Map extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private MarkerOptions markerOptions;
    private LatLng position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        // Action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.map_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent().getExtras() != null) {
            double lat = getIntent().getDoubleExtra("lat", 0.0);
            double lng = getIntent().getDoubleExtra("lng", 0.0);
            position = new LatLng(lat, lng);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {

        mMap = map;

        if (position != null) {
            if (position.latitude == 0.0 && position.longitude == 0.0) {
                position = new LatLng(37.0486113, -3.3123066);
            }
        } else {
            position = new LatLng(37.0486113, -3.3123066);
        }

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(position)           // Sets the center of the map to user location
                .zoom(14)                   // Sets the zoom
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder

        markerOptions = new MarkerOptions()
                .position(position)
                .title("Lugar de la contribución")
                .draggable(true);

        mMap.setOnMapClickListener(this);
        mMap.addMarker(markerOptions);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    @Override
    public void onMapClick(LatLng latLng) {
        mMap.clear();
        markerOptions.position(latLng);
        mMap.addMarker(markerOptions);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_map_ok:
                setResultOk();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        setResultOk();
    }

    public void setResultOk() {

        Intent returnIntent = new Intent();
        returnIntent.putExtra("lat", markerOptions.getPosition().latitude);
        returnIntent.putExtra("lng", markerOptions.getPosition().longitude);
        setResult(RESULT_OK, returnIntent);
        finish();

    }

}
