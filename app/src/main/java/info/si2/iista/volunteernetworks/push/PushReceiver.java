package info.si2.iista.volunteernetworks.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import info.si2.iista.volunteernetworks.Campaign;
import info.si2.iista.volunteernetworks.R;
import info.si2.iista.volunteernetworks.util.Util;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 25/11/15
 * Project: Virde
 */
public class PushReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String title = "";
        String message = "";
        int id = 0;

        try {

            JSONObject object = new JSONObject(intent.getExtras().getString("com.parse.Data"));
            JSONObject push = object.getJSONObject("aps");

            if(push.has("alert"))
                title = push.getString("alert");

            if (push.has("content"))
                message = push.getString("content");

            if (push.has("cid"))
                id = push.getInt("cid");

            generateNotification(context, id, title, message);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void generateNotification(Context context, int id, String title, String message) {

        Intent intent = new Intent(context, Campaign.class);
        intent.putExtra("fromPush", true);
        intent.putExtra("idCampaign", id);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        int appColorInt = ContextCompat.getColor(context, R.color.primary);
        String appColorSt = "#"+Integer.toHexString(appColorInt);
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);

        Uri soundUri = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context)
                .setTicker(context.getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_notification)
                .setLights(Color.parseColor(appColorSt), 300, 1000)
                .setColor(appColorInt)
                .setLargeIcon(largeIcon)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setContentIntent(pendingIntent)
                .setSound(soundUri)
                .setAutoCancel(true).build();
        mNotificationManager.notify(getNumNotification(context), notification);

    }

    /**
     * Genera un número entero consecutivo identificador de la notificación
     * @param c Context
     * @return Integer
     */
    private int getNumNotification (Context c) {

        int not = Util.getIntPreference(c, c.getString(R.string.nNotification));
        not++;
        Util.saveIntPreference(c, c.getString(R.string.nNotification), not);

        return not;

    }

}
