package info.si2.iista.volunteernetworks.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.DateFormat;
import android.util.Pair;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import info.si2.iista.volunteernetworks.apiclient.Dictionary;
import info.si2.iista.volunteernetworks.apiclient.Item;
import info.si2.iista.volunteernetworks.apiclient.ItemCampaign;
import info.si2.iista.volunteernetworks.apiclient.ItemDictionary;
import info.si2.iista.volunteernetworks.apiclient.ItemGoogleMaps;
import info.si2.iista.volunteernetworks.apiclient.ItemGpx;
import info.si2.iista.volunteernetworks.apiclient.ItemModel;
import info.si2.iista.volunteernetworks.apiclient.ItemModelValue;
import info.si2.iista.volunteernetworks.apiclient.ItemParse;
import info.si2.iista.volunteernetworks.apiclient.ItemServer;
import info.si2.iista.volunteernetworks.apiclient.ItemSync;
import info.si2.iista.volunteernetworks.apiclient.Result;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 11/9/15
 * Project: Virde
 */
public class DBApi {

    // Database fields
    private static SQLiteDatabase database;
    private static DataBase dbHelper;
    private static DBApi INSTANCE;
    private static Context context;

    public synchronized static void createInstance (Context c) {
        if (dbHelper == null) {
            context = c;
            INSTANCE = new DBApi(c);
        }
    }

    public static DBApi getInstance(Context c) {
        createInstance(c);
        return INSTANCE;
    }

    /** BD **/

    private DBApi (Context context) {
        dbHelper = new DataBase(context);
    }

    public void open() throws SQLException {
        if (database == null)
            database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
        database = null;
    }

    public synchronized Result insertCampaignsToDB (ArrayList<ItemCampaign> items) {

        int from = DBVirde.FROM_INSERT_CAMPAIGNS;

        // Data actual server
        int idServer = getIdActiveServer();
        String urlServer = getActiveServer();

        try {

            int nRows = items.size();

            for (int i = 0; i < nRows; i++) {

                // Comprobar si la campaña existe mediante su ID
                String sql = "SELECT * FROM " + DBCampaign.TABLE + " " +
                        "WHERE " + DBCampaign.ID + " = '" + items.get(i).getId() + "' " +
                        "AND " + DBCampaign.ID_SERVER + " = " + String.valueOf(idServer);

                open(); // Open DB
                Cursor c = database.rawQuery(sql, null);

                if (c.getCount() == 0) { // Si no existe la campaña, INSERT

                    ItemCampaign item = items.get(i);

                    String ecodedTitle = URLEncoder.encode(item.getTitle(), "UTF-8");

                    // Short description
                    String encodedShortDesc = "";
                    if (item.getShortDescription() != null)
                        encodedShortDesc = URLEncoder.encode(item.getShortDescription(), "UTF-8");

                    // Description
                    String encodedDesc = "";
                    if (item.getDescription() != null)
                        encodedDesc = URLEncoder.encode(item.getDescription(), "UTF-8");

                    // Scope
                    String econdedScope = "";
                    if (item.getScope() != null)
                        econdedScope = URLEncoder.encode(item.getScope(), "UTF-8");



                    sql = "INSERT OR REPLACE INTO " + DBCampaign.TABLE + " " +
                            "VALUES (" + item.getId() + "," + item.getIdServer() + "," + item.getType() + ",'" +
                            item.getHeaderColor() + "','" + ecodedTitle + "','" + encodedShortDesc + "','" +
                            encodedDesc + "','" + econdedScope + "','" + item.getImage() + "','" + item.getCover() + "','" +
                            item.isSuscribe() + "','" + dateToString(item.getDateStart()) + "','" + dateToString(item.getDateEnd()) + "','" +
                            urlServer + "','" + true + "','" + item.haveTracking() + "','" + item.getStringGeoposition() + "')";

                    database.execSQL(sql);

                } else { // Si existe la campaña, UPDATE
                    updateCampaign(items.get(i));
                }

                c.close();

            }

            // Not active campaigns
//            disableCampaigns(items);

            close(); // Close DB

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new Result(true, null, from, 0);
        }

        return new Result(false, null, from, 0);

    }

