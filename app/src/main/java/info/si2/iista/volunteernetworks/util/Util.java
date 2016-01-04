package info.si2.iista.volunteernetworks.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.parse.DeleteCallback;
import com.parse.Parse;
import com.parse.ParseInstallation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import info.si2.iista.volunteernetworks.R;
import info.si2.iista.volunteernetworks.apiclient.ItemParse;
import info.si2.iista.volunteernetworks.apiclient.ItemServer;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 4/9/15
 * Project: Virde
 */
public class Util {

    // Typeface
    private static Typeface robotoLight;

    /**
     * Conversor de pixels a dp
     * @param c Contexto
     * @param pixels Pixels a convertir
     * @return Pixels en dp
     */
    public static int convertPixelsToDp (Context c, int pixels) {
        return (int) (pixels / c.getResources().getDisplayMetrics().density);
    }

    /**
     * Conversor de dp a pixels
     * @param c Contexto
     * @param dp Dp a convertir
     * @return dp en Pixels
     */
    public static int convertDpToPixel(Context c, float dp){
        return (int) (dp * (c.getResources().getDisplayMetrics().densityDpi / 160f));
    }

    /**
     * Convierte una fecha en un String con formato dd MMM yyyy
     * @param date Fecha a convertir a String
     * @return String de fecha con formato dd MMM yyyy
     */
    public static String parseDateToString (Date date) {

        if (date != null)
            return DateFormat.format("dd, MMM yyyy", date).toString();
        else
            return "";

    }

    /**
     * Convierte una fecha en String utilizado en las contribuciones
     * @param date Date a convertir
     * @return String de la fecha deseada
     */
    public static String parseDateHourToString (Date date) {

        if (date != null)
            return DateFormat.format("dd, MMM yyyy", date).toString();
        else
            return "";

    }

