package info.si2.iista.volunteernetworks.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 11/9/15
 * Project: Virde
 */
public class DataBase extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Virde.db";

    private static final String PRIMARY_KEY = " PRIMARY KEY";
    private static final String TYPE_INT = " INTEGER";
    private static final String TYPE_TEXT = " TEXT";
    private static final String TYPE_BOOLEAN = " BOOLEAN";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_CAMPAIGNS =
            "CREATE TABLE " + DBCampaign.TABLE_CAMPAIGNS + " (" +
                    DBCampaign.ID + TYPE_INT + PRIMARY_KEY + COMMA_SEP +
                    DBCampaign.TYPE + TYPE_INT + COMMA_SEP +
                    DBCampaign.COLOR + TYPE_TEXT + COMMA_SEP +
                    DBCampaign.TITLE + TYPE_TEXT + COMMA_SEP +
                    DBCampaign.SHORT_DESCRIPTION + TYPE_TEXT + COMMA_SEP +
                    DBCampaign.DESCRIPTION + TYPE_TEXT + COMMA_SEP +
                    DBCampaign.SCOPE + TYPE_TEXT + COMMA_SEP +
                    DBCampaign.IMAGE + TYPE_TEXT + COMMA_SEP +
                    DBCampaign.IS_SUSCRIBE + TYPE_BOOLEAN + COMMA_SEP +
                    DBCampaign.DATE_START + TYPE_TEXT + COMMA_SEP +
                    DBCampaign.DATE_END + TYPE_TEXT + COMMA_SEP +
                    DBCampaign.SERVER + TYPE_TEXT + COMMA_SEP +
                    DBCampaign.IS_ACTIVE + TYPE_BOOLEAN + " );";

    private static final String SQL_DELETE_CAMPAIGNS =
            "DROP TABLE IF EXISTS " + DBCampaign.TABLE_CAMPAIGNS;

    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CAMPAIGNS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_CAMPAIGNS);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
