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
import java.util.concurrent.TimeUnit;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 7/9/15
 * Project: Virde
 */
public class ApiClient {

    // HOST
    private static String HOST = "";

    // URLs
    private static final String URL_CAMPAIGNS = "API/campaigns/list/";
    private static final String URL_DATA_CAMPAIGN = "API/campaign/";
    private static final String URL_MODEL_CAMPAIGN = "API/campaign/%s/model/";
    private static final String URL_REGISTER_USER = "API/volunteer/register/";
    private static final String URL_SUSCRIBE_CAMPAIGN = "API/campaign/%s/suscribe/";
    private static final String URL_UNSUSCRIBE_CAMPAIGN = "API/campaign/%s/unsuscribe/";
    private static final String URL_SEND_CONTRIBUTION = "API/campaign/%s/sendData/";
    private static final String URL_GET_CONTRIBUTIONS = "API/campaign/%s/listData/";
    private static final String URL_GET_LIST_VOLUNTEERS = "API/campaign/%s/listVolunteers/";

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

    public Pair<Result, ArrayList<ItemCampaign>> getListCampaigns (String token) {

        // FROM
        int from = Virde.FROM_LIST_CAMPAIGNS;
        String message = "No se pudieron obtener las campañas, inténtelo más tarde";

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

        // FROM
        int from = Virde.FROM_DATA_CAMPAIGN;
        String message = "No se pudo obtener la campaña, inténtelo más tarde";

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

            if (!respStr.equals("0")) {
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

        int from = Virde.FROM_USER_REGISTER;
        String message = "Intente acceder más tarde";
        ArrayList<String> result = new ArrayList<>();
        OkHttpClient client = getOkHttpClient();

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

            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            String token = gson.fromJson(respStr, String.class);

            result.add(token);

            return new Pair<>(new Result(false, null, from, 0), result);

        } catch (IOException e) {
            e.printStackTrace();
            return new Pair<>(new Result(true, message, from, 1), new ArrayList<String>());
        }

    }

    public Pair<Result, ArrayList<Integer>> suscription (int idCampaign, String token, boolean suscribe) {

        int from;
        String message;
        String url;
        ArrayList<Integer> result = new ArrayList<>();
        if (suscribe) {
            from = Virde.FROM_SUSCRIBE;
            message = "Intente suscribirse más tarde";
            url = (HOST + String.format(URL_SUSCRIBE_CAMPAIGN, String.valueOf(idCampaign)));
        } else {
            from = Virde.FROM_UNSUSCRIBE;
            message = "Intente desuscribirse más tarde";
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

        int from = Virde.FROM_SEND_CONTRIBUTION;
        String message = "Intente enviar la contribución más tarde";
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

        // FROM
        int from = Virde.FROM_GET_CONTRIBUTIONS;
        String message = "Contribuciones no disponibles";

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

    public Pair<Result, ArrayList<ItemUser>> getListVolunteers (int idCampaign) {

        int from = Virde.FROM_GET_LIST_VOLUNTEERS;
        ArrayList<ItemUser> result = new ArrayList<>();
        String message = "Lista de usuarios no disponible";
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
        return sharedPref.getString("server", "");

    }

    private static OkHttpClient getOkHttpClient () {

        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(15, TimeUnit.SECONDS);
        client.setReadTimeout(15, TimeUnit.SECONDS);

        return client;

    }

}
