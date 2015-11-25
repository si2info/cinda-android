package info.si2.iista.volunteernetworks;

import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.SaveCallback;

import io.fabric.sdk.android.Fabric;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 16/9/15
 * Project: Virde
 */
public class ApplicationVirde extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        // Crashlytics
        Fabric.with(this, new Crashlytics());

        // Parse
        Parse.initialize(this, getString(R.string.parseApplicationID), getString(R.string.parseClientKey));
        ParseInstallation.getCurrentInstallation().saveInBackground();

        if (BuildConfig.DEBUG) {
            suscribeDebugPushNotifications();
        } else {
            suscribePushNotifications();
        }

    }

    /**
     * Suscribir al usuario al canal de notificaciones push por defecto
     */
    public void suscribePushNotifications() {

        ParsePush.subscribeInBackground(getString(R.string.parseProductionChannel), new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to " + getString(R.string.parseProductionChannel));
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push " + getString(R.string.parseProductionChannel), e);
                }
            }
        });

    }

    /**
     * Suscribir al usuario al canal de notificaciones push por defecto y pruebas
     */
    public void suscribeDebugPushNotifications() {

        ParsePush.subscribeInBackground(getString(R.string.parseProductionChannel), new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to " + getString(R.string.parseProductionChannel));
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push " + getString(R.string.parseProductionChannel), e);
                }
            }
        });

        ParsePush.subscribeInBackground(getString(R.string.parseDebugChannel), new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to " + getString(R.string.parseDebugChannel));
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push " + getString(R.string.parseDebugChannel), e);
                }
            }
        });

    }

/**
Example
{"aps": {
"alert": "Titulo de prueba",
"content":"Esto es un mensaje con contenido de prueba al enviar una push con Parse"
}
}
*/

}
