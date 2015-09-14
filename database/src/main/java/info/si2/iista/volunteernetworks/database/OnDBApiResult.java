package info.si2.iista.volunteernetworks.database;

import android.util.Pair;

import java.util.ArrayList;

import info.si2.iista.volunteernetworks.apiclient.Result;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 11/9/15
 * Project: Virde
 */
public interface OnDBApiResult {

    void onDBApiInsertResult(Result result);
    void onDBApiSelectResult(Pair<Result, ArrayList> result);

}