    /**
     * Convierte una fecha de formato String a un objeto de tipo Date
     * @param dateSt String a convertir a Date
     * @return Date con la fecha especificada en dateSt
     */
    public static Date parseDateHourStringToDate (String dateSt) {

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return format.parse(dateSt);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Convierte una fecha en un String con formato yyyy-MM-dd
     * @param date Fecha a convertir a String
     * @return String de fecha con formato yyyy-MM-dd
     */
    public static String parseDateToStringServer (Date date) {

        if (date != null)
            return DateFormat.format("dd, MMM yyyy", date).toString();
        else
            return "";

    }

    /**
     * Convierte una fecha de formato String a un objeto de tipo Date
     * @param dateSt String a convertir a Date
     * @return Date con la fecha especificada en dateSt
     */
    public static Date parseStringToDate (String formatSt, String dateSt) {

        try {
            SimpleDateFormat format = new SimpleDateFormat(formatSt, Locale.getDefault());
            return format.parse(dateSt);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String parseDateToString (String formatSt, Date date) {

        if (date != null)
            return DateFormat.format(formatSt, date).toString();
        else
            return "";

    }

    /**
     * Comprueba si el dispositivo dispone de internet
     * @param c Context
     * @return True si se dispone de internet, False si no
     */
    public static boolean checkInternetConnection (Context c) {

        ConnectivityManager conMgr = (ConnectivityManager) c
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo i = conMgr.getActiveNetworkInfo();
        return i != null && i.isConnected() && i.isAvailable();

    }

    /**
     * Devuelve el token del usuario
     * @param context Contexto del que se quiere obtener el token
     * @return String token
     */
    public static String getToken (Context context) {

        return Util.getPreference(context, context.getString(R.string.token));

    }

    /**
     * Guarda un valor asociado a una key en SharedPreferences
     * @param c Context
     * @param key String key del valor
     * @param value String valor a guardar
     */
    public static void savePreference (Context c, String key, String value) {

        SharedPreferences sharedPref = c.getSharedPreferences(c.getString(R.string.userPreferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();

    }

    /**
     * Guarda un valor asociado a una key en SharedPreferences de las preferencias del usuario
     * @param c Context
     * @param key String key del valor
     * @param value Integer valor a guardar
     */
    public static void saveIntPreference (Context c, String key, int value) {

        SharedPreferences sharedPref = c.getSharedPreferences(c.getString(R.string.userPreferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.apply();

    }

    /**
     * Guarda un valor asociado a una key en SharedPreferences de las preferencias del usuario
     * @param c Context
     * @param key String key del valor
     * @param value Integer valor a guardar
     */
    public static void saveBoolPreference (Context c, String key, boolean value) {

        SharedPreferences sharedPref = c.getSharedPreferences(c.getString(R.string.userPreferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.apply();

    }

    /**
     * Obtiene un valor guardado en SharedPreferences
     * @param c Context
     * @param key key que hace referencia al valor
     * @return String valor de la key
     */
    public static String getPreference (Context c, String key) {

        SharedPreferences sharedPref = c.getSharedPreferences(c.getString(R.string.userPreferences), Context.MODE_PRIVATE);
        return sharedPref.getString(key, "");

    }

    /**
     * Obtiene un valor guardado en SharedPreferences
     * @param c Context
     * @param key key que hace referencia al valor
     * @return Integer valor de la key
     */
    public static int getIntPreference (Context c, String key) {

        SharedPreferences sharedPref = c.getSharedPreferences(c.getString(R.string.userPreferences), Context.MODE_PRIVATE);
        return sharedPref.getInt(key, -1);

    }

    /**
     * Obtiene un valor guardado en SharedPreferences del modelo
     * @param c Context
     * @param key key que hace referencia al valor
     * @return Boolean valor de la key
     */
    public static boolean getBoolPreference (Context c, String key) {

        SharedPreferences sharedPref = c.getSharedPreferences(c.getString(R.string.userPreferences), Context.MODE_PRIVATE);

        if (!sharedPref.contains(key))
            return false;

        return sharedPref.getBoolean(key, false);

    }

    /**
     * Guarda un valor asociado a una key en SharedPreferences de un modelo de campaña
     * @param c Context
     * @param key String key del valor
     * @param value String valor a guardar
     */
    public static void saveStPreferenceModel (Context c, String key, String value) {

        SharedPreferences sharedPref = c.getSharedPreferences(c.getString(R.string.modelPreferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();

    }

    /**
     * Obtiene un valor guardado en SharedPreferences
     * @param c Context
     * @param key key que hace referencia al valor
     * @return String valor de la key
     */
    public static String getStPreferenceModel(Context c, String key) {

        SharedPreferences sharedPref = c.getSharedPreferences(c.getString(R.string.modelPreferences), Context.MODE_PRIVATE);
        return sharedPref.getString(key, "");

    }

    /**
     * Guarda un valor asociado a una key en SharedPreferences de un modelo de campaña
     * @param c Context
     * @param key String key del valor
     * @param value Integer valor a guardar
     */
    public static void saveIntPreferenceModel (Context c, String key, int value) {

        SharedPreferences sharedPref = c.getSharedPreferences(c.getString(R.string.modelPreferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.apply();

    }

    /**
     * Obtiene un valor guardado en SharedPreferences
     * @param c Context
     * @param key key que hace referencia al valor
     * @return Integer valor de la key
     */
    public static int getIntPreferenceModel(Context c, String key) {

        SharedPreferences sharedPref = c.getSharedPreferences(c.getString(R.string.modelPreferences), Context.MODE_PRIVATE);
        return sharedPref.getInt(key, -1);

    }

    /**
     * Guarda un valor asociado a una key en SharedPreferences de un modelo de campaña
     * @param c Context
     * @param key String key del valor
     * @param value Boolean valor a guardar
     */
    public static void saveBoolPreferenceModel (Context c, String key, boolean value) {

        SharedPreferences sharedPref = c.getSharedPreferences(c.getString(R.string.modelPreferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        editor.apply();

    }

    /**
     * Obtiene un valor guardado en SharedPreferences del modelo
     * @param c Context
     * @param key key que hace referencia al valor
     * @return Boolean valor de la key
     */
    public static boolean getBoolPreferenceModel (Context c, String key) {

        SharedPreferences sharedPref = c.getSharedPreferences(c.getString(R.string.modelPreferences), Context.MODE_PRIVATE);

        if (!sharedPref.contains(key))
            return false;

        return sharedPref.getBoolean(key, false);

    }

    /**
     * Guarda un valor asociado a una key en SharedPreferences de un modelo de campaña
     * @param c Context
     * @param key String key del valor
     * @param value Double valor a guardar
     */
    public static void saveDoublePreferenceModel (Context c, String key, double value) {

        SharedPreferences sharedPref = c.getSharedPreferences(c.getString(R.string.modelPreferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat(key, (float) value);
        editor.apply();

    }

    /**
     * Obtiene un valor guardado en SharedPreferences
     * @param c Context
     * @param key key que hace referencia al valor
     * @return Double valor de la key
     */
    public static double getDoublePreferenceModel (Context c, String key) {

        SharedPreferences sharedPref = c.getSharedPreferences(c.getString(R.string.modelPreferences), Context.MODE_PRIVATE);

        if (!sharedPref.contains(key))
            return 0.0;

        return sharedPref.getFloat(key, (float) 0.0);

    }

    /**
     * Restablece las preferencias de visualicación de un Modelo
     */
    public static void restoreModelPreferences(Context c) {

        SharedPreferences sharedPref = c.getSharedPreferences(c.getString(R.string.modelPreferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(c.getString(R.string.currentPhotoPath), "");
        editor.putInt(c.getString(R.string.activeModel), -1);
        editor.putInt(c.getString(R.string.idImage), -1);
        editor.putBoolean(c.getString(R.string.isModelLoaded), false);
        editor.putFloat(c.getString(R.string.latModel), 0.0f);
        editor.putFloat(c.getString(R.string.lngModel), 0.0f);
        editor.apply();

    }

    public static ColorStateList getStatesSuscribe (Context context) {

        int[][] states = new int[][] {
                new int[] {android.R.attr.state_pressed},
                new int[] {android.R.attr.state_focused},
                new int[] {android.R.attr.state_enabled}
        };

        int[] colors = new int[] {
                ContextCompat.getColor(context, R.color.white),
                ContextCompat.getColor(context, R.color.white),
                ContextCompat.getColor(context, R.color.primary)
        };

        return new ColorStateList(states, colors);

    }

    public static ColorStateList getStatesUnsuscribe (Context context) {

        int[][] states = new int[][] {
                new int[] {android.R.attr.state_pressed},
                new int[] {android.R.attr.state_focused},
                new int[] {android.R.attr.state_enabled}
        };

        int[] colors = new int[] {
                ContextCompat.getColor(context, R.color.white),
                ContextCompat.getColor(context, R.color.white),
                ContextCompat.getColor(context, R.color.unsuscribe)
        };

        return new ColorStateList(states, colors);

    }

    /**
     * Cambia de color un drawable
     * @param drawable Drawable a modificar
     * @param color Color a aplicar al drawable
     * @return Drawable modificado
     */
    public static Drawable tintDrawable (Drawable drawable, int color) {

        drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        return drawable;

    }

    /**
     * Cambio de fuente de TextView
     * @param c Contexto en el que se encuentra
     * @param tv TextView a modificar fuente
     * @param font Fuente/Estilo a setear
     */
    public static void setFont (Context c, TextView tv, Font font) {

        if (robotoLight == null)
            robotoLight = getRobotoLight(c);

        switch (font) {

            case LIGHT:
                tv.setTypeface(robotoLight);
                break;

        }

    }

    /**
     * Devuelve la fuente Roboto Light
     * @param c Context
     * @return Typeface Roboto Light
     */
    public static Typeface getRobotoLight (Context c) {
        return Typeface.createFromAsset(c.getAssets(), "fonts/Roboto-Light.ttf");
    }

    /**
     * Make Toast
     * @param c Context
     * @param msg Mensaje a mostrar
     * @param length Duración del toast. 0 = LENGHT_SHORT, 1 = LENGHT_LONG
     */
    public static void makeToast (Context c, String msg, int length) {
        Toast.makeText(c, msg, length).show();
    }

    /**
     * Init or update server info
     * @param c Context
     * @param server Server to save or update
     */
    public static void initServerSharedPreferences (Context c, ItemServer server) {

        SharedPreferences sharedPref = c.getSharedPreferences(c.getString(R.string.userPreferences), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(c.getString(R.string.id_server), server.getId());
        editor.putString(c.getString(R.string.serverUrl), server.getUrl());
        editor.putString(c.getString(R.string.serverName), server.getName());
        editor.putString(c.getString(R.string.serverDesc), server.getDescription());
        editor.putString(c.getString(R.string.serverMapsApi), server.getMapsKeys().getApi());
        editor.putString(c.getString(R.string.serverParseApi), server.getParseKeys().getApi());
        editor.putString(c.getString(R.string.serverParseKey), server.getParseKeys().getKey());
        editor.putBoolean(c.getString(R.string.isDefaultServer), true);
        editor.apply();

        // Change Parse initialization
//        initParse(c, server.getParseKeys());

    }

    /**
     * Parse initialization
     * @param c Context
     * @param parse Parse aplicationID and clientKey
     */
    private static void initParse (final Context c, final ItemParse parse) {

        boolean isParseInit = getBoolPreference(c, c.getString(R.string.isParseInitializated));
        String parseAppID = Util.getPreference(c, c.getString(R.string.serverParseApi));
        String clientKey = Util.getPreference(c, c.getString(R.string.serverParseKey));

        if (!isParseInit) { // Init Parse

            Parse.enableLocalDatastore(c);
            Parse.initialize(c, parse.getApi(), parse.getKey());
            ParseInstallation.getCurrentInstallation().saveInBackground();

            Util.savePreference(c, c.getString(R.string.serverParseApi), parse.getApi());
            Util.savePreference(c, c.getString(R.string.serverParseKey), parse.getKey());

            Util.saveBoolPreference(c, c.getString(R.string.isParseInitializated), true);

        } else if (parseAppID.equals(parse.getApi()) && clientKey.equals(parse.getKey())) { // Change parse account

            if (ParseInstallation.getCurrentInstallation() != null) {
                ParseInstallation.getCurrentInstallation().deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(com.parse.ParseException e) {

                        Parse.enableLocalDatastore(c);
                        Parse.initialize(c, parse.getApi(), parse.getKey());
                        ParseInstallation.getCurrentInstallation().saveInBackground();

                        Util.savePreference(c, c.getString(R.string.serverParseApi), parse.getApi());
                        Util.savePreference(c, c.getString(R.string.serverParseKey), parse.getKey());

                    }
                });
            }

        }

    }

    /** File .GPX **/

    public static String saveGpxFile (Context c, ArrayList<LatLng> locations, String fileName) {

        // File name
        Date now = new Date();
        String dateNowSt = parseDateToString("yyyyMMddHHmmss", now);

        // File content
        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?><gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"MapSource 6.15.5\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\"><trk>\n";
        String name = "<name>" + fileName + "</name><trkseg>\n";

        String segments = "";
        for (LatLng latLng : locations) {
            segments += "<trkpt lat=\"" + latLng.latitude + "\" lon=\"" + latLng.longitude + "\">\n";
            segments += "<ele>0</ele>\n";
            segments += "<name>0</name>\n";
            segments += "<time>0</time>\n";
            segments += "</trkpt>\n";
        }

        String footer = "</trkseg></trk></gpx>";

        // File to save
        String fileDir = Environment.getExternalStorageDirectory() + "/" + c.getString(R.string.base_dir)
                         + "/" + c.getString(R.string.gpx_dir_name) + "/" + fileName + ".gpx";
        File file = new File (fileDir);

        // Save file
        boolean dirExist;
        if (!file.getParentFile().exists())
            dirExist = file.getParentFile().mkdirs();
        else
            dirExist = true;

        if (dirExist) {
            try {
                FileWriter writer = new FileWriter(file, false);
                writer.append(header);
                writer.append(name);
                writer.append(segments);
                writer.append(footer);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                Log.e("GPX File", "Error Writting Path", e);
            }

            return fileDir;

        } else {
            return null;
        }

    }

    /** Dialog **/

    /**
     * AlertView constructor
     * @param c Context
     * @param message Message will be show
     * @param okListener listener when user click "ok" button
     */
    public static void showMessageOKCancel(Context c, String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(c)
                .setMessage(message)
                .setPositiveButton(c.getString(R.string.ok), okListener)
                .setNegativeButton(c.getString(R.string.cancel), null)
                .create()
                .show();
    }

    /**
     * AlertView constructor
     * @param c Context
     * @param message Message will be show
     * @param okListener listener when user click "ok" button
     */
    public static void showMessageOK(Context c, String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(c)
                .setMessage(message)
                .setPositiveButton(c.getString(R.string.ok), okListener)
                .create()
                .show();
    }

    /**
     * Elimina los acentos de una cadena String
     * @param s String a eliminar acentos
     * @return String sin acentos
     */
    public static String stripAccents(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }

}
