package info.si2.iista.volunteernetworks.apiclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import info.si2.iista.bolunteernetworks.apiclient.R;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 7/9/15
 * Project: Virde
 */
public class ApiClient {

    // HOST
    private static String HOST = "";

    // URLs
    private static final String URL_SERVER_INFO = "/cindaAPI/server/info/";
    private static final String URL_CAMPAIGNS = "/cindaAPI/campaigns/list/";
    private static final String URL_DATA_CAMPAIGN = "/cindaAPI/campaign/";
    private static final String URL_MODEL_CAMPAIGN = "/cindaAPI/campaign/%s/model/";
    private static final String URL_REGISTER_USER = "/cindaAPI/volunteer/register/";
    private static final String URL_SUSCRIBE_CAMPAIGN = "/cindaAPI/campaign/%s/suscribe/";
    private static final String URL_UNSUSCRIBE_CAMPAIGN = "/cindaAPI/campaign/%s/unsuscribe/";
    private static final String URL_SEND_CONTRIBUTION = "/cindaAPI/campaign/%s/sendData/";
    private static final String URL_GET_CONTRIBUTIONS = "/cindaAPI/campaign/%s/listData/";
    private static final String URL_GET_LIST_VOLUNTEERS = "/cindaAPI/campaign/%s/listVolunteers/";
    private static final String URL_DICTIONARY = "/cindaAPI/dictionary/";
    private static final String URL_CONTRIBUTION = "/cindaAPI/contribution/";
    private static final String URL_GPX_CONTRIBUTION = "/cindaAPI/tracking/send/";
    private static final String URL_GET_USER_CONTRIBUTIONS = "/cindaAPI/volunteer/%s/listData/";
    private static final String URL_GET_USER_TRACKING = "/cindaAPI/trackings/";

    private static Context context;

    private static ApiClient INSTANCE = null;

    private ApiClient(Context c) {
        context = c;
        HOST = getActiveServer();
    }

    private synchronized static void createInstance() {
        if (INSTANCE == null) {
            Context c = Virde.getActivityContext();
            INSTANCE = new ApiClient(c);
        }
    }

    public static ApiClient getInstance() {
        createInstance();
        return INSTANCE;
    }

    public Pair<Result, ArrayList<ItemServer>> getServerInfo (String urlServer) {

        // FROM
        int from = Virde.FROM_GET_SERVER_INFO;
        String message = context.getString(R.string.errorRequest);

        ArrayList<ItemServer> result = new ArrayList<>();
        OkHttpClient client = getOkHttpClient();

        Request request = new Request.Builder()
                .url(urlServer + URL_SERVER_INFO)
                .build();

        try {

            Response response = client.newCall(request).execute();
            String respStr  = response.body().string();

            GsonBuilder gsonBuilder = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss");
            Gson gson = gsonBuilder.create();

            if (!respStr.equals("0")) {
                ItemServer item = gson.fromJson(respStr, ItemServer.class);
                item.setType(Item.SERVER);
                Collections.addAll(result, item);
            }

            return new Pair<>(new Result(false, null, from, 0), result);

        } catch (IOException e) {
            e.printStackTrace();
            return new Pair<>(new Result(true, message, from, 0), null);
        }

    }

