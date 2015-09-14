package info.si2.iista.volunteernetworks.apiclient;

import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 7/9/15
 * Project: Virde
 */
public class ApiClient {

    // HOST
    private static final String HOST = "http://virde.dev.si2soluciones.es/";

    // URLs
    private static final String URL_CAMPAIGNS = HOST + "API/campaigns/list/";
    private static final String URL_DATA_CAMPAIGN = HOST + "API/campaign/";
    private static final String URL_MODEL_CAMPAIGN = HOST + "API/campaign/%s/model/";



    private static ApiClient INSTANCE = null;

    private ApiClient() {
    }

    private synchronized static void createInstance() {
        if (INSTANCE == null)
            INSTANCE = new ApiClient();
    }

    public static ApiClient getInstance() {
        createInstance();
        return INSTANCE;
    }

    public Pair<Result, ArrayList<ItemCampaign>> getListCampaigns () {

        // FROM
        int from = Virde.FROM_LIST_CAMPAIGNS;
        String message = "No se pudieron obtener las campañas, inténtelo más tarde";

        ArrayList<ItemCampaign> result = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(URL_CAMPAIGNS)
                .build();

        try {

            Response response = client.newCall(request).execute();
            String respStr  = response.body().string();

            GsonBuilder gsonBuilder = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd");
            Gson gson = gsonBuilder.create();

            ItemCampaign[] items = gson.fromJson(respStr, ItemCampaign[].class);

            for (ItemCampaign item : items)
                item.setType(Item.CAMPAIGN);

            Collections.addAll(result, items);

            return new Pair<>(new Result(false, null, from, 0), result);

        } catch (IOException e) {
            e.printStackTrace();
            return new Pair<>(new Result(true, message, from, 0), null);
        }

    }

    public Pair<Result, ArrayList<ItemCampaign>> getDataCampaign (int id) {

        // FROM
        int from = Virde.FROM_DATA_CAMPAIGN;
        String message = "No se pudo obtener la campaña, inténtelo más tarde";

        ArrayList<ItemCampaign> result = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(URL_DATA_CAMPAIGN + id)
                .build();

        try {

            Response response = client.newCall(request).execute();
            String respStr  = response.body().string();

            GsonBuilder gsonBuilder = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd");
            Gson gson = gsonBuilder.create();

            ItemCampaign item = gson.fromJson(respStr, ItemCampaign.class);
            item.setType(Item.CAMPAIGN);

            Collections.addAll(result, item);

            return new Pair<>(new Result(false, null, from, 0), result);

        } catch (IOException e) {
            e.printStackTrace();
            return new Pair<>(new Result(true, message, from, 0), null);
        }

    }

    public Pair<Result, ArrayList<ItemModel>> getModelCampaign (int id) {

        // FROM
        int from = Virde.FROM_MODEL_CAMPAIGN;
        String message = "Inténtelo más tarde";

        ArrayList<ItemModel> result = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(String.format(URL_MODEL_CAMPAIGN, String.valueOf(id)))
                .build();

        try {

            Response response = client.newCall(request).execute();
            String respStr  = response.body().string();

            GsonBuilder gsonBuilder = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss");
            Gson gson = gsonBuilder.create();

            ItemModel[] items = gson.fromJson(respStr, ItemModel[].class);
            Collections.addAll(result, items);

            return new Pair<>(new Result(false, null, from, 0), result);

        } catch (IOException e) {
            e.printStackTrace();
            return new Pair<>(new Result(true, message, from, 0), null);
        }

    }

}
