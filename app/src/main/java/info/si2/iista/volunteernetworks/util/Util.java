package info.si2.iista.volunteernetworks.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateFormat;

import java.util.Date;

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

    public static String parseDateToString (Date date) {

        if (date != null)
            return DateFormat.format("dd MMM yyyy", date).toString();
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

}
