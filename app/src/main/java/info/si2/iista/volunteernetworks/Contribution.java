package info.si2.iista.volunteernetworks;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import info.si2.iista.volunteernetworks.apiclient.ItemFormContribution;
import info.si2.iista.volunteernetworks.apiclient.ItemModel;
import info.si2.iista.volunteernetworks.apiclient.OnApiClientResult;
import info.si2.iista.volunteernetworks.apiclient.Result;
import info.si2.iista.volunteernetworks.apiclient.Virde;
import info.si2.iista.volunteernetworks.camera.ToolCam;
import info.si2.iista.volunteernetworks.database.DBVirde;
import info.si2.iista.volunteernetworks.database.OnDBApiResult;
import info.si2.iista.volunteernetworks.util.Model;
import info.si2.iista.volunteernetworks.util.Util;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 8/9/15
 * Project: Virde
 */
public class Contribution extends AppCompatActivity implements OnApiClientResult, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnDBApiResult {

    private RelativeLayout layout;
    private LinearLayout loading;
    private GoogleApiClient mGoogleApiClient;

    // Location
    private static final int REQUEST_MAP = 0x1;
    private ImageView imgLocation;
    private LatLng position;

    // Camera and gallery
    private static final int REQUEST_IMAGE = 0x2;
    private String mCurrentPhotoPath;
    private boolean fromCamera;

    // Flags
    private boolean initializated;

