package info.si2.iista.volunteernetworks.database;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;

import java.util.ArrayList;

import info.si2.iista.volunteernetworks.apiclient.ItemCampaign;
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

    public void getCampaignsFrom (int idCampaign) {
        new DBVirdeSelectCampaignsFrom().execute(String.valueOf(idCampaign));
    }

    /** AsyncTasks **/

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

}
