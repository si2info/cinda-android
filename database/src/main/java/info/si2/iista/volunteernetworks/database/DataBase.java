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
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "Virde.db";

    private static final String PRIMARY_KEY = " PRIMARY KEY";
    private static final String AUTOINCREMENT = " AUTOINCREMENT";
    private static final String TYPE_INT = " INTEGER ";
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

    private static final String SQL_CREATE_MODEL =
            "CREATE TABLE " + DBModel.TABLE_MODEL + " (" +
                    DBModel.ID + TYPE_INT + PRIMARY_KEY + COMMA_SEP +
                    DBModel.ID_CAMPAIGN + TYPE_INT + COMMA_SEP +
                    DBModel.POSITION + TYPE_INT + COMMA_SEP +
                    DBModel.LABEL + TYPE_TEXT + COMMA_SEP +
                    DBModel.NAME + TYPE_TEXT + COMMA_SEP +
                    DBModel.DESCRIPTION + TYPE_TEXT + COMMA_SEP +
                    DBModel.TYPE + TYPE_TEXT + COMMA_SEP +
                    DBModel.REQUIRED + TYPE_BOOLEAN + COMMA_SEP +
                    DBModel.OPTIONS + TYPE_TEXT + COMMA_SEP +
                    "FOREIGN KEY("+ DBModel.ID + ") REFERENCES " + DBCampaign.TABLE_CAMPAIGNS + "(" + DBCampaign.ID + ")" +
                    " );";

    private static final String SQL_CREATE_MODEL_VALUE =
            "CREATE TABLE " + DBModelValue.TABLE_MODEL_VALUE + " (" +
                    DBModelValue.ID + TYPE_INT + COMMA_SEP +
                    DBModelValue.ID_CAMP + TYPE_INT + COMMA_SEP +
                    DBModelValue.FIELD + TYPE_TEXT + COMMA_SEP +
                    DBModelValue.VALUE + TYPE_TEXT + COMMA_SEP +
                    DBModelValue.ORDER + TYPE_INT + COMMA_SEP +
                    DBModelValue.IS_SYNC + TYPE_BOOLEAN + COMMA_SEP +
                    PRIMARY_KEY + "(" + DBModelValue.ID + "," + DBModelValue.FIELD + ")" + COMMA_SEP +
                    "FOREIGN KEY("+ DBModelValue.ID_CAMP + ") REFERENCES " + DBModel.TABLE_MODEL + "(" + DBModel.ID + ")" +
                    ");";

    private static final String SQL_CREATE_SERVER =
            "CREATE TABLE " + DBServer.TABLE_SERVER + " (" +
                    DBServer.ID + TYPE_INT + PRIMARY_KEY + COMMA_SEP +
                    DBServer.TYPE + TYPE_INT + COMMA_SEP +
                    DBServer.URL + TYPE_TEXT + COMMA_SEP +
                    DBServer.DESC + TYPE_TEXT + COMMA_SEP +
                    DBServer.ACTIVE + TYPE_BOOLEAN +
                    ");";

    private static final String SQL_DELETE_CAMPAIGNS =
            "DROP TABLE IF EXISTS " + DBCampaign.TABLE_CAMPAIGNS;

    private static final String SQL_DELETE_MODELS =
            "DROP TABLE IF EXISTS " + DBModel.TABLE_MODEL;

    private static final String SQL_DELETE_MODELS_ITEM =
            "DROP TABLE IF EXISTS " + DBModelValue.TABLE_MODEL_VALUE;

    private static final String SQL_DELETE_SERVER =
            "DROP TABLE IF EXISTS " + DBServer.TABLE_SERVER;

    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CAMPAIGNS);
        db.execSQL(SQL_CREATE_MODEL);
        db.execSQL(SQL_CREATE_MODEL_VALUE);
        db.execSQL(SQL_CREATE_SERVER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_CAMPAIGNS);
        db.execSQL(SQL_DELETE_MODELS);
        db.execSQL(SQL_DELETE_MODELS_ITEM);
        db.execSQL(SQL_DELETE_SERVER);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
