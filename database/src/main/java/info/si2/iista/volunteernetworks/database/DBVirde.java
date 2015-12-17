package info.si2.iista.volunteernetworks.database;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;

import java.util.ArrayList;

import info.si2.iista.volunteernetworks.apiclient.Dictionary;
import info.si2.iista.volunteernetworks.apiclient.ItemCampaign;
import info.si2.iista.volunteernetworks.apiclient.ItemGpx;
import info.si2.iista.volunteernetworks.apiclient.ItemModel;
import info.si2.iista.volunteernetworks.apiclient.ItemModelValue;
import info.si2.iista.volunteernetworks.apiclient.ItemServer;
import info.si2.iista.volunteernetworks.apiclient.Result;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 11/9/15
 * Project: Virde
 */
public class DBVirde {

    private static DBVirde INSTANCE = null;
    private static OnDBApiResult context;

    public static final int FROM_INSERT_CAMPAIGNS = 1;
    public static final int FROM_UPDATE_CAMPAIGN = 2;
    public static final int FROM_SELECT_CAMPAIGNS = 3;
    public static final int FROM_SELECT_CAMPAIGNS_FROM_ID = 4;
    public static final int FROM_SELECT_CAMPAIGN = 5;

    public static final int FROM_SELECT_MODEL = 6;
    public static final int FROM_INSERT_MODEL = 7;
    public static final int FROM_UPDATE_MODEL = 8;

    public static final int FROM_SELECT_MODELITEM = 9;
    public static final int FROM_INSERT_MODELITEM = 10;
    public static final int FROM_UPDATE_MODELITEM = 11;

    public static final int FROM_SELECT_SERVERS = 12;
    public static final int FROM_SELECT_ACTIVE_SERVER = 13;
    public static final int FROM_INSERT_SERVER = 14;
    public static final int FROM_DELETE_SERVER = 15;
    public static final int FROM_UPDATE_SERVER = 16;

    public static final int FROM_INSERT_GPX = 17;
    public static final int FROM_SELECT_GPX = 18;
    public static final int FROM_UPDATE_GPX = 19;

    public static final int FROM_INSERT_DICTIONARY = 20;
    public static final int FROM_SELECT_DICTIONARY = 21;
    public static final int FROM_CHECK_IF_DICTIONARY_EXISTS = 22;


    public static DBVirde getInstance() {
        if (context == null)
            return null;
        else
            return INSTANCE;
    }

    public static DBVirde getInstance(OnDBApiResult c) {
        createInstance(c);
        DBApi.createInstance((Context)c);
        return INSTANCE;
    }

    private DBVirde(OnDBApiResult c) {
        super();
        DBVirde.context = c;
    }

    private synchronized static void createInstance(OnDBApiResult c) {
        if (INSTANCE == null) {
            INSTANCE = new DBVirde(c);
        } else {
            DBVirde.context = c;
        }
    }

    /** Peticiones a servidor **/

          /** CAMPAIGN **/

    @SuppressWarnings("unchecked")
    public void addCampaigns(ArrayList<ItemCampaign> items) {
        new DBVirdeGetListCampaigns().execute(items);
    }

    public void updateCampaign(ItemCampaign item) {
        new DBVirdeUpdateCampaign().execute(item);
    }

    public void getCampaigns (int idServer) {
        new DBVirdeSelectCampaigns().execute(String.valueOf(idServer));
    }

    public void getCampaign (int id) {
        new DBVirdeSelectCampaign().execute(String.valueOf(id));
    }

    public void getCampaignsFrom (int idCampaign) {
        new DBVirdeSelectCampaignsFrom().execute(String.valueOf(idCampaign));
    }

        /** MODEL **/

    public void selectModel (int idCampaign) {
        new DBVirdeSelectModel().execute(idCampaign);
    }

    @SuppressWarnings("unchecked")
    public void insertModel(ArrayList<ItemModel> items) {
        new DBVirdeInsertModel().execute(items);
    }

    public void updateModel(ItemModel item) {
        new DBVirdeUpdateModel().execute(item);
    }

