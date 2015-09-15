package info.si2.iista.volunteernetworks.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import info.si2.iista.volunteernetworks.apiclient.ItemCampaign;
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
    private boolean fromInsertToUpdate;

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
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Result insertCampaignsToDB (ArrayList<ItemCampaign> items) {

        int from = DBVirde.FROM_INSERT_CAMPAIGNS;
        String activeServer = getActiveServer();

        try {

            int nRows = items.size();
            open(); // Open DB

            for (int i = 0; i < nRows; i++) {

                // Comprobar si la campaña existe mediante su ID
                String sql = "SELECT * FROM " + DBCampaign.TABLE_CAMPAIGNS + " " +
                        "WHERE " + DBCampaign.ID + " = '" + items.get(i).getId() + "'";

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



                    sql = "INSERT OR REPLACE INTO " + DBCampaign.TABLE_CAMPAIGNS + " " +
                            "VALUES (" + item.getId() + "," + item.getType() + ",'" + item.getHeaderColor() + "','" +
                            ecodedTitle + "','" + encodedShortDesc + "','" + encodedDesc + "','" +
                            econdedScope + "','" + item.getImage() + "','" + item.isSuscribe() + "','" +
                            dateToString(item.getDateStart()) + "','" + dateToString(item.getDateEnd()) + "','" +
                            activeServer + "','" + true + "')";

                    database.execSQL(sql);

                } else { // Si existe la campaña, UPDATE
                    fromInsertToUpdate = true;
                    updateCampaign(items.get(i));
                }

                c.close();

            }

            // Not active campaigns
            disableCampaigns(items);

            close(); // Close DB

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new Result(true, null, from, 0);
        }

        return new Result(false, null, from, 0);

    }

    public Result updateCampaign (ItemCampaign item) {

        int from = DBVirde.FROM_UPDATE_CAMPAIGN;

        try {

            if (!fromInsertToUpdate) {
                open(); // Open DB
            }

            // Comprobar si la campaña existe mediante su ID
            String sql = "SELECT * FROM " + DBCampaign.TABLE_CAMPAIGNS + " " +
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

                sql = "UPDATE " + DBCampaign.TABLE_CAMPAIGNS + " " +
                        "SET " + DBCampaign.ID + "=" + item.getId() + "," +
                        DBCampaign.TYPE + "=" + item.getType() + "," +
                        DBCampaign.COLOR + "='" + item.getHeaderColor() + "'," +
                        DBCampaign.TITLE + "='" + ecodedTitle + "'," +
                        DBCampaign.SHORT_DESCRIPTION + "='" + encodedShortDesc + "'," +
                        DBCampaign.DESCRIPTION + "='" + encodedDesc + "'," +
                        DBCampaign.SCOPE + "='" + econdedScope + "'," +
                        DBCampaign.IMAGE + "='" + item.getImage() + "'," +
                        DBCampaign.IS_SUSCRIBE + "='" + item.isSuscribe() + "'," +
                        DBCampaign.DATE_START + "='" + dateToString(item.getDateStart()) + "'," +
                        DBCampaign.DATE_END + "='" + dateToString(item.getDateEnd()) + "' " +
                        "WHERE " + DBCampaign.ID + "=" + item.getId();

                database.execSQL(sql);
            }

            c.close();
            if (!fromInsertToUpdate) {
                close(); // Close DB
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new Result(true, null, from, 0);
        }

        return new Result(false, null, from, 0);

    }

    public Pair<Result, ArrayList<ItemCampaign>> getCampaigns () {

        int from = DBVirde.FROM_SELECT_CAMPAIGNS;
        ArrayList<ItemCampaign> result = new ArrayList<>();
        String sql = "SELECT * FROM " + DBCampaign.TABLE_CAMPAIGNS +
                    " WHERE " + DBCampaign.IS_ACTIVE + "='true'";

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

    public Pair<Result, ArrayList<ItemCampaign>> getCampaign (int id) {

        int from = DBVirde.FROM_SELECT_CAMPAIGN;
        ArrayList<ItemCampaign> result = new ArrayList<>();
        String sql = "SELECT * FROM " + DBCampaign.TABLE_CAMPAIGNS +
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

    public Pair<Result, ArrayList<ItemCampaign>> getCampaignsFromID (int idCampaign) {

        int from = DBVirde.FROM_SELECT_CAMPAIGNS_FROM_ID;
        ArrayList<ItemCampaign> result = new ArrayList<>();
        String sql = "SELECT * FROM " + DBCampaign.TABLE_CAMPAIGNS + " " +
                     "WHERE " + DBCampaign.ID + ">" + String.valueOf(idCampaign) + " " +
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
    private void disableCampaigns (ArrayList<ItemCampaign> items) {

        String ids = "";

        for (ItemCampaign item : items) {
            ids += String.valueOf(item.getId()) + ",";
        }
        ids = ids.substring(0, ids.length()-1);

        String sql = "UPDATE " + DBCampaign.TABLE_CAMPAIGNS +
                " SET " + DBCampaign.IS_ACTIVE + "='false'" +
                " WHERE " + DBCampaign.ID + " NOT IN (" + ids + ")";

        database.execSQL(sql);

    }

    private ItemCampaign formatItemCampaignFromDB(Cursor c) {

        ItemCampaign campaign = new ItemCampaign();

        try {

            campaign.setId(c.getInt(0));
            campaign.setType(c.getInt(1));
            campaign.setHeaderColor(c.getString(2));
            campaign.setTitle(URLDecoder.decode(c.getString(3), "UTF-8"));
            campaign.setShortDescription(URLDecoder.decode(c.getString(4), "UTF-8"));
            campaign.setDescription(URLDecoder.decode(c.getString(5), "UTF-8"));
            campaign.setScope(URLDecoder.decode(c.getString(6), "UTF-8"));
            campaign.setImage(c.getString(7));
            campaign.setIsSuscribe(Boolean.valueOf(c.getString(8)));
            campaign.setDateStart(stringToDate(c.getString(9)));
            campaign.setDateEnd(stringToDate(c.getString(10)));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return campaign;

    }

    private String dateToString(Date date) {

        if (date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd", Locale.getDefault());

            return dateFormat.format(date);
        } else {
            return "";
        }

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
        return sharedPref.getString("server", "");

    }

}
