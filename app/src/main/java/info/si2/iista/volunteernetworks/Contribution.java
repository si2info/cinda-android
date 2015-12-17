package info.si2.iista.volunteernetworks;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import info.si2.iista.volunteernetworks.apiclient.Dictionary;
import info.si2.iista.volunteernetworks.apiclient.ItemFormContribution;
import info.si2.iista.volunteernetworks.apiclient.ItemModel;
import info.si2.iista.volunteernetworks.apiclient.ItemModelValue;
import info.si2.iista.volunteernetworks.apiclient.OnApiClientResult;
import info.si2.iista.volunteernetworks.apiclient.Result;
import info.si2.iista.volunteernetworks.apiclient.Virde;
import info.si2.iista.volunteernetworks.camera.ToolCam;
import info.si2.iista.volunteernetworks.database.DBVirde;
import info.si2.iista.volunteernetworks.database.OnDBApiResult;
import info.si2.iista.volunteernetworks.util.ContributionDialog;
import info.si2.iista.volunteernetworks.util.Model;
import info.si2.iista.volunteernetworks.util.Util;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 8/9/15
 * Project: Virde
 */
public class Contribution extends AppCompatActivity implements OnApiClientResult, GoogleApiClient.ConnectionCallbacks,
        OnConnectionFailedListener, OnDBApiResult, ContributionDialog.ContributionDialogListener {

    // Model
    private ArrayList<ItemModel> model;
    private ArrayList<ItemFormContribution> values;
    private ArrayList<ItemModelValue> dbValues;

    // Views
    private RelativeLayout layout;
    private LinearLayout loading;

    // Google Services
    private GoogleApiClient mGoogleApiClient;

    // Location
    private static final int REQUEST_MAP = 0x1;
    private RelativeLayout viewLocation;
    private LatLng position = new LatLng(0, 0);

    // Dictionary
    private RelativeLayout viewDictionary;
    private String codeTerm = "";

    // Camera and gallery
    private static final int REQUEST_IMAGE = 0x2;
    private int idImage;
    private String mCurrentPhotoPath;
    private boolean fromCamera;

    // Flags
    private boolean initializated;

    // ProgresDialog
    private ProgressDialogFragment dialog;

    // Dictionary
    private Dictionary dictionary;

    // Request
    private static final int PERMISSIONS_REQUEST_LOCATION = 1;
    private static final int PERMISSIONS_REQUEST_CAMERA = 2;
    private static final int PERMISSIONS_REQUEST_CAMERA_AND_STORAGE = 3;
    public static final int DICTIONARY_VALUE_REQUEST = 4;

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
                    getModelCampaign(id);
                    Util.saveBoolPreferenceModel(this, getString(R.string.isModelLoaded), true);
                }
            } else {
                Toast.makeText(this, getString(R.string.noID), Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }

    /**
     * Obtiene el modelo de la campaña por internet si lo hay y si no de base de datos si ya se visualizó
     * @param id ID del modelo de la campaña a obtener
     */
    public void getModelCampaign (int id) {
        if (Util.checkInternetConnection(this)) {
            Virde.getInstance(this).getModelCampaign(id); // Model campaign from internet
        } else {
            DBVirde.getInstance(this).selectModel(id); // Model campaign from DB
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

                    model = new ArrayList<>();

                    for (Object object : result.second) {
                        model.add((ItemModel) object);
                    }

                    // Active Model to Preferences
                    if (model.size() > 0) {
                        Util.saveIntPreferenceModel(this, getString(R.string.activeModel), model.get(0).getIdCampaign());
                    }

                    // Model to DB
                    DBVirde.getInstance(this).insertModel(model);

                    // Draw model
                    drawModel(model);

                }
                break;

            case Virde.FROM_SEND_CONTRIBUTION:
                if (result.first.isError()) {
                    ContributionDialog dialog = new ContributionDialog();
                    dialog.show(getFragmentManager(), "Contribution dialog");
                } else {
                    DBVirde.getInstance(this).updateModelValue(dbValues);
                    Util.restoreModelPreferences(this);
                    Toast.makeText(this, getString(R.string.contributionSended), Toast.LENGTH_SHORT).show();
                    finish();
                }
                dialog.dismiss();
                break;

            case Virde.FROM_GET_DICTIONARY:
                if (result.first.isError()) {
                    Toast.makeText(getApplicationContext(), result.first.getMensaje(), Toast.LENGTH_SHORT).show();
                } else {
                    dictionary = (Dictionary)result.second.get(0);
                    DBVirde.getInstance(this).insertDictionary(dictionary);
                }
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

                String tag = view.getTag().toString();

                // Guardar View de location para asignar después
                if (tag.equals(ItemModel.ITEM_GEOPOS))
                    viewLocation = (RelativeLayout) view;

                // Test dictionary
                if (tag.equals(ItemModel.ITEM_DICTIONARY)) {
                    viewDictionary = (RelativeLayout) view;
                    dictionary = new Dictionary();
                    dictionary.setCode(Integer.valueOf(items.get(i).getFieldOptions()));
                    DBVirde.getInstance(this).checkIfDictionaryExists(dictionary.getCode());
                }

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

        // Id Campaign
        int id = -1;
        if (getIntent().getExtras() != null)
            id = getIntent().getIntExtra("idCampaign", -1);

        // Init ArrayList
        values = new ArrayList<>();

        // Add idCampaign and Token
        values.add(new ItemFormContribution("idCampaign", String.valueOf(id)));
        values.add(new ItemFormContribution("token", Util.getPreference(this, getString(R.string.token))));

        // i=0 => LinearLayout loading
        for (int i=1; i<layout.getChildCount(); i++) {

            RelativeLayout view = (RelativeLayout) layout.getChildAt(i);
            String tag = view.getTag().toString();
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

                    if (mCurrentPhotoPath != null) {
                        if (!mCurrentPhotoPath.equals("")) {
                            values.add(new ItemFormContribution(key, mCurrentPhotoPath, true));
                        } else {
                            values.add(new ItemFormContribution(key, "", false));
                        }
                    } else {
                        values.add(new ItemFormContribution(key, "", false));
                    }

                    break;

                case ItemModel.ITEM_FILE:
                    break;

                case ItemModel.ITEM_SPINNER:
                    data = Model.getStringSpinner(view);
                    values.add(new ItemFormContribution(data[0], data[1]));
                    break;

                case ItemModel.ITEM_DICTIONARY:
                    values.add(new ItemFormContribution(view.getTag().toString(), codeTerm));
                    break;

            }

        }

        addInfoItemSync();

    }

    /**
     * Añade a la contribución fecha y hora en la que se envía
     */
    public void addInfoItemSync () {

        // Campaign name
        String campaignName = getIntent().getExtras().getString("campaignName");
        values.add(new ItemFormContribution(ItemModel.ITEM_CAMPAIGN_NAME, campaignName));

        if (!model.get(model.size()-1).getFieldType().equals(ItemModel.ITEM_CAMPAIGN_NAME)) {
            ItemModel itemModel = new ItemModel();
            itemModel.setIdCampaign(model.get(0).getIdCampaign());
            itemModel.setFieldName(ItemModel.ITEM_CAMPAIGN_NAME);
            itemModel.setFieldType(ItemModel.ITEM_CAMPAIGN_NAME);
            model.add(itemModel);
        }

        // Date send
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy 'a las' HH:mm", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        values.add(new ItemFormContribution(ItemModel.ITEM_DATE_SEND, currentDateandTime));

        if (!model.get(model.size()-1).getFieldType().equals(ItemModel.ITEM_DATE_SEND)) {
            ItemModel itemModel = new ItemModel();
            itemModel.setIdCampaign(model.get(0).getIdCampaign());
            itemModel.setFieldName(ItemModel.ITEM_DATE_SEND);
            itemModel.setFieldType(ItemModel.ITEM_DATE_SEND);
            model.add(itemModel);
        }

        // Url server
        String urlServer = Util.getPreference(this, getString(R.string.serverUrl));
        values.add(new ItemFormContribution(ItemModel.ITEM_URL_SERVER, urlServer));

        if (!model.get(model.size()-1).getFieldType().equals(ItemModel.ITEM_URL_SERVER)) {
            ItemModel itemModel = new ItemModel();
            itemModel.setIdCampaign(model.get(0).getIdCampaign());
            itemModel.setFieldName(ItemModel.ITEM_URL_SERVER);
            itemModel.setFieldType(ItemModel.ITEM_URL_SERVER);
            model.add(itemModel);
        }

    }

    /**
     * Guarda los datos contribuidos a la campaña en DB
     * @param model Modelo de la campaña para contribuir datos
     * @param data Datos del usuario
     */
    public void saveContributionToDB (ArrayList<ItemModel> model, ArrayList<ItemFormContribution> data, boolean isSync) {

        dbValues = new ArrayList<>();

        for (int i=2; i<data.size(); i++) {
            ItemModel itemModel = model.get(i-2);
            ItemFormContribution itemForm = data.get(i);

            dbValues.add(new ItemModelValue(itemModel.getIdCampaign(), itemModel.getFieldName(),
                    itemForm.getValue(), itemModel.getFieldType(), itemModel.getFieldPosition(), isSync));
        }

        DBVirde.getInstance(this).insertModelValue(dbValues);

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

//            changeImgLocation(position);

        }

    }

    public void changeImgLocation (LatLng latLng) {

        String url = "http://maps.google.com/maps/api/staticmap?center=" + latLng.latitude + ","
                + latLng.longitude + "&zoom=15&size=300x200&scale=2&sensor=false&markers=color:red%7C"
                + latLng.latitude + ","+ latLng.longitude
                + "&key=" + getString(R.string.google_maps_key);

        if (viewLocation != null) {

            ImageView img = (ImageView)viewLocation.findViewById(R.id.mapLocation);
            LinearLayout view = (LinearLayout)viewLocation.findViewById(R.id.feedback);

            view.setVisibility(View.GONE);

            Picasso.with(getApplicationContext())
                    .load(url)
                    .into(img);
        }

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
                if (layout.getChildCount() > 1) {
                    getDataFromLayout();
                    saveContributionToDB(model, values, false);
                }
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

        if (Build.VERSION.SDK_INT >= 23) {
            checkMapPermission();
        } else {
            intentToMap();
        }

    }

    /** Grant Permission **/

    @TargetApi(Build.VERSION_CODES.M)
    public void checkMapPermission () {

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                Util.showMessageOKCancel(this, getString(R.string.permission_location),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(Contribution.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        PERMISSIONS_REQUEST_LOCATION);
                            }
                        });
            }

        } else {
            intentToMap();
        }

    }

    public void checkCameraAndStoragePermission(int idImage) {

        if (Build.VERSION.SDK_INT >= 23) {

            this.idImage = idImage;

            List<String> permissionsNeeded = new ArrayList<>();

            final List<String> permissionsList = new ArrayList<>();
            if (!addPermission(permissionsList, Manifest.permission.CAMERA))
                permissionsNeeded.add("Camera");
            if (!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE))
                permissionsNeeded.add("Read photos");
            if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                permissionsNeeded.add("Save photos");

            if (permissionsList.size() > 0) {
                if (permissionsNeeded.size() > 0) {

                    String message = getString(R.string.permission_camera_and_storage);

                    Util.showMessageOKCancel(this, message,
                            new DialogInterface.OnClickListener() {
                                @TargetApi(Build.VERSION_CODES.M)
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                            PERMISSIONS_REQUEST_CAMERA_AND_STORAGE);
                                }
                            });

                    return;
                }

                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        PERMISSIONS_REQUEST_CAMERA_AND_STORAGE);
                return;

            }

            intentCameraGallery(idImage);

        } else {
            intentCameraGallery(idImage);
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    /** Intents **/

    public void intentToMap () {

        Intent intent = new Intent(this, Map.class);

        if (position != null) {
            intent.putExtra("lat", position.latitude);
            intent.putExtra("lng", position.longitude);
        }

        startActivityForResult(intent, REQUEST_MAP);

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSIONS_REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { // Permission granted
                    intentToMap();
                }
                break;

            case PERMISSIONS_REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { // Permission granted
                    intentCameraGallery(idImage);
                }
                break;
            case PERMISSIONS_REQUEST_CAMERA_AND_STORAGE:

                HashMap<String, Integer> perms = new HashMap<>();

                // Initial
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);

                // Check permissions
                if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) { // All Permissions Granted

                    intentCameraGallery(idImage);

                } else { // Permission Denied
                    Toast.makeText(this, getString(R.string.permission_camera_and_storage_bad), Toast.LENGTH_SHORT).show();
                }

                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }

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
            case DBVirde.FROM_INSERT_MODELITEM:
                if (result.isError()) {
                    Log.e("DBVirde", "ModelValue not inserted");
                } else {

                    for (int i=0; i<dbValues.size(); i++) {
                        dbValues.get(i).setId(result.getCodigoError()); // Campo "CodigoError" reutilizado para enviar el id asignado
                        dbValues.get(i).setIsSync(true);
                    }

                    Virde.getInstance(this).sendContribution(values);
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
                    model = new ArrayList<>();

                    for (Object object : result.second) {
                        model.add((ItemModel) object);
                    }

                    drawModel(model);

                }
                break;
            case DBVirde.FROM_CHECK_IF_DICTIONARY_EXISTS:

                int exist = (Integer) result.second.get(0);

                if (exist == 0) {
                    int idServer = Util.getIntPreference(this, getString(R.string.id_server));
                    Virde.getInstance(this).getDictionary(model.get(0).getIdCampaign(), dictionary.getCode(), idServer);
                }

                break;
        }
    }

    @Override
    public void onDBApiUpdateResult(Result result) {
        switch (result.getResultFrom()) {
            case DBVirde.FROM_UPDATE_MODELITEM:
                if (result.isError()) {
                    Log.e("DBVirde", "ModelValue not updated");
                }
                break;
        }
    }

    /** CAMERA **/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == DICTIONARY_VALUE_REQUEST && resultCode == RESULT_OK) {

            codeTerm = data.getExtras().getString("codeTerm");
            String term = data.getExtras().getString("term");

            TextView textView = (TextView)viewDictionary.getChildAt(0);
            textView.setText(term);

        } else if (requestCode == REQUEST_MAP && resultCode == RESULT_OK) {

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

        RelativeLayout view = (RelativeLayout)findViewById(id);
        ImageView image = (ImageView)view.findViewById(R.id.imageSelected);
        LinearLayout feedback = (LinearLayout)view.findViewById(R.id.feedback);

        feedback.setVisibility(View.GONE);

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

    @Override
    public void onContributionDialogPositiveClick() {
        Util.restoreModelPreferences(this);
        finish();
    }

    @Override
    public void onContributionDialogAgainClick() {
        dialog = new ProgressDialogFragment();
        dialog.show(getFragmentManager(), "Sending contribution");
        Virde.getInstance(this).sendContribution(values);
    }

    /**
     * Diálogo de progreso al enviar una contribución
     */
    public static class ProgressDialogFragment extends DialogFragment {

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