        /** MODEL VALUE **/
    public void selectModelValue (int idModelValue) {
        new DBVirdeSelectModelValue().execute(String.valueOf(idModelValue));
    }

    @SuppressWarnings("unchecked")
    public void insertModelValue(ArrayList<ItemModelValue> items) {
        new DBVirdeInsertModelValue().execute(items);
    }

    @SuppressWarnings("unchecked")
    public void updateModelValue(ArrayList<ItemModelValue> items) {
        new DBVirdeUpdateModelValue().execute(items);
    }

        /** SERVER **/
    public void selectServers () {
        new DBVirdeSelectServers().execute();
    }

    public void selectActiveServer () {
        new DBVirdeSelectActiveServer().execute();
    }

    public void insertServer (ItemServer item) {
        new DBVirdeInsertServer().execute(item);
    }

    public void deleteServer (int id) {
        new DBVirdeDeleteServer().execute(id);
    }

    public void updateServer(ItemServer item) {
        new DBVirdeUpdateServer().execute(item);
    }

        /** GPX **/

    public void selectGpxs (int idServer, int idCampaign) {
        new DBVirdeSelectGpx().execute(idServer, idCampaign);
    }

    public void insertGpx (ItemGpx item) {
        new DBVirdeInsertGpx().execute(item);
    }

    public void updateGpx (ItemGpx item) {
        new DBVirdeUpdateGpx().execute(item);
    }

        /** Dictionary **/

    public void checkIfDictionaryExists (int code) {
        new DBVirdeCheckDictionary().execute(code);
    }

    public void insertDictionary (Dictionary dictionary) {
        new DBVirdeInsertDictionary().execute(dictionary);
    }

    public void selectDictionary (int code) {
        new DBVirdeSelectDictionary().execute(code);
    }

    /** AsyncTasks **/

        /** CAMPAIGN **/

    class DBVirdeGetListCampaigns extends AsyncTask<ArrayList<ItemCampaign>, Void, Result> {

        @Override
        protected Result doInBackground(ArrayList<ItemCampaign>... arrayLists) {
            DBApi apiClient = DBApi.getInstance((Context) context);
            return apiClient.insertCampaignsToDB(arrayLists[0]);
        }

        @Override
        protected void onPostExecute(Result result) {
            context.onDBApiInsertResult(result);
        }

    }

    class DBVirdeUpdateCampaign extends AsyncTask<ItemCampaign, Void, Result> {

        @Override
        protected Result doInBackground(ItemCampaign... itemCampaigns) {
            DBApi apiClient = DBApi.getInstance((Context) context);
            return apiClient.updateCampaign(itemCampaigns[0]);
        }

        @Override
        protected void onPostExecute(Result result) {
            context.onDBApiUpdateResult(result);
        }

    }

    class DBVirdeSelectCampaigns extends AsyncTask<String, Void, Pair<Result, ArrayList<ItemCampaign>>> {

        @Override
        protected Pair<Result, ArrayList<ItemCampaign>> doInBackground(String... strings) {
            DBApi apiClient = DBApi.getInstance((Context) context);
            return apiClient.getCampaigns(Integer.valueOf(strings[0]));
        }

        @Override
        protected void onPostExecute(Pair result) {
            context.onDBApiSelectResult(result);
        }

    }

    class DBVirdeSelectCampaign extends AsyncTask<String, Void, Pair<Result, ArrayList<ItemCampaign>>> {

        @Override
        protected Pair<Result, ArrayList<ItemCampaign>> doInBackground(String... strings) {
            DBApi apiClient = DBApi.getInstance((Context) context);
            return apiClient.getCampaign(Integer.valueOf(strings[0]));
        }

        @Override
        protected void onPostExecute(Pair result) {
            context.onDBApiSelectResult(result);
        }

    }

    class DBVirdeSelectCampaignsFrom extends AsyncTask<String, Void, Pair<Result, ArrayList<ItemCampaign>>> {

