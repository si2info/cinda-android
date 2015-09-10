package info.si2.iista.bolunteernetworks.apiclient;

import android.util.Pair;

import java.util.ArrayList;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 7/9/15
 * Project: Virde
 */
public interface OnApiClientResult {

    void onApiClientRequestResult(Pair<Result, ArrayList> result);

}