    public synchronized Result updateCampaign (ItemCampaign item) {

        int from = DBVirde.FROM_UPDATE_CAMPAIGN;

        try {

            open(); // Open DB

            // Comprobar si la campaña existe mediante su ID
            String sql = "SELECT * FROM " + DBCampaign.TABLE + " " +
                        "WHERE " + DBCampaign.ID + " = '" + item.getId() + "'";

            Cursor c = database.rawQuery(sql, null);

            if (c.getCount() == 1) { // Si no existe la campaña, añadir

                String ecodedTitle = URLEncoder.encode(item.getTitle(), "UTF-8");

                // Short description
                String encodedShortDesc = "";
                if (item.getShortDescription() != null)
                    encodedShortDesc = URLEncoder.encode(item.getShortDescription(), "UTF-8");

                // Description
                String encodedDesc = "";
                if (item.getDescription() != null)
                    encodedDesc = URLEncoder.encode(item.getDescription(), "UTF-8");

                // Scope
                String econdedScope = "";
                if (item.getScope() != null)
                    econdedScope = URLEncoder.encode(item.getScope(), "UTF-8");

                sql = "UPDATE " + DBCampaign.TABLE + " " +
                        "SET " + DBCampaign.ID + "=" + item.getId() + "," +
                        DBCampaign.TYPE + "=" + item.getType() + "," +
                        DBCampaign.COLOR + "='" + item.getHeaderColor() + "'," +
                        DBCampaign.TITLE + "='" + ecodedTitle + "'," +
                        DBCampaign.SHORT_DESCRIPTION + "='" + encodedShortDesc + "'," +
                        DBCampaign.DESCRIPTION + "='" + encodedDesc + "'," +
                        DBCampaign.SCOPE + "='" + econdedScope + "'," +
                        DBCampaign.IMAGE + "='" + item.getImage() + "'," +
                        DBCampaign.COVER + "='" + item.getCover() + "'," +
                        DBCampaign.IS_SUSCRIBE + "='" + item.isSuscribe() + "'," +
                        DBCampaign.DATE_START + "='" + dateToString(item.getDateStart()) + "'," +
                        DBCampaign.DATE_END + "='" + dateToString(item.getDateEnd()) + "', " +
                        DBCampaign.HAVE_TRACKING + "='" + item.haveTracking() + "'," +
                        DBCampaign.GEOPOSITION + "='" + item.getStringGeoposition() + "' " +
                        "WHERE " + DBCampaign.ID + "=" + item.getId();

                database.execSQL(sql);
            }

            c.close();
            close(); // Close DB

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new Result(true, null, from, 0);
        }

        return new Result(false, null, from, 0);

    }

    public synchronized Pair<Result, ArrayList<ItemCampaign>> getCampaigns (int idServer) {

        int from = DBVirde.FROM_SELECT_CAMPAIGNS;
        ArrayList<ItemCampaign> result = new ArrayList<>();
        String sql = "SELECT * FROM " + DBCampaign.TABLE +
                    " WHERE " + DBCampaign.IS_ACTIVE + "='true'" +
                    " AND " + DBCampaign.ID_SERVER + "=" + idServer;

        open();

        Cursor c = database.rawQuery(sql, null);

        if (c.moveToFirst()) {

            do {

                result.add(formatItemCampaignFromDB(c));

            } while (c.moveToNext());

        }

        c.close();
        close();

        return new Pair<>(new Result(false, null, from, 0), result);

    }

    public synchronized Pair<Result, ArrayList<ItemCampaign>> getCampaign (int id) {

        int from = DBVirde.FROM_SELECT_CAMPAIGN;
        ArrayList<ItemCampaign> result = new ArrayList<>();
        String sql = "SELECT * FROM " + DBCampaign.TABLE +
                    " WHERE " + DBCampaign.ID + "=" + id;
        open();

        Cursor c = database.rawQuery(sql, null);

        if (c.moveToFirst()) {

            do {

                result.add(formatItemCampaignFromDB(c));

            } while (c.moveToNext());

        }

        c.close();
        close();

        return new Pair<>(new Result(false, null, from, 0), result);

    }

    public synchronized Pair<Result, ArrayList<ItemCampaign>> getCampaignsFromID (int idServer, int idCampaign) {

        int from = DBVirde.FROM_SELECT_CAMPAIGNS_FROM_ID;
        ArrayList<ItemCampaign> result = new ArrayList<>();
        String sql = "SELECT * FROM " + DBCampaign.TABLE + " " +
                     "WHERE " + DBCampaign.ID + ">" + String.valueOf(idCampaign) + " " +
                     "AND " + DBCampaign.ID_SERVER + "=" + String.valueOf(idServer) + " " +
                     "ORDER BY " + DBCampaign.ID + " ASC";

        open();

        Cursor c = database.rawQuery(sql, null);

        if (c.moveToFirst()) {

            do {

                result.add(formatItemCampaignFromDB(c));

            } while (c.moveToNext());

        }

        c.close();
        close();

        return new Pair<>(new Result(false, null, from, 0), result);

    }

    /**
     * Desactiva las campañas que ya no están activas
     * @param items Campañas que si que estan activas para comparar con la base de datos local
     */
    private synchronized void disableCampaigns (ArrayList<ItemCampaign> items) {

        String ids = "";

        for (ItemCampaign item : items) {
            ids += String.valueOf(item.getId()) + ",";
        }
        ids = ids.substring(0, ids.length()-1);

        String sql = "UPDATE " + DBCampaign.TABLE +
                " SET " + DBCampaign.IS_ACTIVE + "='false'" +
                " WHERE " + DBCampaign.ID + " NOT IN (" + ids + ")";

        open();
        database.execSQL(sql);
        close();

    }