        @Override
        protected Pair<Result, ArrayList<ItemCampaign>> doInBackground(String... strings) {
            DBApi apiClient = DBApi.getInstance((Context) context);
            return apiClient.getCampaignsFromID(Integer.valueOf(strings[0]));
        }

        @Override
        protected void onPostExecute(Pair result) {
            context.onDBApiSelectResult(result);
        }

    }

        /** MODEL **/

    class DBVirdeSelectModel extends AsyncTask<Integer, Void, Pair<Result, ArrayList<ItemModel>>> {

        @Override
        protected Pair<Result, ArrayList<ItemModel>> doInBackground(Integer... integers) {
            DBApi apiClient = DBApi.getInstance((Context) context);
            return apiClient.getModel(integers[0]);
        }

        @Override
        protected void onPostExecute(Pair result) {
            context.onDBApiSelectResult(result);
        }

    }

    class DBVirdeInsertModel extends AsyncTask<ArrayList<ItemModel>, Void, Result> {

        @Override
        protected Result doInBackground(ArrayList<ItemModel>... arrayLists) {
            DBApi apiClient = DBApi.getInstance((Context) context);
            return apiClient.insertModelToDB(arrayLists[0]);
        }

        @Override
        protected void onPostExecute(Result result) {
            context.onDBApiInsertResult(result);
        }

    }

    class DBVirdeUpdateModel extends AsyncTask<ItemModel, Void, Result> {

        @Override
        protected Result doInBackground(ItemModel... ItemModels) {
            DBApi apiClient = DBApi.getInstance((Context) context);
            return apiClient.updateModel(ItemModels[0]);
        }

        @Override
        protected void onPostExecute(Result result) {
            context.onDBApiUpdateResult(result);
        }

    }

        /** MODEL VALUE **/

    class DBVirdeSelectModelValue extends AsyncTask<String, Void, Pair<Result, ArrayList<ItemModelValue>>> {

        @Override
        protected Pair<Result, ArrayList<ItemModelValue>> doInBackground(String... strings) {
            DBApi apiClient = DBApi.getInstance((Context) context);
            return apiClient.selectModelValues(Integer.valueOf(strings[0]));
        }

        @Override
        protected void onPostExecute(Pair result) {
            context.onDBApiSelectResult(result);
        }

    }

    class DBVirdeInsertModelValue extends AsyncTask<ArrayList<ItemModelValue>, Void, Result> {

        @Override
        protected Result doInBackground(ArrayList<ItemModelValue>... arrayLists) {
            DBApi apiClient = DBApi.getInstance((Context) context);
            return apiClient.insertModelValueToDB(arrayLists[0]);
        }

        @Override
        protected void onPostExecute(Result result) {
            context.onDBApiInsertResult(result);
        }

    }

    class DBVirdeUpdateModelValue extends AsyncTask<ArrayList<ItemModelValue>, Void, Result> {

        @Override
        protected Result doInBackground(ArrayList<ItemModelValue>... ItemModels) {
            DBApi apiClient = DBApi.getInstance((Context) context);
            return apiClient.updateModelValue(ItemModels[0]);
        }

        @Override
        protected void onPostExecute(Result result) {
            context.onDBApiUpdateResult(result);
        }

    }

        /** SERVER **/

    class DBVirdeSelectServers extends AsyncTask<String, Void, Pair<Result, ArrayList<ItemServer>>> {

        @Override
        protected Pair<Result, ArrayList<ItemServer>> doInBackground(String... strings) {
            DBApi apiClient = DBApi.getInstance((Context) context);
            return apiClient.selectServers();
        }

        @Override
        protected void onPostExecute(Pair result) {
            context.onDBApiSelectResult(result);
        }

    }

    class DBVirdeSelectActiveServer extends AsyncTask<String, Void, Pair<Result, ArrayList<ItemServer>>> {

        @Override
        protected Pair<Result, ArrayList<ItemServer>> doInBackground(String... strings) {
            DBApi apiClient = DBApi.getInstance((Context) context);
            return apiClient.selectActiveServer();
        }

