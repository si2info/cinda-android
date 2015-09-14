package info.si2.iista.volunteernetworks.database;

import android.content.Context;
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

    public synchronized static void createInstance (Context c) {
        if (dbHelper == null) {
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

        try {

            int nRows = items.size();
            open(); // Open DB

            for (int i = 0; i < nRows; i++) {

                // Comprobar si la campaña existe mediante su ID
                String sql = "SELECT * FROM " + DBCampaign.TABLE_CAMPAIGNS + " " +
                        "WHERE " + DBCampaign.ID + " = '" + items.get(i).getId() + "'";

                Cursor c = database.rawQuery(sql, null);

                if (c.getCount() == 0) { // Si no existe la campaña, añadir

                    ItemCampaign item = items.get(i);

                    String ecodedTitle = URLEncoder.encode(item.getTitle(), "UTF-8");
//                    String encodedShortDesc = URLEncoder.encode(item.getShortDescription(), "UTF-8");
                    String encodedShortDesc = "";
                    String encodedDesc = URLEncoder.encode(item.getDescription(), "UTF-8");

                    String econdedScope = "";
                    if (item.getScope() != null)
                        econdedScope = URLEncoder.encode(item.getScope(), "UTF-8");



                    sql = "INSERT OR REPLACE INTO " + DBCampaign.TABLE_CAMPAIGNS + " " +
                            "VALUES (" + item.getId() + "," + item.getType() + ",'" + item.getHeaderColor() + "','" +
                            ecodedTitle + "','" + encodedShortDesc + "','" + encodedDesc + "','" +
                            econdedScope + "','" + item.getImage() + "','" + item.isSuscribe() + "','" +
                            dateToString(item.getDateStart()) + "','" + dateToString(item.getDateEnd()) + "','" + item.isLoaded() + "')";

                    database.execSQL(sql);
                }

                c.close();

            }

            close(); // Close DB

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new Result(true, null, DBVirde.FROM_INSERT_CAMPAIGNS, 0);
        }

        return new Result(false, null, DBVirde.FROM_INSERT_CAMPAIGNS, 0);

    }

    public Result updateCampaign (ItemCampaign item) {

        try {

            open(); // Open DB

                // Comprobar si la campaña existe mediante su ID
                String sql = "SELECT * FROM " + DBCampaign.TABLE_CAMPAIGNS + " " +
                        "WHERE " + DBCampaign.ID + " = '" + item.getId() + "'";

                Cursor c = database.rawQuery(sql, null);

                if (c.getCount() == 1) { // Si no existe la campaña, añadir

                    String ecodedTitle = URLEncoder.encode(item.getTitle(), "UTF-8");
                    String encodedShortDesc = URLEncoder.encode(item.getShortDescription(), "UTF-8");
                    String encodedDesc = URLEncoder.encode(item.getDescription(), "UTF-8");
                    String econdedScope = URLEncoder.encode(item.getScope(), "UTF-8");

                    sql = "UPDATE " + DBCampaign.TABLE_CAMPAIGNS + " " +
                            "SET " + DBCampaign.ID + "=" + item.getId() + "," +
                            DBCampaign.TYPE + "=" + item.getType() + ",'" +
                            DBCampaign.COLOR + "=" + item.getHeaderColor() + "','" +
                            DBCampaign.TITLE + "=" + ecodedTitle + "','" +
                            DBCampaign.SHORT_DESCRIPTION + "=" + encodedShortDesc + "','" +
                            DBCampaign.DESCRIPTION + "=" + encodedDesc + "','" +
                            DBCampaign.SCOPE + "=" + econdedScope + "','" +
                            DBCampaign.IMAGE + "=" + item.getImage() + "','" +
                            DBCampaign.IS_SUSCRIBE + "=" + item.isSuscribe() + "','" +
                            DBCampaign.DATE_START + "=" + dateToString(item.getDateStart()) + "','" +
                            DBCampaign.DATE_END + "=" + dateToString(item.getDateEnd()) + "','" +
                            DBCampaign.LOADED + "=" + item.isLoaded() + "')";

                    database.execSQL(sql);
                }

                c.close();

            close(); // Close DB

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new Result(true, null, DBVirde.FROM_UPDATE_CAMPAIGN, 0);
        }

        return new Result(false, null, DBVirde.FROM_UPDATE_CAMPAIGN, 0);

    }

    public Pair<Result, ArrayList<ItemCampaign>> getCampaigns () {

        int from = DBVirde.FROM_SELECT_CAMPAIGNS;
        ArrayList<ItemCampaign> result = new ArrayList<>();
        String sql = "SELECT * FROM " + DBCampaign.TABLE_CAMPAIGNS;

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
            campaign.setLoaded(Boolean.valueOf(c.getString(11)));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return campaign;

    }

    private String dateToString(Date date) {

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        return dateFormat.format(date);

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

}
