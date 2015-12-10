package info.si2.iista.volunteernetworks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 9/12/15
 * Project: Shiari
 */
public class NotificationReceiver extends BroadcastReceiver {

    // Actions
    public static final String ACTION_PAUSE = "Pausar";
    public static final String ACTION_STOP = "Parar";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        switch (action) {
            case ACTION_PAUSE:
                break;
            case ACTION_STOP:

                break;
        }

    }


}