    private ItemCampaign formatItemCampaignFromDB(Cursor c) {

        ItemCampaign campaign = new ItemCampaign();

        try {

            campaign.setId(c.getInt(0));
            campaign.setIdServer(c.getInt(1));
            campaign.setType(c.getInt(2));
            campaign.setHeaderColor(c.getString(3));
            campaign.setTitle(URLDecoder.decode(c.getString(4), "UTF-8"));
            campaign.setShortDescription(URLDecoder.decode(c.getString(5), "UTF-8"));
            campaign.setDescription(URLDecoder.decode(c.getString(6), "UTF-8"));
            campaign.setScope(URLDecoder.decode(c.getString(7), "UTF-8"));
            campaign.setImage(c.getString(8));
            campaign.setCover(c.getString(9));
            campaign.setIsSuscribe(Boolean.valueOf(c.getString(10)));
            campaign.setDateStart(stringToDate(c.getString(11)));
            campaign.setDateEnd(stringToDate(c.getString(12)));
            // c == 13 -> url server - dato interno
            // c == 14 -> is active
            campaign.setHaveTracking(Boolean.valueOf(c.getString(15)));
            campaign.setGeoposition(c.getString(16));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return campaign;

    }

    /***********/
    /** MODEL **/
    /***********/

    public synchronized Result insertModelToDB (ArrayList<ItemModel> items) {

        int from = DBVirde.FROM_INSERT_MODEL;

        try {

            int nRows = items.size();

            for (int i = 0; i < nRows; i++) {

                // Comprobar si el modelo existe mediante su ID
                String sql = "SELECT * FROM " + DBModel.TABLE + " " +
                        "WHERE " + DBModel.ID + " = '" + items.get(i).getId() + "'";

                open(); // Open DB
                Cursor c = database.rawQuery(sql, null);

                if (c.getCount() == 0) { // Si no existe la campaña, INSERT

                    ItemModel item = items.get(i);

                    // Options
                    String options = "";
                    if (item.getFieldOptions() != null)
                        options = URLEncoder.encode(item.getFieldOptions(), "UTF-8");

                    sql = "INSERT OR REPLACE INTO " + DBModel.TABLE + " " +
                            "VALUES (" + item.getId() + "," + item.getIdCampaign() + "," + item.getFieldPosition() + ",'" +
                            item.getFieldLabel() + "','" + item.getFieldName() + "','" + item.getFieldDescription() + "','" +
                            item.getFieldType() + "','" + item.getFieldRequired() + "','" + options + "')";

                    database.execSQL(sql);

                } else { // Si existe la campaña, UPDATE
                    updateModel(items.get(i));
                }

                c.close();

            }

            close(); // Close DB

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new Result(true, null, from, 0);
        }

        return new Result(false, null, from, 0);

    }

    public synchronized Result updateModel (ItemModel item) {

        int from = DBVirde.FROM_UPDATE_MODEL;

        try {

            open(); // Open DB

            // Comprobar si la campaña existe mediante su ID
            String sql = "SELECT * FROM " + DBModel.TABLE + " " +
                         "WHERE " + DBModel.ID + " = '" + item.getId() + "'";

            Cursor c = database.rawQuery(sql, null);

            if (c.getCount() == 1) { // Si no existe la campaña, añadir

                // Scope
                String options = "";
                if (item.getFieldOptions() != null)
                    options = URLEncoder.encode(item.getFieldOptions(), "UTF-8");

                sql = "UPDATE " + DBModel.TABLE + " " +
                        "SET " + DBModel.ID + "=" + item.getId() + "," +
                        DBModel.ID_CAMPAIGN + "=" + item.getIdCampaign() + "," +
                        DBModel.POSITION + "='" + item.getFieldPosition() + "'," +
                        DBModel.LABEL + "='" + item.getFieldLabel() + "'," +
                        DBModel.NAME + "='" + item.getFieldName() + "'," +
                        DBModel.DESCRIPTION + "='" + item.getFieldDescription() + "'," +
                        DBModel.TYPE + "='" + item.getFieldType() + "'," +
                        DBModel.REQUIRED + "='" + item.getFieldRequired() + "'," +
                        DBModel.OPTIONS + "='" + options + "' " +
                        "WHERE " + DBModel.ID + "=" + item.getId();

                database.execSQL(sql);
            }

            c.close();
            close(); // Close DB

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new Result(true, null, from, 0);
        }

        return new Result(false, null, from, 0);

    }

    public synchronized Pair<Result, ArrayList<ItemModel>> getModel (int id) {

        int from = DBVirde.FROM_SELECT_MODEL;
        ArrayList<ItemModel> result = new ArrayList<>();
        String sql = "SELECT * FROM " + DBModel.TABLE +
                " WHERE " + DBModel.ID_CAMPAIGN + "=" + id +
                " ORDER BY " + DBModel.POSITION + " ASC";

        open();

        Cursor c = database.rawQuery(sql, null);

        if (c.moveToFirst()) {

            do {

                result.add(formatItemModelFromDB(c));

            } while (c.moveToNext());

        }

        c.close();
        close();

        return new Pair<>(new Result(false, null, from, 0), result);

    }

    private ItemModel formatItemModelFromDB(Cursor c) {

        ItemModel model = new ItemModel();

        try {
            model.setId(c.getInt(0));
            model.setIdCampaign(c.getInt(1));
            model.setFieldPosition(c.getInt(2));
            model.setFieldLabel(c.getString(3));
            model.setFieldName(c.getString(4));
            model.setFieldDescription(c.getString(5));
            model.setFieldType(c.getString(6));
            boolean required = Boolean.valueOf(c.getString(7));
            model.setFieldRequired((required) ? 1 : 0);
            model.setFieldOptions(URLDecoder.decode(c.getString(8), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return model;

    }

    /**********************/
    /** MODEL_ITEM_VALUE **/
    /**********************/

    public synchronized Result insertModelValueToDB (ArrayList<ItemModelValue> items) {

        int from = DBVirde.FROM_INSERT_MODELITEM;
        int id = getIdForModelValue();

        try {

            int nRows = items.size();

            open();

            for (int i = 0; i < nRows; i++) {

                ItemModelValue item = items.get(i);

                // Options
                String value = "";
                if (item.getValue() != null)
                    value = URLEncoder.encode(item.getValue(), "UTF-8");

                String sql = "INSERT OR REPLACE INTO " + DBModelValue.TABLE + " " +
                             "VALUES (" + id + "," + item.getIdModel() + ",'" + item.getField() + "','" +
                             value + "','" + item.getFieldType() + "'," + item.getOrder() + ",'" + item.isSync() + "')";

                database.execSQL(sql);

            }

            close(); // Close DB

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new Result(true, null, from, 0);
        }

        return new Result(false, null, from, id);

    }

    public synchronized Result updateModelValue (ArrayList<ItemModelValue> items) {

        int from = DBVirde.FROM_UPDATE_MODELITEM;

        try {

            int nRows = items.size();

            for (int i = 0; i < nRows; i++) {

                ItemModelValue item = items.get(i);

                // Comprobar si el valor del modelo existe mediante su ID y Field
                String sql = "SELECT * FROM " + DBModelValue.TABLE + " " +
                        "WHERE " + DBModelValue.ID + " = '" + item.getId() + "' " +
                        "AND " + DBModelValue.FIELD + " = '" + item.getField() + "'";

                open(); // Open DB
                Cursor c = database.rawQuery(sql, null);

                if (c.getCount() == 1) { // Si existe la contribución, update

                    // Value
                    String value = "";
                    if (item.getValue() != null)
                        value = URLEncoder.encode(item.getValue(), "UTF-8");

                    sql = "UPDATE " + DBModelValue.TABLE + " " +
                            "SET " + DBModelValue.ID + "=" + item.getId() + "," +
                            DBModelValue.ID_CAMP + "=" + item.getIdModel() + "," +
                            DBModelValue.FIELD + "='" + item.getField() + "'," +
                            DBModelValue.VALUE + "='" + value + "'," +
                            DBModelValue.ORDER + "=" + item.getOrder() + ", " +
                            DBModelValue.IS_SYNC + "='" + item.isSync() + "' " +
                            "WHERE " + DBModelValue.ID + "=" + item.getId() + " " +
                            "AND " + DBModelValue.FIELD + "='" + item.getField() + "'";

                    database.execSQL(sql);

                }

                c.close();

            }

            close(); // Close DB

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new Result(true, null, from, 0);
        }

        return new Result(false, null, from, 0);

    }

    public synchronized Pair<Result, ArrayList<ItemModelValue>> selectModelValues (int id) {

        int from = DBVirde.FROM_SELECT_MODELITEM;
        ArrayList<ItemModelValue> result = new ArrayList<>();

        String sql;
        if (id == -1) {
            sql = "SELECT * FROM " + DBModelValue.TABLE +
                 " ORDER BY " + DBModelValue.ID + " ASC";
        } else {
            sql = "SELECT * FROM " + DBModelValue.TABLE +
                 " WHERE " + DBModelValue.ID + "=" + id +
                 " ORDER BY " + DBModelValue.ID + " ASC";
        }

        open();

        Cursor c = database.rawQuery(sql, null);

        if (c.moveToFirst()) {

            do {

                result.add(formatItemModelValueFromDB(c));

            } while (c.moveToNext());

        }

        c.close();
        close();

        return new Pair<>(new Result(false, null, from, 0), result);

    }

    private ItemModelValue formatItemModelValueFromDB(Cursor c) {

        ItemModelValue value = new ItemModelValue();

        try {
            value.setId(c.getInt(0));
            value.setIdModel(c.getInt(1));
            value.setField(c.getString(2));
            value.setValue(URLDecoder.decode(c.getString(3), "UTF-8"));
            value.setFieldType(c.getString(4));
            value.setOrder(c.getInt(5));
            value.setIsSync(Boolean.valueOf(c.getString(6)));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return value;

    }

    /**********************/
    /**       GPX        **/
    /**********************/

    public synchronized Result insertGpxToDB (ItemGpx item) {

        int from = DBVirde.FROM_INSERT_GPX;

        try {

            open();

            // Dir gpx
            String dir = "";
            if (item.getDir() != null)
                dir = URLEncoder.encode(item.getDir(), "UTF-8");

            String sql = "INSERT OR REPLACE INTO " + DBGpxContribution.TABLE + " " +
                    "VALUES ('" + item.getId() + "'," + item.getIdServer() + "," + item.getIdCampaign() + ",'" +
                    dir + "','" + parseDateToString("dd/MM/yyyy 'a las' HH:mm", item.getDate()) + "','" + item.isSync() + "')";

            database.execSQL(sql);


            close(); // Close DB

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new Result(true, null, from, 0);
        }

        return new Result(false, null, from, 0);

    }

    public synchronized Result updateGPX (ItemGpx item) {

        int from = DBVirde.FROM_UPDATE_GPX;

        try {

            open(); // Open DB

            // Comprobar si el GPX existe mediante su ID
            String sql = "SELECT * FROM " + DBGpxContribution.TABLE + " " +
                    "WHERE " + DBGpxContribution.ID + " = '" + item.getId() + "'";

            Cursor c = database.rawQuery(sql, null);

            if (c.getCount() == 1) {

                // Dir gpx
                String dir = "";
                if (item.getDir() != null)
                    dir = URLEncoder.encode(item.getDir(), "UTF-8");

                sql = "UPDATE " + DBGpxContribution.TABLE + " " +
                        "SET " + DBGpxContribution.ID_SERVER + "=" + item.getIdServer() + "," +
                        DBGpxContribution.ID_CAMPAIGN + "='" + item.getIdCampaign() + "'," +
                        DBGpxContribution.DIR + "='" + dir + "'," +
                        DBGpxContribution.DATE + "='" + dateToString(item.getDate()) + "'," +
                        DBGpxContribution.IS_SYNC + "='" + item.isSync() + "' " +
                        "WHERE " + DBGpxContribution.ID + "='" + item.getId() + "'";

                database.execSQL(sql);
            }

            c.close();
            close(); // Close DB

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new Result(true, null, from, 0);
        }

        return new Result(false, null, from, 0);

    }

    public synchronized Result updateSyncGPX (ItemGpx item) {

        int from = DBVirde.FROM_UPDATE_SYNC_GPX;

        open(); // Open DB

        // Comprobar si el GPX existe mediante su ID
        String sql = "SELECT * FROM " + DBGpxContribution.TABLE + " " +
                "WHERE " + DBGpxContribution.ID + " = '" + item.getId() + "'";

        Cursor c = database.rawQuery(sql, null);

        if (c.getCount() == 1) {

            sql = "UPDATE " + DBGpxContribution.TABLE + " " +
                    "SET " + DBGpxContribution.IS_SYNC + "='" + item.isSync() + "' " +
                    "WHERE " + DBGpxContribution.ID + "='" + item.getId() + "'";

            database.execSQL(sql);

        }

        c.close();
        close(); // Close DB

        return new Result(false, null, from, 0);

    }

    public synchronized Pair<Result, ArrayList<ItemGpx>> selectGpx (int idServer, String id) {

        int from = DBVirde.FROM_SELECT_GPX;
        ArrayList<ItemGpx> result = new ArrayList<>();

        String sql;
        sql = "SELECT * FROM " + DBGpxContribution.TABLE +
                " WHERE " + DBGpxContribution.ID_SERVER + " = " + idServer +
                " AND " + DBGpxContribution.ID + " = '" + id + "'";

        open();

        Cursor c = database.rawQuery(sql, null);

        if (c.moveToFirst()) {

            do {

                result.add(formatItemGpxFromDB(c));

            } while (c.moveToNext());

        }

        c.close();
        close();

        return new Pair<>(new Result(false, null, from, 0), result);

    }

    public synchronized Pair<Result, ArrayList<ItemSync>> selectGpxsToSync () {

        int from = DBVirde.FROM_SELECT_GPX_TO_SYNC;
        ArrayList<ItemSync> result = new ArrayList<>();

        String sql;
        sql = "SELECT " + DBGpxContribution.TABLE+"."+DBGpxContribution.ID + ", " + DBGpxContribution.DATE + ", " +
                DBCampaign.TITLE + ", " + DBServer.URL + ", " + DBGpxContribution.IS_SYNC + "\n" +
                "FROM " + DBServer.TABLE + ", " + DBCampaign.TABLE + ", " + DBGpxContribution.TABLE + "\n" +
                "WHERE " + DBCampaign.TABLE+"."+DBCampaign.ID_SERVER + " = " + DBServer.TABLE+"."+DBServer.ID + "\n" +
                "AND " + DBGpxContribution.TABLE+"."+DBGpxContribution.ID_SERVER + " = " + DBServer.TABLE+"."+DBServer.ID + "\n" +
                "AND " + DBGpxContribution.TABLE+"."+DBGpxContribution.ID_CAMPAIGN + " = " + DBCampaign.TABLE+"."+DBCampaign.ID + "\n" +
                "ORDER BY " + DBGpxContribution.TABLE+"."+DBGpxContribution.ID + " ASC";

        open();

        Cursor c = database.rawQuery(sql, null);

        if (c.moveToFirst()) {

            do {

                result.add(formatItemGpxFromDBToSync(c));

            } while (c.moveToNext());

        }

        c.close();
        close();

        return new Pair<>(new Result(false, null, from, 0), result);

    }

    private ItemSync formatItemGpxFromDBToSync(Cursor c) {

        ItemSync item = new ItemSync();

        try {

            item.setType(Item.SYNC);
            item.setImgCampaign("");
            item.setIdGpx(c.getString(0));
            item.setDate(c.getString(1));
            item.setTitle(URLDecoder.decode(c.getString(2), "UTF-8"));
            item.setUrl(URLDecoder.decode(c.getString(3), "UTF-8"));
            item.setIsSync(Boolean.valueOf(c.getString(4)));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return item;

    }

    private ItemGpx formatItemGpxFromDB(Cursor c) {

        ItemGpx item = new ItemGpx();

        try {
            item.setId(c.getString(0));
            item.setIdServer(c.getInt(1));
            item.setIdCampaign(c.getInt(2));
            item.setDir(URLDecoder.decode(c.getString(3), "UTF-8"));
            item.setDate(formatDateToString("dd/MM/yyyy 'a las' HH:mm", c.getString(4)));
            item.setSync(Boolean.valueOf(c.getString(5)));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return item;

    }

    /**********************/
    /**      SERVER      **/
    /**********************/

    public synchronized Result insertServerToDB (ItemServer item) {

        int from = DBVirde.FROM_INSERT_SERVER;
        int id = -1;

        try {

            // poner a false el antiguo servidor activo
            if (item.isActive()) {
                Pair<Result, ArrayList<ItemServer>> result = selectActiveServer();
                if (!result.first.isError()) {
                    if (result.second.size() > 0) { // Check first execution
                        result.second.get(0).setActive(false);
                        updateServer(result.second.get(0));
                    }
                }
            }

            open();

            // URL server
            String url = "";
            if (item.getUrl() != null)
                url = URLEncoder.encode(item.getUrl(), "UTF-8");

            // Name server
            String name = "";
            if (item.getName() != null)
                name = URLEncoder.encode(item.getName(), "UTF-8");

            // URL server
            String desc = "";
            if (item.getDescription() != null)
                desc = URLEncoder.encode(item.getDescription(), "UTF-8");

            String sql = "INSERT OR REPLACE INTO " + DBServer.TABLE + " " +
                         "(" + DBServer.TYPE + ", " + DBServer.NAME + ", " + DBServer.DESC + ", " + DBServer.URL + ", " +
                         DBServer.GOOGLE_MAPS_STATIC_KEY + ", " + DBServer.PARSE_API  + ", " + DBServer.PARSE_KEY + ", " + DBServer.ACTIVE + ")" +
                         " VALUES (" + item.getType() + ", '" + name + "', '" + desc + "', '" + url + "', '" +
                         item.getMapsKeys().getApi() + "', '" + item.getParseKeys().getApi() + "', '" + item.getParseKeys().getKey() + "', '" +
                         item.isActive() + "')";

            database.execSQL(sql);

            // Get assigned id
            sql = "SELECT " + DBServer.ID + " FROM " + DBServer.TABLE + " ORDER BY " + DBServer.ID + " DESC LIMIT 1";
            Cursor c = database.rawQuery(sql, null);

            if (c.moveToFirst()) {
                id = c.getInt(0);
            }

            c.close();
            close(); // Close DB

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new Result(true, null, from, 1);
        }

        return new Result(false, null, from, id); // Error code reused

    }

    public synchronized Result updateServer (ItemServer item) {

        int from = DBVirde.FROM_UPDATE_SERVER;

        try {

            // poner a false el antiguo servidor activo
            if (item.isActive()) {
                Pair<Result, ArrayList<ItemServer>> result = selectActiveServer();
                if (!result.first.isError()) {
                    if (result.second.size() > 0) { // Check first execution
                        result.second.get(0).setActive(false);
                        updateServer(result.second.get(0));
                    }
                }
            }

            open();

            // URL server
            String url = "";
            if (item.getUrl() != null)
                url = URLEncoder.encode(item.getUrl(), "UTF-8");

            // Name server
            String name = "";
            if (item.getName() != null)
                name = URLEncoder.encode(item.getName(), "UTF-8");

            // URL server
            String desc = "";
            if (item.getDescription() != null)
                desc = URLEncoder.encode(item.getDescription(), "UTF-8");

            String sql = "UPDATE " + DBServer.TABLE +
                    " SET " + DBServer.TYPE + "=" + item.getType() + ", " +
                    DBServer.NAME + "='" + name + "', " +
                    DBServer.DESC + "='" + desc + "', " +
                    DBServer.URL + "='" + url + "', " +
                    DBServer.GOOGLE_MAPS_STATIC_KEY + "='" + item.getMapsKeys().getApi() + "', " +
                    DBServer.PARSE_API + "='" + item.getParseKeys().getApi() + "', " +
                    DBServer.PARSE_KEY + "='" + item.getParseKeys().getKey() + "', " +
                    DBServer.ACTIVE + "='" + item.isActive() + "' " +
                    " WHERE " + DBServer.ID + "=" + item.getId();

            database.execSQL(sql);

            close();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new Result(true, null, from, item.getId());
        }

        return new Result(false, null, from, item.getId()); // Error code reused

    }

    public synchronized Result deleteServerFromDB (int id) {

        int from = DBVirde.FROM_DELETE_SERVER;

        open();

        String sql = "DELETE FROM " + DBServer.TABLE + " WHERE " + DBServer.ID + "=" + String.valueOf(id);
        database.execSQL(sql);

        close(); // Close DB

        return new Result(false, null, from, 1);

    }


    public synchronized Pair<Result, ArrayList<ItemServer>> selectServers () {

        int from = DBVirde.FROM_SELECT_SERVERS;
        ArrayList<ItemServer> result = new ArrayList<>();
        String sql = "SELECT * FROM " + DBServer.TABLE +
                    " ORDER BY " + DBServer.ID + " ASC";

        open();

        Cursor c = database.rawQuery(sql, null);

        if (c.moveToFirst()) {

            do {

                result.add(formatItemServerFromDB(c));

            } while (c.moveToNext());

        }

        c.close();
        close();

        return new Pair<>(new Result(false, null, from, 0), result);

    }

    public synchronized Pair<Result, ArrayList<ItemServer>> selectActiveServer () {

        int from = DBVirde.FROM_SELECT_ACTIVE_SERVER;
        ArrayList<ItemServer> result = new ArrayList<>();
        String sql = "SELECT * FROM " + DBServer.TABLE +
                    " WHERE " + DBServer.ACTIVE + "='true'";

        open();

        Cursor c = database.rawQuery(sql, null);

        if (c.moveToFirst()) {
            result.add(formatItemServerFromDB(c));
        }

        c.close();
        close();

        return new Pair<>(new Result(false, null, from, 0), result);

    }

    private ItemServer formatItemServerFromDB(Cursor c) {

        ItemServer item = new ItemServer();

        try {
            item.setId(c.getInt(0));
            item.setType(c.getInt(1));
            item.setName(URLDecoder.decode(c.getString(2), "UTF-8"));
            item.setDescription(URLDecoder.decode(c.getString(3), "UTF-8"));
            item.setUrl(URLDecoder.decode(c.getString(4), "UTF-8"));
            item.setMapsKeys(new ItemGoogleMaps(c.getString(5)));
            item.setParseKeys(new ItemParse(c.getString(6), c.getString(7)));
            item.setActive(Boolean.valueOf(c.getString(8)));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return item;

    }

    /**
     * Genera un número entero obtenido de SharedPreferences para asignárselo como id a los
     * valores de una contribución
     * @return Integer id
     */
    private int getIdForModelValue () {

        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.DBPreferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String key = context.getString(R.string.nModelValue);
        int nKey;

        if (!sharedPref.contains(key)) {
            editor.putInt(key, 0);
            nKey = 0;
        } else {
            nKey = sharedPref.getInt(key, -1) + 1;
            editor.putInt(key, nKey);
        }

        editor.apply();

        return nKey;

    }

    /**********************/
    /**    DICTIONARY    **/
    /**********************/

    public synchronized Result insertDictionaryToDB (Dictionary dictionary) {

        int from = DBVirde.FROM_INSERT_DICTIONARY;

        try {

            open();

            // Name dictionary
            String name = "";
            if (dictionary.getName() != null)
                name = URLEncoder.encode(dictionary.getName(), "UTF-8");

            // Description dictionary
            String description = "";
            if (dictionary.getDescription() != null)
                description = URLEncoder.encode(dictionary.getDescription(), "UTF-8");

            String sql = "INSERT OR REPLACE INTO " + DBDictionary.TABLE + " " +
                    " VALUES (" + dictionary.getCode() + ", " + dictionary.getIdServer() + ", '" + name + "', '" +
                    description + "')";

            database.execSQL(sql);

            for (ItemDictionary item : dictionary.getTerms()) {

                // Name term
                name = "";
                if (dictionary.getName() != null)
                    name = URLEncoder.encode(item.getName(), "UTF-8");

                // Description term
                description = "";
                if (dictionary.getDescription() != null)
                    description = URLEncoder.encode(item.getDescription(), "UTF-8");

                sql = "INSERT OR REPLACE INTO " + DBDictionaryTerm.TABLE + " " +
                        " VALUES (" + dictionary.getCode() + ", '" + name + "', '" + description +
                        "', " + item.getCode() + ")";

                database.execSQL(sql);

            }

            close(); // Close DB

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new Result(true, null, from, 1);
        }

        return new Result(false, null, from, 0);

    }

    public synchronized Pair<Result, ArrayList<Integer>> checkIfDictionaryExists (int code) {

        int from = DBVirde.FROM_CHECK_IF_DICTIONARY_EXISTS;
        ArrayList<Integer> result = new ArrayList<>();

        open();

        String sql = "SELECT * FROM " + DBDictionary.TABLE +
                " WHERE " + DBDictionary.ID_DICTIONARY +  " = " + code;


        Cursor c = database.rawQuery(sql, null);

        if (c.getCount() == 0) {
            result.add(0);
        } else {
            result.add(1);
        }

        c.close();
        close();

        return new Pair<>(new Result(false, null, from, 0), result);

    }

    public synchronized Pair<Result, ArrayList<Dictionary>> selectDictionary (int code) {

        int from = DBVirde.FROM_SELECT_DICTIONARY;
        ArrayList<Dictionary> result = new ArrayList<>();

        String sql = "SELECT " + DBDictionary.TABLE +"."+DBDictionary.ID_DICTIONARY + ", " +
                DBDictionary.TABLE +"."+DBDictionary.NAME + ", " + DBDictionary.TABLE +"."+DBDictionary.DESCRIPTION + ", " +
                DBDictionaryTerm.TABLE +"."+DBDictionaryTerm.NAME + ", " + DBDictionaryTerm.TABLE +"."+DBDictionaryTerm.DESCRIPTION + ", " +
                DBDictionaryTerm.TABLE +"."+DBDictionaryTerm.CODE + "\n" +
                "FROM " + DBDictionary.TABLE + ", " + DBDictionaryTerm.TABLE + ", " + DBServer.TABLE + "\n" +
                "WHERE " + DBDictionary.TABLE +"."+DBDictionary.ID_DICTIONARY + " = " + DBDictionaryTerm.TABLE +"."+DBDictionaryTerm.ID_DICTIONARY + "\n" +
                "AND " + DBDictionary.TABLE +"."+DBDictionary.ID_SERVER + " = " + DBServer.TABLE +"."+DBServer.ID + "\n" +
                "ORDER BY " + DBDictionaryTerm.TABLE +"."+DBDictionaryTerm.CODE;

        open();

        Cursor c = database.rawQuery(sql, null);

        if (c.moveToFirst()) {
            result.add(formatDictionaryFromDB(c));
        }

        c.close();
        close();

        return new Pair<>(new Result(false, null, from, 0), result);

    }

    private Dictionary formatDictionaryFromDB(Cursor c) {

        Dictionary dictionary = new Dictionary();

        try {

            dictionary.setCode(c.getInt(0));
            dictionary.setName(URLDecoder.decode(c.getString(1), "UTF-8"));
            dictionary.setDescription(URLDecoder.decode(c.getString(2), "UTF-8"));
            dictionary.setTerms(new ArrayList<ItemDictionary>());

            do {

                ItemDictionary item = new ItemDictionary();
                item.setName(URLDecoder.decode(c.getString(3), "UTF-8"));
                item.setDescription(URLDecoder.decode(c.getString(4), "UTF-8"));
                item.setCode(c.getString(5));
                dictionary.getTerms().add(item);

            } while (c.moveToNext());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return dictionary;

    }

    /** UTIL **/
    private String dateToString(Date date) {

        if (date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd", Locale.getDefault());

            return dateFormat.format(date);
        } else {
            return "";
        }

    }

    private String parseDateToString (String formatSt, Date date) {

        if (date != null)
            return DateFormat.format(formatSt, date).toString();
        else
            return "";

    }

    private Date formatDateToString (String formatSt, String sDate) {

        SimpleDateFormat format = new SimpleDateFormat(formatSt, Locale.getDefault());
        Date date = null;

        try {
            date = format.parse(sDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;

    }

    private Date stringToDate (String sDate) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = null;

        try {
            date = format.parse(sDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;

    }

    private String getActiveServer () {

        SharedPreferences sharedPref = context.getSharedPreferences("userPreferences", Context.MODE_PRIVATE);
        return sharedPref.getString("serverUrl", "");

    }

    private int getIdActiveServer () {

        SharedPreferences sharedPref = context.getSharedPreferences("userPreferences", Context.MODE_PRIVATE);
        return sharedPref.getInt("idServer", -1);

    }

}