    // ProgresDialog
    private ProgressDialogFragment dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contribution);

        // Action bar
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Views
        layout = (RelativeLayout)findViewById(R.id.layout);
        loading = (LinearLayout)findViewById(R.id.loading);

        // Position
        double lat = Util.getDoublePreferenceModel(this, getString(R.string.latModel));
        double lng = Util.getDoublePreferenceModel(this, getString(R.string.lngModel));

        if (lat != 0.0 && lng != 0.0)
            position = new LatLng(lat, lng);

        // Get model
        if (getIntent().getExtras() != null) {
            int id = getIntent().getIntExtra("idCampaign", -1);
            if (id != -1) {
                initializated = Util.getBoolPreferenceModel(this, getString(R.string.isModelLoaded));
                if (initializated) {
                    DBVirde.getInstance(this).selectModel(Util.getIntPreferenceModel(this, getString(R.string.activeModel)));
                } else {
                    Virde.getInstance(this).getModelCampaign(id);
                    Util.saveBoolPreferenceModel(this, getString(R.string.isModelLoaded), true);
                }
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

                    // Hide loading feedback
                    loading.setVisibility(View.GONE);

                    // Google Api Client - Location
                    buildGoogleApiClient();
                    mGoogleApiClient.connect();

                    // Model
                    ArrayList<ItemModel> items = new ArrayList<>();

                    for (Object object : result.second) {
                        items.add((ItemModel) object);
                    }

                    // Active Model to Preferences
                    if (items.size() > 0) {
                        Util.saveIntPreferenceModel(this, getString(R.string.activeModel), items.get(0).getIdCampaign());
                    }

                    // Model to DB
                    DBVirde.getInstance(this).insertModel(items);

                    // Draw model
                    drawModel(items);

                }
                break;

            case Virde.FROM_SEND_CONTRIBUTION:
                if (result.first.isError()) {
                    Toast.makeText(getApplicationContext(), result.first.getMensaje(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.contributionSended), Toast.LENGTH_SHORT).show();
                    finish();
                }
                dialog.dismiss();
                break;
        }
    }

    public void drawModel (ArrayList<ItemModel> items) {

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

        if (initializated) {
            setPic();
            if (fromCamera)
                ToolCam.galleryAddPic(getApplicationContext());
        }

    }

    public void getDataFromLayout () {

        dialog = new ProgressDialogFragment();
        dialog.show(getFragmentManager(), "Sending contribution");

        ArrayList<ItemFormContribution> values = new ArrayList<>();

        // Id Campaign
        int id = -1;
        if (getIntent().getExtras() != null)
            id = getIntent().getIntExtra("idCampaign", -1);

        // Add idCampaign and Token
        values.add(new ItemFormContribution("idCampaign", String.valueOf(id)));
        values.add(new ItemFormContribution("token", Util.getPreference(this, getString(R.string.token))));

        // i=0 => LinearLayout loading
        for (int i=1; i<layout.getChildCount(); i++) {

            LinearLayout view = (LinearLayout) layout.getChildAt(i);
            String tag = view.getChildAt(0).getTag().toString();
            String[] data;
            String key;

            switch (tag) {
                case ItemModel.ITEM_EDIT_TEXT:
                case ItemModel.ITEM_EDIT_TEXT_BIG:
                case ItemModel.ITEM_EDIT_NUMBER:
                    data = Model.getEditText(view);
                    values.add(new ItemFormContribution(data[0], data[1]));
                    break;

                case ItemModel.ITEM_DATE:
                    data = Model.getDate(view);
                    values.add(new ItemFormContribution(data[0], data[1]));
                    break;

                case ItemModel.ITEM_DATETIME:
                    break;

                case ItemModel.ITEM_GEOPOS:

                    String userPosition = String.valueOf(position.latitude) + "," + String.valueOf(position.longitude);
                    key = view.getTag().toString();

                    values.add(new ItemFormContribution(key, userPosition));

                    break;

                case ItemModel.ITEM_IMAGE:

                    key = view.getTag().toString();

                    if (mCurrentPhotoPath != null)
                        if (!mCurrentPhotoPath.equals(""))
                            values.add(new ItemFormContribution(key, mCurrentPhotoPath, true));
                    break;

                case ItemModel.ITEM_FILE:
                    break;

                case ItemModel.ITEM_SPINNER:
                    data = Model.getStringSpinner(view);
                    values.add(new ItemFormContribution(data[0], data[1]));
                    break;
            }

        }

        Virde.getInstance(this).sendContribution(values);

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

            if (position == null)
                position = new LatLng(lat, lng);

            changeImgLocation(position);

        }

    }

    public void changeImgLocation (LatLng latLng) {

        String url = "http://maps.google.com/maps/api/staticmap?center=" + latLng.latitude + ","
                + latLng.longitude + "&zoom=15&size=300x200&scale=2&sensor=false&markers=color:red%7C"
                + latLng.latitude + ","+ latLng.longitude
                + "&key=" + getString(R.string.google_maps_key);

        if (imgLocation != null)
            Picasso.with(getApplicationContext())
                    .load(url)
                    .into(imgLocation);

    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contribution, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                Util.restoreModelPreferences(this);
                finish();
                return true;
            case R.id.action_send:
                getDataFromLayout();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /** MAP **/

    public void actionMap () {

        Intent intent = new Intent(this, Map.class);

        if (position != null) {
            intent.putExtra("lat", position.latitude);
            intent.putExtra("lng", position.longitude);
        }

        startActivityForResult(intent, REQUEST_MAP);

    }

    /** DATA BASE **/

    @Override
    public void onDBApiInsertResult(Result result) {
        switch (result.getResultFrom()) {
            case DBVirde.FROM_INSERT_MODEL:
                if (result.isError()) {
                    Log.e("DBVirde", "Model not inserted");
                }
                break;
        }
    }

    @Override
    public void onDBApiSelectResult(Pair<Result, ArrayList> result) {
        switch (result.first.getResultFrom()) {
            case DBVirde.FROM_SELECT_MODEL:
                if (result.first.isError()) {
                    Log.e("DBVirde", "Model not selected");
                } else {

                    // Hide loading feedback
                    loading.setVisibility(View.GONE);

                    // Google Api Client - Location
                    buildGoogleApiClient();
                    mGoogleApiClient.connect();

                    // Model
                    ArrayList<ItemModel> items = new ArrayList<>();

                    for (Object object : result.second) {
                        items.add((ItemModel) object);
                    }

                    drawModel(items);

                }
                break;
        }
    }

    @Override
    public void onDBApiUpdateResult(Result result) {



    }

    /** CAMERA **/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_MAP && resultCode == RESULT_OK) {

            Double lat = data.getDoubleExtra("lat", 0.0);
            Double lng = data.getDoubleExtra("lng", 0.0);
            position = new LatLng(lat, lng);

            Util.saveDoublePreferenceModel(this, getString(R.string.latModel), lat);
            Util.saveDoublePreferenceModel(this, getString(R.string.lngModel), lng);

            changeImgLocation(position);

        } else if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {

            if (data == null) { // Camera

                if (!initializated) {
                    setPic();
                    ToolCam.galleryAddPic(getApplicationContext());
                }
                fromCamera = true;

            } else {

                if (data.getData() == null) { // Camera
                    setPic();
                    ToolCam.galleryAddPic(getApplicationContext());
                    fromCamera = true;
                } else { // Gallery

                    // Eliminar archivo temporal si se escogía la cámara
                    ToolCam.deleteUnusedFile(mCurrentPhotoPath);

                    mCurrentPhotoPath = ToolCam.getPath(getApplicationContext(), data);

                    if (!initializated)
                        setPic();

                }
            }

        } else {
            ToolCam.deleteUnusedFile(mCurrentPhotoPath);
        }

    }

    /**
     * Intent selector para imagen de perfil desde galería o cámara
     */
    public void intentCameraGallery(int id) {

        Util.saveIntPreferenceModel(this, getString(R.string.idImage), id);

        // Camera
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            try {
                File f = ToolCam.setUpPhotoFile(this);
                mCurrentPhotoPath = f.getAbsolutePath();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
            } catch (IOException e) {
                e.printStackTrace();
                mCurrentPhotoPath = null;
            }

        }

        // Gallery
        Intent galleryIntent =  new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // Chooser Intent
        Intent chooserIntent = Intent.createChooser(galleryIntent, "Foto de prefil");
        List<Intent> intents = new ArrayList<>();
        intents.add(takePictureIntent);

        // Add camera option and lauch chooser
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents.toArray(new Parcelable[intents.size()]));
        startActivityForResult(chooserIntent, REQUEST_IMAGE);

    }

    /**
     * Carga la imagen de la galería y la establece en un ImageView
     */
    @SuppressWarnings("deprecation")
    private void setPic() {

        int id = Util.getIntPreferenceModel(this, getString(R.string.idImage));

        LinearLayout linearLayout = (LinearLayout)findViewById(id);
        ImageView image = (ImageView)linearLayout.getChildAt(0);

        // Get the dimensions of the View
        int targetW = image.getWidth();
        int targetH = image.getHeight();

        if (targetW == 0 && targetH == 0) {
            targetW = 600;
            targetH = 220;
        }

        if (mCurrentPhotoPath == null)
            mCurrentPhotoPath = Util.getStPreferenceModel(this, getString(R.string.currentPhotoPath));

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        image.setImageBitmap(bitmap);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Util.restoreModelPreferences(this);
    }

    public static class ProgressDialogFragment extends DialogFragment {

//        static ProgressDialogFragment frag;
//
//        public static ProgressDialogFragment newInstance() {
//            frag = new ProgressDialogFragment();
//            return frag;
//        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final ProgressDialog dialog = new ProgressDialog(getActivity());
            dialog.setMessage(getString(R.string.sendingContribution));
            dialog.setIndeterminate(true);
            this.setCancelable(false);
            return dialog;
        }

    }

}
