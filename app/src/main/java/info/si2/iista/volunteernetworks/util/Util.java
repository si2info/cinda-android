package info.si2.iista.volunteernetworks.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import info.si2.iista.volunteernetworks.R;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 4/9/15
 * Project: Virde
 */
public class Util {

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
     * Convierte una fecha en un String con formato yyyy-MM-dd
     * @param date Fecha a convertir a String
     * @return String de fecha con formato yyyy-MM-dd
     */
    public static String parseDateToStringServer (Date date) {

        if (date != null)
            return DateFormat.format("yyyy-MM-dd", date).toString();
        else
            return "";

    }

    /**
     * Convierte una fecha de formato String a un objeto de tipo Date
     * @param dateSt String a convertir a Date
     * @return Date con la fecha especificada en dateSt
     */
    public static Date parseStringToDate (String dateSt) {

        try {
            SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            return format.parse(dateSt);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

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
     * Obtiene un valor guardado en SharedPreferences
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

}