    public Pair<Result, ArrayList<ItemCampaign>> getListCampaigns (String token) {

        int from = Virde.FROM_LIST_CAMPAIGNS; // FROM
        String message = context.getString(R.string.noCampaigns);

        ArrayList<ItemCampaign> result = new ArrayList<>();

        OkHttpClient client = getOkHttpClient();

        RequestBody formBody = new FormEncodingBuilder()
                .add("token", token)
                .build();

        Request request = new Request.Builder()
                .url(HOST + URL_CAMPAIGNS)
                .post(formBody)
                .build();

        try {

            Response response = client.newCall(request).execute();
            String respStr  = response.body().string();

            GsonBuilder gsonBuilder = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd");
            Gson gson = gsonBuilder.create();

            if (isJSONValid(respStr)) {
                ItemCampaign[] items = gson.fromJson(respStr, ItemCampaign[].class);

                for (ItemCampaign item : items)
                    item.setType(Item.CAMPAIGN);

                Collections.addAll(result, items);
                Collections.reverse(result);

                return new Pair<>(new Result(false, null, from, 0), result);
            } else {
                return new Pair<>(new Result(true, message, from, 0), null);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return new Pair<>(new Result(true, message, from, 0), null);
        }

    }

    public Pair<Result, ArrayList<ItemCampaign>> getDataCampaign (int id, String token) {

        int from = Virde.FROM_DATA_CAMPAIGN; // FROM
        String message = context.getString(R.string.noCampaign);

        ArrayList<ItemCampaign> result = new ArrayList<>();

        OkHttpClient client = getOkHttpClient();

        RequestBody formBody = new FormEncodingBuilder()
                .add("token", token)
                .build();

        Request request = new Request.Builder()
                .url(HOST + URL_DATA_CAMPAIGN + id)
                .post(formBody)
                .build();

        try {

            Response response = client.newCall(request).execute();
            String respStr  = response.body().string();

            GsonBuilder gsonBuilder = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd");
            Gson gson = gsonBuilder.create();

            if (isJSONValid(respStr)) {

                ItemCampaign item = gson.fromJson(respStr, ItemCampaign.class);
                item.setType(Item.CAMPAIGN);

                Collections.addAll(result, item);

                return new Pair<>(new Result(false, null, from, 0), result);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Pair<>(new Result(true, message, from, 0), null);

    }

    public Pair<Result, ArrayList<ItemModel>> getModelCampaign (int id) {

        int from = Virde.FROM_MODEL_CAMPAIGN; // FROM
        String message = context.getString(R.string.errorRequest);

        ArrayList<ItemModel> result = new ArrayList<>();
        OkHttpClient client = getOkHttpClient();

        Request request = new Request.Builder()
                .url(HOST + String.format(URL_MODEL_CAMPAIGN, String.valueOf(id)))
                .build();

        try {

            Response response = client.newCall(request).execute();
            String respStr  = response.body().string();

            GsonBuilder gsonBuilder = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss");
            Gson gson = gsonBuilder.create();

            if (isJSONValid(respStr) && !respStr.equals("0")) {
                ItemModel[] items = gson.fromJson(respStr, ItemModel[].class);
                Collections.addAll(result, items);
            }

            return new Pair<>(new Result(false, null, from, 0), result);

        } catch (IOException e) {
            e.printStackTrace();
            return new Pair<>(new Result(true, message, from, 0), null);
        }

    }

    public Pair<Result, ArrayList<String>> userRegister (String username, String mail, String deviceID) {

        int from = Virde.FROM_USER_REGISTER; // FROM
        String message = context.getString(R.string.userNotLogged);
        ArrayList<String> result = new ArrayList<>();
        OkHttpClient client = getOkHttpClient();

        if (HOST.equals(""))
            HOST = getActiveServer();

        RequestBody formBody = new FormEncodingBuilder()
                .add("nickname", username)
                .add("email", mail)
                .add("device_id", deviceID)
                .build();

        Request request = new Request.Builder()
                .url(HOST + URL_REGISTER_USER)
                .post(formBody)
                .build();

        try {

            Response response = client.newCall(request).execute();
            String respStr  = response.body().string();

            JSONArray array = new JSONArray(respStr);

            if (array.length() >= 2) {

                int idUser = array.getInt(0);
                String token = array.getString(1);

                result.add(String.valueOf(idUser));
                result.add(token);

            }

            return new Pair<>(new Result(false, null, from, 0), result);

        } catch (IOException e) {
            e.printStackTrace();
            return new Pair<>(new Result(true, message, from, 1), new ArrayList<String>());
        } catch (JSONException e) {
            e.printStackTrace();
            return new Pair<>(new Result(true, message, from, 1), new ArrayList<String>());
        }

    }

    public Pair<Result, ArrayList<Integer>> suscription (int idCampaign, String token, boolean suscribe) {

        int from; // FROM
        String message;
        String url;
        ArrayList<Integer> result = new ArrayList<>();
        if (suscribe) {
            from = Virde.FROM_SUSCRIBE;
            message = context.getString(R.string.suscription);
            url = (HOST + String.format(URL_SUSCRIBE_CAMPAIGN, String.valueOf(idCampaign)));
        } else {
            from = Virde.FROM_UNSUSCRIBE;
            message = context.getString(R.string.noSuscription);
            url = (HOST + String.format(URL_UNSUSCRIBE_CAMPAIGN, String.valueOf(idCampaign)));
        }

        OkHttpClient client = getOkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("token", token)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        try {

            Response response = client.newCall(request).execute();
            String respStr  = response.body().string();

            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            Integer respuesta = gson.fromJson(respStr, Integer.class);

            result.add(respuesta);
            result.add(idCampaign);

            if (respuesta == 1)
                return new Pair<>(new Result(false, null, from, 0), result);
            else
                return new Pair<>(new Result(true, message, from, 1), result);

        } catch (IOException e) {
            e.printStackTrace();
            return new Pair<>(new Result(true, message, from, 1), new ArrayList<Integer>());
        }

    }

    public Pair<Result, ArrayList<Integer>> sendContribution (ArrayList<ItemFormContribution> values) {

        int from = Virde.FROM_SEND_CONTRIBUTION; // FROM
        String message = context.getString(R.string.noContribution);
        OkHttpClient client = getOkHttpClient();

        // Id Campaign
        int idCampaign = Integer.valueOf(values.get(0).getValue());

        // Añadir valores de la campaña al formulario para enviarlos
        MultipartBuilder formEncodingBuilder = new MultipartBuilder();
        formEncodingBuilder.type(MultipartBuilder.FORM);
        for (int i=1; i<values.size(); i++) {
            ItemFormContribution item = values.get(i);
            if (!item.getKey().equals("myPosContributionNotSend"))
                if (!item.isWithImage()) {
                    formEncodingBuilder.addFormDataPart(item.getKey(), item.getValue());
                } else {
                    File image = new File(item.getValue());
                    formEncodingBuilder.addFormDataPart(item.getKey(), image.getName(),
                            RequestBody.create(MediaType.parse("image/jpg"), image));
                }
        }

        Request request = new Request.Builder()
                .url(HOST + String.format(URL_SEND_CONTRIBUTION, String.valueOf(idCampaign)))
                .post(formEncodingBuilder.build())
                .build();

        String isPosItemSync = values.get(values.size() - 1).getKey();
        String posItemSync = values.get(values.size() - 1).getValue();

        try {

            Response response = client.newCall(request).execute();
            String respStr  = response.body().string();

            if (respStr.equals("1")) {
                if (!isPosItemSync.equals("myPosContributionNotSend")) {
                    return new Pair<>(new Result(false, null, from, 0), new ArrayList<Integer>()); // codigoError reused
                } else {
                    return new Pair<>(new Result(false, null, from, Integer.valueOf(posItemSync)), new ArrayList<Integer>()); // codigoError reused
                }
            } else {
                if (!isPosItemSync.equals("myPosContributionNotSend"))
                    return new Pair<>(new Result(true, message, from, 1), new ArrayList<Integer>()); // codigoError reused
                else
                    return new Pair<>(new Result(true, message, from, Integer.valueOf(posItemSync)), new ArrayList<Integer>()); // codigoError reused
            }

        } catch (IOException e) {
            e.printStackTrace();
            if (!isPosItemSync.equals("myPosContributionNotSend"))
                return new Pair<>(new Result(true, message, from, 1), new ArrayList<Integer>()); // codigoError reused
            else
                return new Pair<>(new Result(true, message, from, Integer.valueOf(posItemSync)), new ArrayList<Integer>()); // codigoError reused
        }

    }

    public Pair<Result, ArrayList<ItemContribution>> getContributions (int id, String token) {

        int from = Virde.FROM_GET_CONTRIBUTIONS; // FROM
        String message = context.getString(R.string.noContributions);

        ArrayList<ItemContribution> result = new ArrayList<>();
        OkHttpClient client = getOkHttpClient();

        Request request;
        if (!token.equals("")) { // Contribuciones propias del usuario
            RequestBody formBody = new FormEncodingBuilder()
                    .add("token", token)
                    .build();
            request = new Request.Builder()
                    .url(HOST + String.format(URL_GET_CONTRIBUTIONS, String.valueOf(id)))
                    .post(formBody)
                    .build();
        } else { // Contribuciones propias y de los demás usuarios
            request = new Request.Builder()
                    .url(HOST + String.format(URL_GET_CONTRIBUTIONS, String.valueOf(id)))
                    .build();
        }

        try {

            Response response = client.newCall(request).execute();
            String respStr  = response.body().string();

            if (respStr.equals("0")) // Ninguna contribución
                return new Pair<>(new Result(false, null, from, 0), result);

            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();

            if (isJSONValid(respStr)) {
                ItemContribution[] items = gson.fromJson(respStr, ItemContribution[].class);
                Collections.addAll(result, items);
            }

            return new Pair<>(new Result(false, null, from, 0), result);

        } catch (IOException e) {
            e.printStackTrace();
            return new Pair<>(new Result(true, message, from, 0), null);
        }

    }

    public Pair<Result, ArrayList<ItemProfile>> getUserContributions (String token, int id) {

        int from = Virde.FROM_GET_USER_CONTRIBUTIONS; // FROM
        String message = context.getString(R.string.noContributions);

        ArrayList<ItemProfile> result = new ArrayList<>();
        OkHttpClient client = getOkHttpClient();

        RequestBody formBody = new FormEncodingBuilder()
                .add("token", token)
                .build();

        Request request = new Request.Builder()
                .url(HOST + String.format(URL_GET_USER_CONTRIBUTIONS, String.valueOf(id)))
                .post(formBody)
                .build();

        try {

            Response response = client.newCall(request).execute();
            String respStr  = response.body().string();

            GsonBuilder gsonBuilder = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss");
            Gson gson = gsonBuilder.create();

            if (isJSONValid(respStr)) {
                ItemProfile[] items = gson.fromJson(respStr, ItemProfile[].class);
                for (ItemProfile item : items)
                    item.setType(Item.PROFILE);
                Collections.addAll(result, items);
            }

            return new Pair<>(new Result(false, message, from, 0), result);

        } catch (IOException e) {
            e.printStackTrace();
            return new Pair<>(new Result(true, message, from, 1), new ArrayList<ItemProfile>());
        }

    }

    public Pair<Result, ArrayList<ItemProfileTracking>> getUserTracking (String token) {

        int from = Virde.FROM_GET_USER_TRACKING; // FROM
        String message = context.getString(R.string.noContributions);

        ArrayList<ItemProfileTracking> result = new ArrayList<>();
        OkHttpClient client = getOkHttpClient();

        RequestBody formBody = new FormEncodingBuilder()
                .add("token", token)
                .build();

        Request request = new Request.Builder()
                .url(HOST + URL_GET_USER_TRACKING)
                .post(formBody)
                .build();

        try {

            Response response = client.newCall(request).execute();
            String respStr  = response.body().string();

            GsonBuilder gsonBuilder = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss");
            Gson gson = gsonBuilder.create();

            if (isJSONValid(respStr)) {
                ItemProfileTracking[] items = gson.fromJson(respStr, ItemProfileTracking[].class);
                for (ItemProfileTracking item : items)
                    item.setType(Item.PROFILE_TRACKING);
                Collections.addAll(result, items);
            }

            return new Pair<>(new Result(false, message, from, 0), result);

        } catch (IOException e) {
            e.printStackTrace();
            return new Pair<>(new Result(true, message, from, 1), new ArrayList<ItemProfileTracking>());
        }

    }

    public Pair<Result, ArrayList<ItemUser>> getListVolunteers (int idCampaign) {

        int from = Virde.FROM_GET_LIST_VOLUNTEERS; // FROM
        ArrayList<ItemUser> result = new ArrayList<>();
        String message = context.getString(R.string.noUsers);
        OkHttpClient client = getOkHttpClient();

        Request request = new Request.Builder()
                .url(HOST + String.format(URL_GET_LIST_VOLUNTEERS, String.valueOf(idCampaign)))
                .build();

        try {

            Response response = client.newCall(request).execute();
            String respStr  = response.body().string();

            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();

            if (isJSONValid(respStr)) {
                ItemUser[] items = gson.fromJson(respStr, ItemUser[].class);
                Collections.addAll(result, items);
            }

            return new Pair<>(new Result(false, message, from, 0), result);

        } catch (IOException e) {
            e.printStackTrace();
            return new Pair<>(new Result(true, message, from, 1), new ArrayList<ItemUser>());
        }

    }

    public Pair<Result, ArrayList<String>> sendGpxContribution (ItemGpx item) {

        int from = Virde.FROM_SEND_GPX_CONTRIBUTION; // FROM
        String message = context.getString(R.string.noGpx);
        ArrayList<String> result = new ArrayList<>();
        OkHttpClient client = getOkHttpClient();
        result.add(String.valueOf(item.getId()));

        MultipartBuilder formEncodingBuilder = new MultipartBuilder();
        formEncodingBuilder.type(MultipartBuilder.FORM);

        formEncodingBuilder.addFormDataPart("id", item.getId());
        formEncodingBuilder.addFormDataPart("id_campaign", String.valueOf(item.getIdCampaign()));
        formEncodingBuilder.addFormDataPart("id_volunteer", String.valueOf(item.getIdVolunteer()));

        File gpx = new File(item.getDir());
        formEncodingBuilder.addFormDataPart("tracking", gpx.getName(),
                RequestBody.create(MediaType.parse("application/xml"), gpx));

        Request request = new Request.Builder()
                .url(HOST + URL_GPX_CONTRIBUTION)
                .post(formEncodingBuilder.build())
                .build();

        String respStr;

        try {

            Response response = client.newCall(request).execute();
            respStr = response.body().string();

        } catch (IOException e) {
            e.printStackTrace();
            return new Pair<>(new Result(true, message, from, 1), result);
        }

        if (respStr.equals("1")) {
            return new Pair<>(new Result(false, null, from, 0), result);
        } else {
            return new Pair<>(new Result(true, message, from, 1), result);
        }

    }

    public Pair<Result, ArrayList<Dictionary>> getDictionary (String idDictionary, String idServer) {

        int from = Virde.FROM_GET_DICTIONARY; // FROM
        String message = context.getString(R.string.errorRequest);

        ArrayList<Dictionary> result = new ArrayList<>();
        OkHttpClient client = getOkHttpClient();

        Request request = new Request.Builder()
                .url(HOST + URL_DICTIONARY + idDictionary)
                .build();

        try {

            Response response = client.newCall(request).execute();
            String respStr  = response.body().string();

            GsonBuilder gsonBuilder = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss");
            Gson gson = gsonBuilder.create();

            if (!respStr.equals("0")) {
                Dictionary dictionary = gson.fromJson(respStr, Dictionary.class);
                dictionary.setCode(Integer.valueOf(idDictionary));
                dictionary.setIdServer(Integer.valueOf(idServer));
                result.add(dictionary);
            }

            return new Pair<>(new Result(false, null, from, 0), result);

        } catch (IOException e) {
            e.printStackTrace();
            return new Pair<>(new Result(true, message, from, 0), null);
        }

    }

    public Pair<Result, ArrayList<ItemModelValue>> getContributionDetail (String idContribution) {

        int from = Virde.FROM_GET_CONTRIBUTION_DETAIL; // FROM
        String message = context.getString(R.string.errorRequest);

        ArrayList<ItemModelValue> result = new ArrayList<>();
        OkHttpClient client = getOkHttpClient();

        Request request = new Request.Builder()
                .url(HOST + URL_CONTRIBUTION + idContribution)
                .build();

        try {

            Response response = client.newCall(request).execute();
            String respStr  = response.body().string();

            if (!respStr.equals("0")) {

                JSONObject obj = new JSONObject(respStr);
                Iterator iterator = obj.keys();
                while(iterator.hasNext()){
                    String key = (String)iterator.next();
                    String value = obj.getString(key);
                    result.add(new ItemModelValue(key, value));
                }

            }

            return new Pair<>(new Result(false, null, from, 0), result);

        } catch (IOException e) {
            e.printStackTrace();
            return new Pair<>(new Result(true, message, from, 0), null);
        } catch (JSONException e) {
            e.printStackTrace();
            return new Pair<>(new Result(true, message, from, 0), null);
        }

    }

    /** UTIL **/
    private boolean isJSONValid(String json) {
        try {
            new JSONObject(json);
        } catch (JSONException ex) {
            // in case JSONArray is valid as well...
            try {
                new JSONArray(json);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    private static String getActiveServer () {

        SharedPreferences sharedPref = context.getSharedPreferences("userPreferences", Context.MODE_PRIVATE);
        return sharedPref.getString("serverUrl", "");

    }

    public static void setActiveServer (String newServer) {

        HOST = newServer;

    }

    private static OkHttpClient getOkHttpClient () {

        HOST = getActiveServer();
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(10, TimeUnit.SECONDS);
        client.setReadTimeout(10, TimeUnit.SECONDS);

        return client;

    }

}
