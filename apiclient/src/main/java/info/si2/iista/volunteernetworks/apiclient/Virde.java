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
    public static final int FROM_USER_REGISTER = 4;
    public static final int FROM_SUSCRIBE = 5;
    public static final int FROM_UNSUSCRIBE = 6;
    public static final int FROM_SEND_CONTRIBUTION = 7;
    public static final int FROM_GET_CONTRIBUTIONS = 8;
    public static final int FROM_GET_LIST_VOLUNTEERS = 9;
    public static final int FROM_GET_SERVER_INFO = 10;
    public static final int FROM_GET_DICTIONARY = 11;
    public static final int FROM_GET_CONTRIBUTION_DETAIL = 12;
    public static final int FROM_SEND_GPX_CONTRIBUTION = 13;
    public static final int FROM_GET_USER_CONTRIBUTIONS = 14;
    public static final int FROM_GET_USER_TRACKING = 15;

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
    public void getServerInfo(String urlServer) {
        new VirdeGetServerInfo().execute(urlServer);
    }


    public void getListCampaigns(String token) {
        new VirdeGetListCampaigns().execute(token);
    }

    public void getDataCampaign (int id, String token) {
        new VirdeGetDataCampaign().execute(String.valueOf(id), token);
    }

    public void getModelCampaign (int id) {
        new VirdeGetModelCampaign().execute(String.valueOf(id));
    }

    public void userRegister (String username, String mail, String deviceID) {
        new VirdeUserRegister().execute(username, mail, deviceID);
    }

    public void suscription (int idCampaign, String token, boolean suscribe) {
        new VirdeSuscriptionCampaign().execute(String.valueOf(idCampaign), token, String.valueOf(suscribe));
    }

    @SuppressWarnings("unchecked")
    public void sendContribution (ArrayList<ItemFormContribution> values) {
        new VirdeSendContribution().execute(values);
    }

    public void getContributions (int id, String token) {
        new VirdeGetContributions().execute(String.valueOf(id), token);
    }

    public void getListVolunteers (int idCampaign) {
        new VirdeGetListVolunteers().execute(String.valueOf(idCampaign));
    }

    public void setActiveServer (String newServer) {
        ApiClient.setActiveServer(newServer);
    }

    public void sendGpxContribution (ItemGpx item) {
        new VirdeSendGpx().execute(item);
    }

    public void getDictionary(int idDictionary, int idServer) {
        new VirdeGetDictionary().execute(String.valueOf(idDictionary), String.valueOf(idServer));
    }

    public void getContributionDetail(int idContribution) {
        new VirdeGetContributionDetail().execute(String.valueOf(idContribution));
    }

    public void getUserContributions (String token, int idUser) {
        new VirdeGetUserContributions().execute(token, String.valueOf(idUser));
    }

    public void getUserTracking (String token) {
        new VirdeGetUserTracking().execute(token);
    }

    /** AsyncTasks **/

    class VirdeGetServerInfo extends AsyncTask<String, Void, Pair<Result, ArrayList<ItemServer>>> {

        @Override
        protected Pair<Result, ArrayList<ItemServer>> doInBackground(String... params) {
            ApiClient apiClient = ApiClient.getInstance();
            return apiClient.getServerInfo(params[0]);
        }

        @Override
        protected void onPostExecute(Pair result) {
            context.onApiClientRequestResult(result);
        }

    }

    class VirdeGetListCampaigns extends AsyncTask<String, Void, Pair<Result, ArrayList<ItemCampaign>>> {

        @Override
        protected Pair<Result, ArrayList<ItemCampaign>> doInBackground(String... params) {
            ApiClient apiClient = ApiClient.getInstance();
            return apiClient.getListCampaigns(params[0]);
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
            return apiClient.getDataCampaign(Integer.valueOf(params[0]), params[1]);
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

    class VirdeUserRegister extends AsyncTask<String, Void, Pair<Result, ArrayList<String>>> {

        @Override
        protected Pair<Result, ArrayList<String>> doInBackground(String... params) {
            ApiClient apiClient = ApiClient.getInstance();
            return apiClient.userRegister(params[0], params[1], params[2]);
        }

        @Override
        protected void onPostExecute(Pair result) {
            context.onApiClientRequestResult(result);
        }

    }

    class VirdeSuscriptionCampaign extends AsyncTask<String, Void, Pair<Result, ArrayList<Integer>>> {

        @Override
        protected Pair<Result, ArrayList<Integer>> doInBackground(String... params) {
            ApiClient apiClient = ApiClient.getInstance();
            return apiClient.suscription(Integer.valueOf(params[0]), params[1], Boolean.valueOf(params[2]));
        }

        @Override
        protected void onPostExecute(Pair result) {
            context.onApiClientRequestResult(result);
        }

    }

    class VirdeSendContribution extends AsyncTask<ArrayList<ItemFormContribution>, Void, Pair<Result, ArrayList<Integer>>> {

        @SafeVarargs
        @Override
        protected final Pair<Result, ArrayList<Integer>> doInBackground(ArrayList<ItemFormContribution>... params) {
            ApiClient apiClient = ApiClient.getInstance();
            return apiClient.sendContribution(params[0]);
        }

        @Override
        protected void onPostExecute(Pair result) {
            context.onApiClientRequestResult(result);
        }

    }

    class VirdeGetContributions extends AsyncTask<String, Void, Pair<Result, ArrayList<ItemContribution>>> {

        @SafeVarargs
        @Override
        protected final Pair<Result, ArrayList<ItemContribution>> doInBackground(String... params) {
            ApiClient apiClient = ApiClient.getInstance();
            return apiClient.getContributions(Integer.valueOf(params[0]), params[1]);
        }

        @Override
        protected void onPostExecute(Pair result) {
            context.onApiClientRequestResult(result);
        }

    }

    class VirdeGetListVolunteers extends AsyncTask<String, Void, Pair<Result, ArrayList<ItemUser>>> {

        @Override
        protected Pair<Result, ArrayList<ItemUser>> doInBackground(String... params) {
            ApiClient apiClient = ApiClient.getInstance();
            return apiClient.getListVolunteers(Integer.valueOf(params[0]));
        }

        @Override
        protected void onPostExecute(Pair result) {
            context.onApiClientRequestResult(result);
        }

    }

    class VirdeSendGpx extends AsyncTask<ItemGpx, Void, Pair<Result, ArrayList<String>>> {

        @Override
        protected Pair<Result, ArrayList<String>> doInBackground(ItemGpx... params) {
            ApiClient apiClient = ApiClient.getInstance();
            return apiClient.sendGpxContribution(params[0]);
        }

        @Override
        protected void onPostExecute(Pair result) {
            context.onApiClientRequestResult(result);
        }

    }

    class VirdeGetDictionary extends AsyncTask<String, Void, Pair<Result, ArrayList<Dictionary>>> {

        @Override
        protected Pair<Result, ArrayList<Dictionary>> doInBackground(String... params) {
            ApiClient apiClient = ApiClient.getInstance();
            return apiClient.getDictionary(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(Pair result) {
            context.onApiClientRequestResult(result);
        }

    }

    class VirdeGetContributionDetail extends AsyncTask<String, Void, Pair<Result, ArrayList<ItemModelValue>>> {

        @Override
        protected Pair<Result, ArrayList<ItemModelValue>> doInBackground(String... params) {
            ApiClient apiClient = ApiClient.getInstance();
            return apiClient.getContributionDetail(params[0]);
        }

        @Override
        protected void onPostExecute(Pair result) {
            context.onApiClientRequestResult(result);
        }

    }

    class VirdeGetUserContributions extends AsyncTask<String, Void, Pair<Result, ArrayList<ItemProfile>>> {

        @Override
        protected Pair<Result, ArrayList<ItemProfile>> doInBackground(String... params) {
            ApiClient apiClient = ApiClient.getInstance();
            return apiClient.getUserContributions(params[0], Integer.valueOf(params[1]));
        }

        @Override
        protected void onPostExecute(Pair result) {
            context.onApiClientRequestResult(result);
        }

    }

    class VirdeGetUserTracking extends AsyncTask<String, Void, Pair<Result, ArrayList<ItemProfileTracking>>> {

        @Override
        protected Pair<Result, ArrayList<ItemProfileTracking>> doInBackground(String... params) {
            ApiClient apiClient = ApiClient.getInstance();
            return apiClient.getUserTracking(params[0]);
        }

        @Override
        protected void onPostExecute(Pair result) {
            context.onApiClientRequestResult(result);
        }

    }

}
