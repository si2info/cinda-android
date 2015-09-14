package info.si2.iista.volunteernetworks.util;

import android.content.Context;
import android.text.format.DateFormat;

import java.util.Date;

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

}
