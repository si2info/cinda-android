package info.si2.iista.volunteernetworks.apiclient;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;

import java.util.ArrayList;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 7/9/15
 * Project: Virde
 */
public class Virde {

    private static Virde INSTANCE = null;
    private static OnApiClientResult context;

    public static final int FROM_LIST_CAMPAIGNS = 1;
    public static final int FROM_DATA_CAMPAIGN = 2;
    public static final int FROM_MODEL_CAMPAIGN = 3;

    public static Virde getInstance() {
        if (context == null)
            return null;
        else
            return INSTANCE;
    }

    public static Virde getInstance(OnApiClientResult c) {
        createInstance(c);
        return INSTANCE;
    }

    private Virde(OnApiClientResult c) {
        super();
        Virde.context = c;
    }

    private synchronized static void createInstance(OnApiClientResult c) {
        if (INSTANCE == null) {
            INSTANCE = new Virde(c);
        } else {
            Virde.context = c;
        }
    }

    public static Context getActivityContext () {
        return (Context) context;
    }

    /** Peticiones a servidor **/

    public void getListCampaigns() {
        new VirdeGetListCampaigns().execute();
    }

    public void getDataCampaign (int id) {
        new VirdeGetDataCampaign().execute(String.valueOf(id));
    }

    public void getModelCampaign (int id) {
        new VirdeGetModelCampaign().execute(String.valueOf(id));
    }

    /** AsyncTasks **/

    class VirdeGetListCampaigns extends AsyncTask<String, Void, Pair<Result, ArrayList<ItemCampaign>>> {

        @Override
        protected Pair<Result, ArrayList<ItemCampaign>> doInBackground(String... params) {
            ApiClient apiClient = ApiClient.getInstance();
            return apiClient.getListCampaigns();
        }

        @Override
        protected void onPostExecute(Pair result) {
            context.onApiClientRequestResult(result);
        }

    }

    class VirdeGetDataCampaign extends AsyncTask<String, Void, Pair<Result, ArrayList<ItemCampaign>>> {

        @Override
        protected Pair<Result, ArrayList<ItemCampaign>> doInBackground(String... params) {
            ApiClient apiClient = ApiClient.getInstance();
            return apiClient.getDataCampaign(Integer.valueOf(params[0]));
        }

        @Override
        protected void onPostExecute(Pair result) {
            context.onApiClientRequestResult(result);
        }

    }

    class VirdeGetModelCampaign extends AsyncTask<String, Void, Pair<Result, ArrayList<ItemModel>>> {

        @Override
        protected Pair<Result, ArrayList<ItemModel>> doInBackground(String... params) {
            ApiClient apiClient = ApiClient.getInstance();
            return apiClient.getModelCampaign(Integer.valueOf(params[0]));
        }

        @Override
        protected void onPostExecute(Pair result) {
            context.onApiClientRequestResult(result);
        }

    }

}
