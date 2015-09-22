package info.si2.iista.volunteernetworks.database;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;

import java.util.ArrayList;

import info.si2.iista.volunteernetworks.apiclient.ItemCampaign;
import info.si2.iista.volunteernetworks.apiclient.ItemModel;
import info.si2.iista.volunteernetworks.apiclient.ItemModelValue;
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

    public void getCampaigns () {
        new DBVirdeSelectCampaigns().execute();
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
            return apiClient.getCampaigns();
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

}
