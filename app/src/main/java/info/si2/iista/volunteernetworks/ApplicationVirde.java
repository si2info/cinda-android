package info.si2.iista.volunteernetworks;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 16/9/15
 * Project: Virde
 */
public class ApplicationVirde extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Crashlytics
        Fabric.with(this, new Crashlytics());

    }
}