        @Override
        protected void onPostExecute(Pair result) {
            context.onDBApiSelectResult(result);
        }

    }

    class DBVirdeInsertServer extends AsyncTask<ItemServer, Void, Result> {

        @Override
        protected Result doInBackground(ItemServer... items) {
            DBApi apiClient = DBApi.getInstance((Context) context);
            return apiClient.insertServerToDB(items[0]);
        }

        @Override
        protected void onPostExecute(Result result) {
            context.onDBApiInsertResult(result);
        }

    }

    class DBVirdeDeleteServer extends AsyncTask<Integer, Void, Result> {

        @Override
        protected Result doInBackground(Integer... params) {
            DBApi apiClient = DBApi.getInstance((Context) context);
            return apiClient.deleteServerFromDB(params[0]);
        }

        @Override
        protected void onPostExecute(Result result) {
            context.onDBApiUpdateResult(result);
        }

    }

    class DBVirdeUpdateServer extends AsyncTask<ItemServer, Void, Result> {

        @Override
        protected Result doInBackground(ItemServer... items) {
            DBApi apiClient = DBApi.getInstance((Context) context);
            return apiClient.updateServer(items[0]);
        }

        @Override
        protected void onPostExecute(Result result) {
            context.onDBApiUpdateResult(result);
        }

    }

        /** GPX **/

    class DBVirdeSelectGpx extends AsyncTask<Integer, Void, Pair<Result, ArrayList<ItemGpx>>> {

        @Override
        protected Pair<Result, ArrayList<ItemGpx>> doInBackground(Integer... integers) {
            DBApi apiClient = DBApi.getInstance((Context) context);
            return apiClient.selectGpxs(integers[0], integers[1]);
        }

        @Override
        protected void onPostExecute(Pair result) {
            context.onDBApiSelectResult(result);
        }

    }

    class DBVirdeInsertGpx extends AsyncTask<ItemGpx, Void, Result> {

        @Override
        protected Result doInBackground(ItemGpx... items) {
            DBApi apiClient = DBApi.getInstance((Context) context);
            return apiClient.insertGpxToDB(items[0]);
        }

        @Override
        protected void onPostExecute(Result result) {
            context.onDBApiInsertResult(result);
        }

    }

    class DBVirdeUpdateGpx extends AsyncTask<ItemGpx, Void, Result> {

        @Override
        protected Result doInBackground(ItemGpx... items) {
            DBApi apiClient = DBApi.getInstance((Context) context);
            return apiClient.updateGPX(items[0]);
        }

        @Override
        protected void onPostExecute(Result result) {
            context.onDBApiUpdateResult(result);
        }

    }

        /** DICTIONARY **/

    class DBVirdeSelectDictionary extends AsyncTask<Integer, Void, Pair<Result, ArrayList<Dictionary>>> {

        @Override
        protected Pair<Result, ArrayList<Dictionary>> doInBackground(Integer... integers) {
            DBApi apiClient = DBApi.getInstance((Context) context);
            return apiClient.selectDictionary(integers[0]);
        }

        @Override
        protected void onPostExecute(Pair result) {
            context.onDBApiSelectResult(result);
        }

    }

    class DBVirdeInsertDictionary extends AsyncTask<Dictionary, Void, Result> {

        @Override
        protected Result doInBackground(Dictionary... items) {
            DBApi apiClient = DBApi.getInstance((Context) context);
            return apiClient.insertDictionaryToDB(items[0]);
        }

        @Override
        protected void onPostExecute(Result result) {
            context.onDBApiInsertResult(result);
        }

    }

    class DBVirdeCheckDictionary extends AsyncTask<Integer, Void, Pair<Result, ArrayList<Integer>>> {

        @Override
        protected Pair<Result, ArrayList<Integer>> doInBackground(Integer... integers) {
            DBApi apiClient = DBApi.getInstance((Context) context);
            return apiClient.checkIfDictionaryExists(integers[0]);
        }

        @Override
        protected void onPostExecute(Pair result) {
            context.onDBApiSelectResult(result);
        }

    }

}
