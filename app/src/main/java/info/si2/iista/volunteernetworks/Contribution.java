package info.si2.iista.volunteernetworks;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

import info.si2.iista.volunteernetworks.apiclient.ItemModel;
import info.si2.iista.volunteernetworks.apiclient.OnApiClientResult;
import info.si2.iista.volunteernetworks.apiclient.Result;
import info.si2.iista.volunteernetworks.apiclient.Virde;
import info.si2.iista.volunteernetworks.util.Model;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 8/9/15
 * Project: Virde
 */
public class Contribution extends AppCompatActivity implements OnApiClientResult, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private RelativeLayout layout;
    private GoogleApiClient mGoogleApiClient;

    // Item Location
    private ImageView imgLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contribution);

        // Views
        layout = (RelativeLayout)findViewById(R.id.layout);

        if (getIntent().getExtras() != null) {
            int id = getIntent().getIntExtra("idCampaign", -1);
            if (id != -1) {
                Virde.getInstance(this).getModelCampaign(id);
            } else {
                Toast.makeText(this, getString(R.string.noID), Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }

    @Override
    public void onApiClientRequestResult(Pair<Result, ArrayList> result) {
        switch (result.first.getResultFrom()) {
            case Virde.FROM_MODEL_CAMPAIGN:
                if (result.first.isError()) {
                    Toast.makeText(getApplicationContext(), result.first.getMensaje(), Toast.LENGTH_SHORT).show();
                } else {

                    // Google Api Client - Location
                    buildGoogleApiClient();
                    mGoogleApiClient.connect();

                    // Model
                    ArrayList<ItemModel> items = new ArrayList<>();

                    for (Object object : result.second) {
                        items.add((ItemModel) object);
                    }

                    for (int i=0; i<items.size(); i++) {

                        // Construcción de la vista
                        View view;
                        boolean lastItem = (i == items.size()-1);
                        if (i != 0) {
                            view = Model.getItem(Contribution.this, items.get(i), items.get(i-1).getId(), lastItem);
                        } else {
                            view = Model.getItem(Contribution.this, items.get(i), -1, lastItem);
                        }

                        if (view != null) {

                            String tag = ((LinearLayout)view).getChildAt(0).getTag().toString();

                            // Guardar ImageView de location para asignar después
                            if (tag.equals(ItemModel.ITEM_GEOPOS))
                                imgLocation = (ImageView)((LinearLayout)view).getChildAt(0);

                            // Añadir view al Layout
                            layout.addView(view);

                        }
                    }

                }
                break;
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null) {

            double lat = mLastLocation.getLatitude();
            double lng = mLastLocation.getLongitude();

            String url = "http://maps.google.com/maps/api/staticmap?center=" + lat + "," + lng
                    + "&zoom=15&size=300x200&scale=2&sensor=false&markers=color:red%7C"+ lat + ","+ lng;

//            Picasso.with(getApplicationContext())
//                    .load(url)
//                    .into(imgLocation);

        }

    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

}
