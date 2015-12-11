package info.si2.iista.volunteernetworks;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 9/12/15
 * Project: Shiari
 */
public class TrackingService extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // Actions
    public static final String ACTION_INIT = "Iniciar";
    public static final String ACTION_PAUSE = "Pausar";
    public static final String ACTION_STOP = "Parar";
    public static final String ACTION_CONTINUE = "Continuar";

    // Notification
    public static final int NOTIFICATION_TRACKING_ID = 417;

    // Update interval
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 10;

    // Location
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    public ArrayList<LatLng> locationsRecorded = new ArrayList<>();

    // Google Api Client
    private GoogleApiClient mGoogleApiClient;

    // Instance
    private static TrackingService trackingService;

    /** method for clients */

    public static TrackingService getInstance () {
        return trackingService;
    }

    public ArrayList<LatLng> getLocationsRecorded() {

        return locationsRecorded;

    }

    /** Class method **/
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mGoogleApiClient.disconnect();
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String action = intent.getAction();

        switch (action) {

            case ACTION_INIT:
                Tracking.menuType = Tracking.MENU_PAUSE_STOP;
                NotificationCompat.Builder builder = createTrackingNotification(this, true);
                startForeground(NOTIFICATION_TRACKING_ID, builder.build());
                trackingService = this;
                break;

            case ACTION_PAUSE:
                Tracking.menuType = Tracking.MENU_CONTINUE_STOP;
                stopLocationUpdates();
                updateTrackingNotification(false);
                break;

            case ACTION_STOP:
                Tracking.menuType = Tracking.MENU_PLAY;
                actionStop();
                break;

            case ACTION_CONTINUE:
                Tracking.menuType = Tracking.MENU_PAUSE_STOP;
                startLocationUpdates();
                updateTrackingNotification(true);
                break;
        }

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Init location request
        createLocationRequest();

        // init Google Api Client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

    }

    /**
     * Stop location updates and start activity Tracking to send the new contribution
     */
    public void actionStop () {

        // Stop location updates
        stopLocationUpdates();

        // Stop service
        stopForeground(true);
        stopSelf();

        // Intent to send Track
        Intent intent = new Intent(this, Tracking.class);
        intent.putExtra("actionSend", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    /** Google Api Client **/
    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /** Location listener **/
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        saveLocation();
    }

    /**
     * Start updates of location
     */
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    /**
     * Stop LocationRequest
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    /**
     * Init LocationRequest
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Save location to show the travel
     */
    private void saveLocation () {

        LatLng currentLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        locationsRecorded.add(currentLatLng);

    }

    /** Notifications **/

    public void updateTrackingNotification (boolean hastPause) {

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mNotifyBuilder = createTrackingNotification(this, hastPause);

        mNotificationManager.notify(NOTIFICATION_TRACKING_ID, mNotifyBuilder.build());

    }

    public NotificationCompat.Builder createTrackingNotification (Context context, boolean hasPause) {

        // Notification
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setOngoing(true)
                        .setVisibility(Notification.VISIBILITY_PUBLIC)
                        .setContentText("Campaña X");

        // Action notification
        mBuilder.setContentIntent(getTrackingPI(context));

        // Action buttons notification
        if (hasPause)
            mBuilder.addAction(getPauseTrakingPI(context));
        else
            mBuilder.addAction(getPlayTrakingPI(context));
        mBuilder.addAction(getStopTrakingPI(context));

        return mBuilder;

    }

    private static PendingIntent getTrackingPI (Context context) {

        Intent resultIntent = new Intent(context, Tracking.class);

        return PendingIntent.getActivity(
                context,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

    }

    private static NotificationCompat.Action getPlayTrakingPI (Context context) {

        Intent intent = new Intent(context, TrackingService.class);
        intent.setAction(TrackingService.ACTION_CONTINUE);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, 0);

        return new NotificationCompat.Action(R.drawable.ic_play_arrow_white_24dp,
                context.getString(R.string.action_continue), pi);

    }

    private static NotificationCompat.Action getPauseTrakingPI (Context context) {

        Intent intent = new Intent(context, TrackingService.class);
        intent.setAction(TrackingService.ACTION_PAUSE);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, 0);

        return new NotificationCompat.Action(R.drawable.ic_pause_white_24dp,
                context.getString(R.string.action_pause), pi);

    }

    private static NotificationCompat.Action getStopTrakingPI (Context context) {

        Intent intent = new Intent(context, TrackingService.class);
        intent.setAction(TrackingService.ACTION_STOP);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, 0);

        return new NotificationCompat.Action(R.drawable.ic_stop_white_24dp,
                context.getString(R.string.action_stop), pi);

    }

}
